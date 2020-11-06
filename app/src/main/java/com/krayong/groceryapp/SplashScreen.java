package com.krayong.groceryapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		findViewById(R.id.githubIcon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
				builder.setTitle("Open Link");
				builder.setMessage("Do you want to open the link?");
				builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/krayong"));
						startActivity(Intent.createChooser(browserIntent, "Select the app to open the link"));
						
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					
					}
				});
				builder.show();
			}
		});
		
		findViewById(R.id.linkedInIcon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
				builder.setTitle("Open Link");
				builder.setMessage("Do you want to open the link?");
				builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/karangourisaria"));
						startActivity(Intent.createChooser(browserIntent, "Select the app to open the link"));
						
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					
					}
				});
				builder.show();
			}
		});
		
		findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashScreen.this, MainActivity.class));
				finish();
			}
		});
	}
}