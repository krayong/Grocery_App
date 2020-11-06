package com.krayong.groceryapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krayong.groceryapp.MainActivity;
import com.krayong.groceryapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ItemDetailsFragment extends Fragment {
	
	private MainActivity mMainActivity;
	
	private ArrayList<String> foodItemName = new ArrayList<>();
	private ArrayList<String> foodItemPrice = new ArrayList<>();
	private ArrayList<Uri> foodItemImage = new ArrayList<>();
	
	private int selectedIndex = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_item_details, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMainActivity = (MainActivity) requireActivity();
		
		checkConnection(this);
		
		view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainActivity.mNavController.navigateUp();
			}
		});
		
		view.findViewById(R.id.food_add_cart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedIndex != -1) {
					FirebaseDatabase.getInstance().getReference().child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot snapshot) {
							if (snapshot.exists()) {
								if (snapshot.hasChild(foodItemName.get(selectedIndex).toLowerCase())) {
									int quantity = snapshot.child(foodItemName.get(selectedIndex).toLowerCase()).getValue(Integer.class);
									FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItemName.get(selectedIndex).toLowerCase()).setValue(quantity + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void aVoid) {
											MainActivity activity = (MainActivity) requireActivity();
											activity.mNavController.navigate(R.id.action_nav_item_details_to_nav_cart);
										}
									});
								} else {
									FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItemName.get(selectedIndex).toLowerCase()).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void aVoid) {
											MainActivity activity = (MainActivity) requireActivity();
											activity.mNavController.navigate(R.id.action_nav_item_details_to_nav_cart);
										}
									});
								}
							} else {
								FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItemName.get(selectedIndex).toLowerCase()).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
									@Override
									public void onSuccess(Void aVoid) {
										MainActivity activity = (MainActivity) requireActivity();
										activity.mNavController.navigate(R.id.action_nav_item_details_to_nav_cart);
									}
								});
							}
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError error) {
						
						}
					});
				}
			}
		});
		
		FirebaseDatabase.getInstance().getReferenceFromUrl("https://grocery-app-f84ee.firebaseio.com/foodItems")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot snapshot) {
						if (snapshot.exists() && snapshot.hasChildren()) {
							for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
								String nameAndPrice = dataSnapshot.getKey();
								foodItemName.add(nameAndPrice.split(",")[0].substring(0, 1).toUpperCase() + nameAndPrice.split(",")[0].substring(1));
								foodItemPrice.add(nameAndPrice.split(",")[1] + "/KG");
								foodItemImage.add(Uri.parse((String) dataSnapshot.getValue()));
							}
							
							selectedIndex = new Random().nextInt(foodItemName.size());
							
							Picasso.get().load(foodItemImage.get(selectedIndex))
									.into((ImageView) view.findViewById(R.id.food_image));
							
							TextView foodName = view.findViewById(R.id.food_name);
							TextView foodPrice = view.findViewById(R.id.food_price);
							
							foodName.setText(foodItemName.get(selectedIndex));
							foodPrice.setText(foodItemPrice.get(selectedIndex));
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError error) {
					
					}
				});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mMainActivity.mToolbar.setVisibility(View.GONE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mMainActivity.mToolbar.setVisibility(View.VISIBLE);
	}
	
	public void checkConnection(final Fragment fragment) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager) fragment.requireActivity().getSystemService(CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragment.requireActivity())
					.setMessage("Please connect to the internet to proceed further")
					.setCancelable(false)
					.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							fragment.requireActivity().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Toast.makeText(fragment.requireActivity(), "Some functions might not work properly without internet", Toast.LENGTH_SHORT).show();
						}
					})
					.setNeutralButton("Reload", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkConnection(fragment);
						}
					});
			alertDialog.show();
		}
	}
}