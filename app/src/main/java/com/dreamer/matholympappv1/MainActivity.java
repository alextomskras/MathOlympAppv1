package com.dreamer.matholympappv1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.dreamer.matholympappv1.utils.MyArrayList;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Integer solutionlimits;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
    NavController navController;

    //    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (getResources().getBoolean(R.bool.is_dark_theme_enabled)) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);

//    sharedPreffsSaveSolutionLimits(3);
// Create a new instance of MyArrayList
        MyArrayList myArrayList = new MyArrayList();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//    private Object getSystemService(String connectivityService) {
//        return null;
//    }


        NetworkInfo networkInfo;

    {
        assert connectivityManager != null;
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    if (networkInfo != null && networkInfo.isConnected()) {
        // Internet is available
        Toast.makeText(this, "Internet connection good", Toast.LENGTH_SHORT).show();
    } else {
        // Internet is not available
        Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
    }

    //check if android11
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        } else {
            // Permission already granted, do whatever you need to do
        }
    } else {
        // For Android versions lower than 11, the app is always considered to be in the foreground when it is started
    }

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    navController = navHost.getNavController();

    NavigationUI.setupActionBarWithNavController(this, navController);

    //init FirebaseDB
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
////                    .replace(R.id.login_fragment, MainFragment.newInstance())
////                    .replace(R.id.login_fragment, LoginFragment.newInstance())
//                    .replace(R.id.login_fragment, ScrollingFragment.newInstance())
//                    .commitNow();
//        }
}


    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do whatever you need to do
                Toast.makeText(this, "Permission is granted", Toast.LENGTH_LONG).show();
            } else {
                // Permission denied, handle the situation
                Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    //    @Override
//    protected void onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.scroll_frag_menu, menu);
//        return true;
//    }
}