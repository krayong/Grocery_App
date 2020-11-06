package com.krayong.groceryapp;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
	
	private AppBarConfiguration mAppBarConfiguration;
	
	public BottomAppBar mBottomAppBar;
	public NavController mNavController;
	public Toolbar mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		
		mBottomAppBar = findViewById(R.id.bottom_app_bar);
		
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.nav_view);
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
		
		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
				.setDrawerLayout(drawer)
				.build();
		
		mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(bottomNavigationView, mNavController);
		NavigationUI.setupWithNavController(navigationView, mNavController);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}
}