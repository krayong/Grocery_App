package com.krayong.groceryapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
	private Activity mActivity;
	private ArrayList<String> categoriesName;
	private ArrayList<Uri> categoriesImage;
	private CategoryClickListener mCategoryClickListener;
	
	public CategoriesAdapter(Activity activity, CategoryClickListener categoryClickListener, ArrayList<String> categoriesName, ArrayList<Uri> categoriesImage) {
		mActivity = activity;
		this.mCategoryClickListener = categoryClickListener;
		this.categoriesName = categoriesName;
		this.categoriesImage = categoriesImage;
	}
	
	@NonNull
	@Override
	public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.categories_item, parent, false);
		return new CategoriesViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
		position = position % categoriesName.size();
		holder.categoryName.setText(categoriesName.get(position).substring(0, 1).toUpperCase() + categoriesName.get(position).substring(1));
		Picasso.get().load(categoriesImage.get(position))
				.placeholder(R.drawable.fruit3)
				.into(holder.categoryBg);
	}
	
	@Override
	public int getItemCount() {
		return categoriesName.size() * 5;
	}
	
	public interface CategoryClickListener {
		void onCategoryClick(int position);
	}
	
	public class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private ImageView categoryBg;
		private TextView categoryName;
		
		public CategoriesViewHolder(@NonNull View itemView) {
			super(itemView);
			categoryBg = itemView.findViewById(R.id.categories_bg);
			categoryName = itemView.findViewById(R.id.categories_name);
			
			itemView.findViewById(R.id.categories_card).setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			mCategoryClickListener.onCategoryClick(getAdapterPosition());
		}
	}
}
