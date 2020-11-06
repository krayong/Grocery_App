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
import com.krayong.groceryapp.CartAdapter;
import com.krayong.groceryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CartFragment extends Fragment implements CartAdapter.CartItemClickListener {
	
	private ArrayList<String> cartItems = new ArrayList<>();
	private HashMap<String, Integer> cartItemPrices = new HashMap<>();
	private ArrayList<Integer> cartItemQuantities = new ArrayList<>();
	private HashMap<String, Uri> cartItemImages = new HashMap<>();
	
	private RecyclerView cartRv;
	private TextView totalCostTv;
	private CartAdapter cartAdapter;
	private ImageView emptyCart;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_cart, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cartItems.size() > 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
					builder.setTitle("Clear Cart");
					builder.setMessage("Are you sure you want to clear your cart?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							FirebaseDatabase.getInstance().getReference().child("cartItems").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
								@Override
								public void onSuccess(Void aVoid) {
									cartItems = new ArrayList<>();
									cartItemImages = new HashMap<>();
									cartItemPrices = new HashMap<>();
									cartItemQuantities = new ArrayList<>();
									
									cartRv.setAdapter(null);
									totalCostTv.setText("");
									emptyCart.setVisibility(View.VISIBLE);
								}
							});
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					builder.show();
				}
			}
		});
		
		checkConnection(this);
		
		cartRv = view.findViewById(R.id.cart_rv);
		totalCostTv = view.findViewById(R.id.total_cost);
		emptyCart = view.findViewById(R.id.cart_empty);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		FirebaseDatabase.getInstance().getReference().child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists() && snapshot.hasChildren()) {
					for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
						cartItems.add(dataSnapshot.getKey());
						cartItemQuantities.add(dataSnapshot.getValue(Integer.class));
					}
					
					FirebaseDatabase.getInstance().getReference().child("foodPrices").addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot snapshot) {
							if (snapshot.exists() && snapshot.hasChildren()) {
								for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
									cartItemPrices.put(dataSnapshot.getKey(), dataSnapshot.getValue(Integer.class));
								}
								
								FirebaseDatabase.getInstance().getReference().child("foodItems").addValueEventListener(new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot snapshot) {
										if (snapshot.exists() && snapshot.hasChildren()) {
											for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
												cartItemImages.put(dataSnapshot.getKey().split(",")[0], Uri.parse(dataSnapshot.getValue(String.class)));
											}
											if (getActivity() != null) {
												cartAdapter = new CartAdapter(requireActivity(), CartFragment.this, cartItems, cartItemPrices, cartItemQuantities, cartItemImages);
												cartRv.setLayoutManager(new LinearLayoutManager(requireContext()));
												cartRv.setAdapter(cartAdapter);
												
												if (cartItems.size() == 0) emptyCart.setVisibility(View.VISIBLE);
												else emptyCart.setVisibility(View.INVISIBLE);
												
												int totalPrice = 0;
												for (int i = 0; i < cartItems.size(); i++) {
													String itemName = cartItems.get(i);
													totalPrice += cartItemPrices.get(itemName) * cartItemQuantities.get(i);
												}
												totalCostTv.setText("Total Cost: Rs " + totalPrice);
											}
										} else {
											emptyCart.setVisibility(View.VISIBLE);
										}
									}
									
									@Override
									public void onCancelled(@NonNull DatabaseError error) {
									
									}
								});
							} else {
								emptyCart.setVisibility(View.VISIBLE);
							}
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError error) {
						
						}
					});
				} else {
					emptyCart.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			
			}
		});
	}
	
	@Override
	public void add(int position) {
		int quantity = cartItemQuantities.get(position) + 1;
		cartItemQuantities.set(position, quantity);
		FirebaseDatabase.getInstance().getReference().child("cartItems").child(cartItems.get(position)).setValue(quantity);
		cartAdapter.notifyItemChanged(position);
		
		int totalPrice = 0;
		for (int i = 0; i < cartItems.size(); i++) {
			String itemName = cartItems.get(i);
			totalPrice += cartItemPrices.get(itemName) * cartItemQuantities.get(i);
		}
		totalCostTv.setText("Total Cost: Rs " + totalPrice);
	}
	
	@Override
	public void remove(final int position) {
		if (cartItemQuantities.get(position) == 1) {
			final String foodItem = cartItems.get(position);
			
			FirebaseDatabase.getInstance().getReference().child("cartItems").child(cartItems.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
				@Override
				public void onSuccess(Void aVoid) {
					cartItemQuantities.remove(position);
					cartItems.remove(position);
					cartItemImages.remove(foodItem);
					cartItemPrices.remove(foodItem);
					
					cartAdapter.notifyDataSetChanged();
					
					if (cartItems.size() == 0) emptyCart.setVisibility(View.VISIBLE);
					else emptyCart.setVisibility(View.INVISIBLE);
					
					int totalPrice = 0;
					for (int i = 0; i < cartItems.size(); i++) {
						String itemName = cartItems.get(i);
						totalPrice += cartItemPrices.get(itemName) * cartItemQuantities.get(i);
					}
					totalCostTv.setText("Total Cost: Rs " + totalPrice);
				}
			});
		} else {
			int quantity = cartItemQuantities.get(position) - 1;
			cartItemQuantities.set(position, quantity);
			FirebaseDatabase.getInstance().getReference().child("cartItems").child(cartItems.get(position)).setValue(quantity);
			cartAdapter.notifyItemChanged(position);
			
			int totalPrice = 0;
			for (int i = 0; i < cartItems.size(); i++) {
				String itemName = cartItems.get(i);
				totalPrice += cartItemPrices.get(itemName) * cartItemQuantities.get(i);
			}
			totalCostTv.setText("Total Cost: Rs " + totalPrice);
		}
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