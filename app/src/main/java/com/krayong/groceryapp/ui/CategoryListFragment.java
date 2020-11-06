package com.krayong.groceryapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krayong.groceryapp.FoodItemAdapter;
import com.krayong.groceryapp.MainActivity;
import com.krayong.groceryapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListFragment extends Fragment implements FoodItemAdapter.FoodItemClickListener {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_category_list, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		Bundle bundle = getArguments();
		MainActivity activity = (MainActivity) requireActivity();
		activity.mToolbar.setTitle(bundle.getString("CATEGORY").toUpperCase());
		
		RecyclerView itemsRv = view.findViewById(R.id.item_list_rv);
		FoodItemAdapter foodItemAdapter = new FoodItemAdapter(requireActivity(), this);
		itemsRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
		itemsRv.setAdapter(foodItemAdapter);
	}
	
	@Override
	public void onClick(int position) {
		MainActivity activity = (MainActivity) requireActivity();
		activity.mNavController.navigate(R.id.action_nav_category_list_to_nav_item_details);
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
								activity.mNavController.navigate(R.id.action_nav_category_list_to_nav_cart);
							}
						});
					} else {
						FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItem).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
								MainActivity activity = (MainActivity) requireActivity();
								activity.mNavController.navigate(R.id.action_nav_category_list_to_nav_cart);
							}
						});
					}
				} else {
					FirebaseDatabase.getInstance().getReference().child("cartItems").child(foodItem).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
						@Override
						public void onSuccess(Void aVoid) {
							MainActivity activity = (MainActivity) requireActivity();
							activity.mNavController.navigate(R.id.action_nav_category_list_to_nav_cart);
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