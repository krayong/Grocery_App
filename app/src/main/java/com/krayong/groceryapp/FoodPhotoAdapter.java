package com.krayong.groceryapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodPhotoAdapter extends RecyclerView.Adapter<FoodPhotoAdapter.FoodPhotoViewHolder> {
	private Activity mActivity;
	private ArrayList<Uri> mPhotos;
	
	public FoodPhotoAdapter(Activity activity, ArrayList<Uri> photos) {
		mActivity = activity;
		mPhotos = photos;
	}
	
	@NonNull
	@Override
	public FoodPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.food_photo_item, parent, false);
		return new FoodPhotoViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull FoodPhotoViewHolder holder, int position) {
		position = position % mPhotos.size();
		Picasso.get().load(mPhotos.get(position))
				.placeholder(R.drawable.fruit1)
				.into(holder.photo);
	}
	
	@Override
	public int getItemCount() {
		return mPhotos.size() * 10;
	}
	
	static class FoodPhotoViewHolder extends RecyclerView.ViewHolder {
		final ImageView photo;
		public FoodPhotoViewHolder(@NonNull View itemView) {
			super(itemView);
			photo = itemView.findViewById(R.id.food_photo);
		}
	}
}
