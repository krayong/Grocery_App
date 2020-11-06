package com.krayong.groceryapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krayong.groceryapp.CategoriesAdapter;
import com.krayong.groceryapp.FoodItemAdapter;
import com.krayong.groceryapp.FoodPhotoAdapter;
import com.krayong.groceryapp.MainActivity;
import com.krayong.groceryapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class HomeFragment extends Fragment implements CategoriesAdapter.CategoryClickListener, FoodItemAdapter.FoodItemClickListener {
	private static final String TAG = "HomeFragment";
	
	private RecyclerView categoriesRv;
	
	private ArrayList<Uri> mFoodPhotos = new ArrayList<>();
	private ArrayList<String> categoriesName = new ArrayList<>();
	private ArrayList<Uri> categoriesImage = new ArrayList<>();
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		checkConnection(this);
		
		MainActivity activity = (MainActivity) requireActivity();
		activity.mBottomAppBar.bringToFront();
		
		categoriesRv = requireActivity().findViewById(R.id.home_main_rv);
		categoriesRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
		
		loadPhotos();
		
		loadCategories();
		
		TabLayout tabLayout = view.findViewById(R.id.tab_layout);
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				Log.d(TAG, "onTabSelected: " + tab.getPosition());
				if (tab.getPosition() == 0) {
					loadCategories();
				} else {
					loadItems();
				}
			}
			
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			
			}
			
			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			
			}
		});
	}
	
	private void loadItems() {
		categoriesRv.invalidate();
		
		FoodItemAdapter foodItemAdapter = new FoodItemAdapter(requireActivity(), this);
		categoriesRv.setAdapter(foodItemAdapter);
	}
	
	private void loadCategories() {
		categoriesRv.invalidate();
		
		DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://grocery-app-f84ee.firebaseio.com/categories");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists() && snapshot.hasChildren()) {
					for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
						categoriesName.add(dataSnapshot.getKey());
						categoriesImage.add(Uri.parse((String) dataSnapshot.getValue()));
					}
					CategoriesAdapter categoriesAdapter = new CategoriesAdapter(requireActivity(), HomeFragment.this, categoriesName, categoriesImage);
					categoriesRv.setAdapter(categoriesAdapter);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			
			}
		});
	}
	
	private void loadPhotos() {
		final StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://grocery-app-f84ee.appspot.com/FoodPhotos");
		reference.child("fruit1.jpg").getDownloadUrl()
				.addOnSuccessListener(new OnSuccessListener<Uri>() {
					@Override
					public void onSuccess(Uri uri) {
						mFoodPhotos.add(uri);
						reference.child("fruit2.jpg").getDownloadUrl()
								.addOnSuccessListener(new OnSuccessListener<Uri>() {
									@Override
									public void onSuccess(Uri uri) {
										mFoodPhotos.add(uri);
										reference.child("fruit3.jpg").getDownloadUrl()
												.addOnSuccessListener(new OnSuccessListener<Uri>() {
													@Override
													public void onSuccess(Uri uri) {
														mFoodPhotos.add(uri);
														if (getActivity() != null) {
															RecyclerView foodPhotosRv = requireActivity().findViewById(R.id.food_photos_rv);
															FoodPhotoAdapter foodPhotoAdapter = new FoodPhotoAdapter(requireActivity(), mFoodPhotos);
															if (foodPhotosRv != null) {
																foodPhotosRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
																foodPhotosRv.setAdapter(foodPhotoAdapter);
															}
														}
													}
												})
												.addOnFailureListener(new OnFailureListener() {
													@Override
													public void onFailure(@NonNull Exception e) {
														Log.e(TAG, "onFailure: ", e.fillInStackTrace());
													}
												});
									}
								})
								.addOnFailureListener(new OnFailureListener() {
									@Override
									public void onFailure(@NonNull Exception e) {
										Log.e(TAG, "onFailure: ", e.fillInStackTrace());
									}
								});
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "onFailure: ", e.fillInStackTrace());
					}
				});
		
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
	
	@Override
	public void onClick(int position) {
		MainActivity activity = (MainActivity) requireActivity();
		activity.mNavController.navigate(R.id.action_nav_home_to_nav_item_details);
	}
	
	@Override
	public void addCart(int position) {
		final String foodItem;
		if (position % 2 == 0) foodItem = "apple";
		else foodItem = "potato";
		
		FirebaseDatabase.getInstance().getReference().child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					if (snapshot.hasChild(foodItem)) {
						int quantity = snapshot.child(foodItem).getValue(Integer.class);
						FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItem).setValue(quantity + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
								MainActivity activity = (MainActivity) requireActivity();
								activity.mNavController.navigate(R.id.action_nav_home_to_nav_cart);
							}
						});
					} else {
						FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItem).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
								MainActivity activity = (MainActivity) requireActivity();
								activity.mNavController.navigate(R.id.action_nav_home_to_nav_cart);
							}
						});
					}
				} else {
					FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItem).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
						@Override
						public void onSuccess(Void aVoid) {
							MainActivity activity = (MainActivity) requireActivity();
							activity.mNavController.navigate(R.id.action_nav_home_to_nav_cart);
						}
					});
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			
			}
		});
	}
	
	@Override
	public void onCategoryClick(int position) {
		MainActivity activity = (MainActivity) requireActivity();
		Bundle bundle = new Bundle();
		bundle.putString("CATEGORY", categoriesName.get(position % categoriesName.size()));
		activity.mNavController.navigate(R.id.action_nav_home_to_nav_category_list, bundle);
	}
}