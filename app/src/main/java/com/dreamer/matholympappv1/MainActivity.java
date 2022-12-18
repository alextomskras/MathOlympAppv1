package com.dreamer.matholympappv1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    //    @Override
//    protected void onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }


}