package com.krayong.groceryapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {
	private Activity mActivity;
	private FoodItemClickListener mFoodItemClickListener;
	
	public FoodItemAdapter(Activity activity, FoodItemClickListener mFoodItemClickListener) {
		mActivity = activity;
		this.mFoodItemClickListener = mFoodItemClickListener;
	}
	
	@NonNull
	@Override
	public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.food_item, parent, false);
		return new FoodItemViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
		if (position % 2 == 0) {
			Picasso.get()
					.load(R.drawable.apple)
					.into(holder.itemImage);
			holder.itemName.setText("Apple");
			holder.itemPrice.setText("120/KG");
		} else {
			Picasso.get()
					.load(R.drawable.potato)
					.into(holder.itemImage);
			holder.itemName.setText("Potato");
			holder.itemPrice.setText("50/KG");
		}
	}
	
	@Override
	public int getItemCount() {
		return 20;
	}
	
	public interface FoodItemClickListener {
		void onClick(int position);
		void addCart(int position);
	}
	
	public class FoodItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private ImageView itemImage;
		private TextView itemName, itemPrice;
		
		public FoodItemViewHolder(@NonNull View itemView) {
			super(itemView);
			itemImage = itemView.findViewById(R.id.item_image);
			
			itemName = itemView.findViewById(R.id.item_name);
			itemPrice = itemView.findViewById(R.id.item_price);
			
			itemView.findViewById(R.id.item_add_cart).setOnClickListener(this);
			
			itemView.findViewById(R.id.item_card).setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.item_add_cart)
				mFoodItemClickListener.addCart(getAdapterPosition());
			else
				mFoodItemClickListener.onClick(getAdapterPosition());
		}
	}
}
