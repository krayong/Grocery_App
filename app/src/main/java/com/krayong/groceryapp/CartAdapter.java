package com.krayong.groceryapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
	private static final String TAG = "CartAdapter";
	
	private Activity mActivity;
	private CartItemClickListener mCartItemClickListener;
	private ArrayList<String> cartItems;
	private HashMap<String, Integer> cartItemPrices;
	private ArrayList<Integer> cartItemQuantities;
	private HashMap<String, Uri>  cartItemImages;
	
	public CartAdapter(Activity activity, CartItemClickListener cartItemClickListener, ArrayList<String> cartItems, HashMap<String, Integer>  cartItemPrices, ArrayList<Integer> cartItemQuantities, HashMap<String, Uri>  cartItemImages) {
		mActivity = activity;
		mCartItemClickListener = cartItemClickListener;
		this.cartItems = cartItems;
		this.cartItemPrices = cartItemPrices;
		this.cartItemQuantities = cartItemQuantities;
		this.cartItemImages = cartItemImages;
	}
	
	@NonNull
	@Override
	public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.cart_item, parent, false);
		return new CartViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
		String itemName = cartItems.get(position);
		holder.itemName.setText(itemName.substring(0, 1).toUpperCase() + itemName.substring(1));
		Picasso.get().load(cartItemImages.get(itemName))
				.into(holder.itemImage);
		int price = cartItemPrices.get(itemName);
		holder.itemPrice.setText(price + "/KG");
		holder.itemTotalPrice.setText(price * cartItemQuantities.get(position) + "");
	}
	
	@Override
	public int getItemCount() {
		return cartItems.size();
	}
	
	public interface CartItemClickListener {
		void add (int position);
		void remove (int position);
	}
	
	public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView itemImage;
		TextView itemName, itemPrice, itemTotalPrice;
		ImageButton addButton, removeButton;
		
		public CartViewHolder(@NonNull View itemView) {
			super(itemView);
			itemImage = itemView.findViewById(R.id.cart_item_image);
			itemName = itemView.findViewById(R.id.cart_item_name);
			itemPrice = itemView.findViewById(R.id.cart_item_price);
			itemTotalPrice = itemView.findViewById(R.id.cart_item_total_price);
			addButton = itemView.findViewById(R.id.cart_add_item);
			removeButton = itemView.findViewById(R.id.cart_remove_item);
			
			addButton.setOnClickListener(this);
			removeButton.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.cart_add_item) mCartItemClickListener.add(getAdapterPosition());
			else if (v.getId() == R.id.cart_remove_item) mCartItemClickListener.remove(getAdapterPosition());
		}
	}
}
