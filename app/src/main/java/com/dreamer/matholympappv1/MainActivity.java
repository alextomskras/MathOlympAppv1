package com.dreamer.matholympappv1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.NetworkManager;
import com.dreamer.matholympappv1.utils.NetworkManager.NetworkState;
import com.dreamer.matholympappv1.domain.usecase.auth.LogoutUseCase;
import com.dreamer.matholympappv1.domain.usecase.session.SessionManager;
import androidx.navigation.NavOptions;
import com.dreamer.matholympappv1.utils.UserPrefsRepository;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
    private NavController navController;
    // Р­РєР·РµРјРїР»СЏСЂ NetworkManager РґР»СЏ РѕС‚СЃР»РµР¶РёРІР°РЅРёСЏ СЃРѕСЃС‚РѕСЏРЅРёСЏ СЃРµС‚Рё
    private NetworkManager networkManager;
    // Р”РёР°Р»РѕРі, РёРЅС„РѕСЂРјРёСЂСѓСЋС‰РёР№ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ РѕР± РѕС‚СЃСѓС‚СЃС‚РІРёРё РїРѕРґРєР»СЋС‡РµРЅРёСЏ
    private AlertDialog noInternetDialog;
    // РџРѕСЃР»РµРґРЅРµРµ СЃРѕРѕР±С‰С‘РЅРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ СЃРµС‚Рё: Toast РїРѕРєР°Р·С‹РІР°РµС‚СЃСЏ С‚РѕР»СЊРєРѕ РЅР° СЂРµР°Р»СЊРЅРѕРј
    // РїРµСЂРµС…РѕРґРµ СЃРѕСЃС‚РѕСЏРЅРёСЏ, Р° РЅРµ РЅР° РєР°Р¶РґРѕР№ РїРµСЂРµСЃС‹Р»РєРµ С‚РѕРіРѕ Р¶Рµ СЃРѕСЃС‚РѕСЏРЅРёСЏ LiveData
    private NetworkState lastNetworkState = null;
    private AuthStateListener authStateListener;
    // Р¤Р»Р°Рі: РёРґС‘С‚ РїСЂРѕС†РµСЃСЃ РІС‹С…РѕРґР° вЂ” Р±Р»РѕРєРёСЂСѓРµС‚ Р°РЅРѕРЅРёРјРЅС‹Р№ Р°РІС‚РѕР»РѕРіРёРЅ РІ AuthStateListener
    private volatile boolean isLoggingOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // РўРµРјР° РїСЂРёРјРµРЅСЏРµС‚СЃСЏ РґРѕ super.onCreate (AppCompat С‚СЂРµР±СѓРµС‚ СЌС‚РѕРіРѕ РїРѕСЂСЏРґРєР°):
        // Р·РЅР°С‡РµРЅРёРµ Р±РµСЂС‘С‚СЃСЏ РёР· РґРµС„РѕР»С‚РЅС‹С… SharedPreferences, РєСѓРґР° РµРіРѕ РїРёС€РµС‚ SettingsFragment.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkTheme = prefs.getBoolean("dark_theme_enabled", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // РџСЂРёРјРµСЂ РёСЃРїРѕР»СЊР·РѕРІР°РЅРёСЏ РєР°СЃС‚РѕРјРЅРѕРіРѕ РєР»Р°СЃСЃР°
        MyArrayList myArrayList = new MyArrayList();

        // РџСЂРѕРІРµСЂРєР° Рё Р·Р°РїСЂРѕСЃ СЂР°Р·СЂРµС€РµРЅРёСЏ РґР»СЏ РґРѕСЃС‚СѓРїР° Рє СЃРѕСЃС‚РѕСЏРЅРёСЋ СЃРµС‚Рё
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        } else {
            // Р•СЃР»Рё СЂР°Р·СЂРµС€РµРЅРёРµ СѓР¶Рµ РїРѕР»СѓС‡РµРЅРѕ, РёРЅРёС†РёР°Р»РёР·РёСЂСѓРµРј NetworkManager
            initNetworkManager();
        }

        // РќР°СЃС‚СЂР°РёРІР°РµРј СЃР»СѓС€Р°С‚РµР»СЊ СЃРѕСЃС‚РѕСЏРЅРёСЏ Р°СѓС‚РµРЅС‚РёС„РёРєР°С†РёРё
        authStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null && !isLoggingOut) {
                    // РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ РЅРµ Р°РІС‚РѕСЂРёР·РѕРІР°РЅ - РІС‹РїРѕР»РЅСЏРµРј Р°РЅРѕРЅРёРјРЅС‹Р№ РІС…РѕРґ.
                    // Р’Рѕ РІСЂРµРјСЏ РІС‹С…РѕРґР° (isLoggingOut) РќР• РїРµСЂРµР»РѕРіРёРЅРёРІР°РµРјСЃСЏ,
                    // РёРЅР°С‡Рµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ СЃСЂР°Р·Сѓ РІРѕР·РІСЂР°С‰Р°РµС‚СЃСЏ РІ Р°РІС‚РѕСЂРёР·РѕРІР°РЅРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ
                    mAuth.signInAnonymously();
                }
            }
        };

        // РРЅРёС†РёР°Р»РёР·Р°С†РёСЏ РЅР°РІРёРіР°С†РёРё
        NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHost != null) {
//            navController = navHost.getNavController();
//            NavigationUI.setupActionBarWithNavController(this, navController);
            navController = navHost.getNavController();

// рџ‘‡ РЈРєР°Р·С‹РІР°РµРј, С‡С‚Рѕ СЃС‚СЂРµР»РєР° РЅРµ РЅСѓР¶РЅР° С‚РѕР»СЊРєРѕ РІ RAZDELFragment (РІРµСЂС…РЅРµСѓСЂРѕРІРЅРµРІС‹Р№ СЌРєСЂР°РЅ)
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.RAZDELFragment
            ).build();

// рџ‘‡ РџРѕРґРєР»СЋС‡Р°РµРј Рє AppBar
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            // рџ”Ѕ Р”РѕР±Р°РІР»СЏРµРј РёР·РјРµРЅРµРЅРёРµ Р·Р°РіРѕР»РѕРІРєР° С„СЂР°РіРјРµРЅС‚Р° С‡РµСЂРµР· СЂРµСЃСѓСЂСЃС‹
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                String title;

                switch (destId) {
                    case R.id.RAZDELFragment:
                        title = getString(R.string.fragment_title_home);
                        break;
                    case R.id.zadachaFragment:
                        title = getString(R.string.fragment_title_tasks);
                        break;
                    case R.id.scrollingFragment2:
                        title = getString(R.string.fragment_title_tasks);
                        break;
                    case R.id.settingsFragment:
                        title = getString(R.string.fragment_title_settings);
                        break;
                    default:
                        title = getString(R.string.app_name);
                }

                getSupportActionBar().setTitle(title);
            });

            // РџСЂРѕРІРµСЂРєР° Р°РІС‚РѕСЂРёР·Р°С†РёРё Рё РЅР°РІРёРіР°С†РёСЏ Рє РЅСѓР¶РЅРѕРјСѓ С„СЂР°РіРјРµРЅС‚Сѓ (РїРѕСЃР»Рµ РёРЅРёС†РёР°Р»РёР·Р°С†РёРё navController).
            // Р•СЃР»Рё РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ СѓР¶Рµ Р°РІС‚РѕСЂРёР·РѕРІР°РЅ РІ Firebase Auth (СЃРµСЃСЃРёСЏ СЃРѕС…СЂР°РЅРµРЅР° SDK),
            // СЃСЂР°Р·Сѓ РѕС‚РєСЂС‹РІР°РµРј РіР»Р°РІРЅС‹Р№ СЌРєСЂР°РЅ Р±РµР· Р·Р°РїСЂРѕСЃР° РїР°СЂРѕР»СЏ.
            // loginFragment РїСЂРё СЌС‚РѕРј СѓРґР°Р»СЏРµС‚СЃСЏ РёР· back stack (popUpTo inclusive),
            // С‡С‚РѕР±С‹ Back РЅРµ РІРѕР·РІСЂР°С‰Р°Р» РЅР° СЌРєСЂР°РЅ РІС…РѕРґР°.
            // Р›РѕРіРёРєР° Р°РІС‚РѕР»РѕРіРёРЅР° СЃРёРЅС…СЂРѕРЅРёР·РёСЂРѕРІР°РЅР° СЃ LoginFragment.isUserAlreadyLoggedIn().
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                NavOptions toMain = new NavOptions.Builder()
                        .setPopUpTo(navController.getGraph().getStartDestination(), true)
                        .build();
                navController.navigate(R.id.RAZDELFragment, null, toMain);
            }
            // РРЅР°С‡Рµ РЅРёС‡РµРіРѕ РЅРµ РґРµР»Р°РµРј: startDestination (loginFragment) СѓР¶Рµ РїРѕРєР°Р·Р°РЅ
            // СЃР°РјРёРј NavHostFragment вЂ” Р»РёС€РЅРёР№ navigate(R.id.loginFragment) СЃРѕР·РґР°РІР°Р»
            // РґСѓР±Р»РёРєР°С‚ СЌРєСЂР°РЅР° РІС…РѕРґР° РІ СЃС‚РµРєРµ.
        }

        // Р’РѕСЃСЃС‚Р°РЅР°РІР»РёРІР°РµРј SessionManager РїРѕСЃР»Рµ РїРµСЂРµР·Р°РїСѓСЃРєР° РїСЂРёР»РѕР¶РµРЅРёСЏ:
        // Firebase Auth С…СЂР°РЅРёС‚ СЃРµСЃСЃРёСЋ, РЅРѕ РїР»Р°РЅРёСЂРѕРІС‰РёРє РѕР±РЅРѕРІР»РµРЅРёСЏ С‚РѕРєРµРЅР° вЂ” in-memory,
        // РїРѕСЃР»Рµ СѓР±РёР№СЃС‚РІР° РїСЂРѕС†РµСЃСЃР° РѕРЅ РѕС‚СЃСѓС‚СЃС‚РІСѓРµС‚. Р‘РµР· СЌС‚РѕРіРѕ С‚РѕРєРµРЅ РїРµСЂРµСЃС‚Р°РµС‚
        // РїСЂРёРЅСѓРґРёС‚РµР»СЊРЅРѕ РѕР±РЅРѕРІР»СЏС‚СЊСЃСЏ РґРѕ С‚РµС… РїРѕСЂ, РїРѕРєР° РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ СЃРЅРѕРІР° РЅРµ РІРѕР№РґС‘С‚ РІСЂСѓС‡РЅСѓСЋ.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            SessionManager.getInstance().start();
        }
    }

    /**
     * РРЅРёС†РёР°Р»РёР·РёСЂСѓРµС‚ NetworkManager Рё СѓСЃС‚Р°РЅР°РІР»РёРІР°РµС‚ РЅР°Р±Р»СЋРґР°С‚РµР»СЏ Р·Р° РёР·РјРµРЅРµРЅРёСЏРјРё СЃРѕСЃС‚РѕСЏРЅРёСЏ СЃРµС‚Рё.
     */
    private void initNetworkManager() {
        networkManager = new NetworkManager(this);
        networkManager.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                // NetworkManager РѕС‚РґР°С‘С‚ СЃРѕСЃС‚РѕСЏРЅРёРµ РїСЂРё РєР°Р¶РґРѕР№ РїСЂРѕРІРµСЂРєРµ, Рё Р±РµР· СЌС‚РѕР№ РїСЂРѕРІРµСЂРєРё
                // Toast РїРѕРІС‚РѕСЂСЏР»СЃСЏ РјРЅРѕРіРѕРєСЂР°С‚РЅРѕ (С€С‚РѕСЂРј РѕРґРёРЅР°РєРѕРІС‹С… СѓРІРµРґРѕРјР»РµРЅРёР№).
                boolean stateChanged = (networkState != lastNetworkState);
                lastNetworkState = networkState;

                switch (networkState) {
                    case CONNECTED_WIFI:
                        dismissNoInternetDialog();
                        if (stateChanged) {
                            Toast.makeText(MainActivity.this, "Connected via WiFi1", Toast.LENGTH_SHORT).show();
                        }
                        // Р—РґРµСЃСЊ РјРѕР¶РЅРѕ РІРѕР·РѕР±РЅРѕРІРёС‚СЊ СЃРµС‚РµРІС‹Рµ РѕРїРµСЂР°С†РёРё
                        break;
                    case CONNECTED_MOBILE:
                        dismissNoInternetDialog();
                        if (stateChanged) {
                            Toast.makeText(MainActivity.this, "Connected via Mobile network", Toast.LENGTH_SHORT).show();
                        }
                        // Р—РґРµСЃСЊ РјРѕР¶РЅРѕ РІРѕР·РѕР±РЅРѕРІРёС‚СЊ СЃРµС‚РµРІС‹Рµ РѕРїРµСЂР°С†РёРё
                        break;
                    case CONNECTED_ETHERNET:
                        dismissNoInternetDialog();
                        if (stateChanged) {
                            Toast.makeText(MainActivity.this, "Connected via Ethernet", Toast.LENGTH_SHORT).show();
                        }
                        // Р—РґРµСЃСЊ РјРѕР¶РЅРѕ РІРѕР·РѕР±РЅРѕРІРёС‚СЊ СЃРµС‚РµРІС‹Рµ РѕРїРµСЂР°С†РёРё
                        break;
                    case DISCONNECTED:
                        if (stateChanged) {
                            Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                        }
                        showNoInternetDialog();
                        // Р—РґРµСЃСЊ РјРѕР¶РЅРѕ РїСЂРёРѕСЃС‚Р°РЅРѕРІРёС‚СЊ СЃРµС‚РµРІС‹Рµ РѕРїРµСЂР°С†РёРё
                        break;
                    case CONNECTING:
                        if (stateChanged) {
                            Toast.makeText(MainActivity.this, "Connecting to network...", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    /**
     * РћС‚РѕР±СЂР°Р¶Р°РµС‚ РґРёР°Р»РѕРіРѕРІРѕРµ РѕРєРЅРѕ, РёРЅС„РѕСЂРјРёСЂСѓСЋС‰РµРµ РѕР± РѕС‚СЃСѓС‚СЃС‚РІРёРё РїРѕРґРєР»СЋС‡РµРЅРёСЏ.
     * Р”РёР°Р»РѕРі РЅРµ Р·Р°РєСЂС‹РІР°РµС‚СЃСЏ Р°РІС‚РѕРјР°С‚РёС‡РµСЃРєРё Рё Р¶РґС‘С‚ РІРѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёСЏ СЃРµС‚Рё.
     */
    private void showNoInternetDialog() {
        if (noInternetDialog == null || !noInternetDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("РќРµС‚ РїРѕРґРєР»СЋС‡РµРЅРёСЏ Рє РёРЅС‚РµСЂРЅРµС‚Сѓ")
                    .setMessage("РџРѕРґРєР»СЋС‡РµРЅРёРµ Рє СЃРµС‚Рё РѕС‚СЃСѓС‚СЃС‚РІСѓРµС‚. РџСЂРёР»РѕР¶РµРЅРёРµ РѕР¶РёРґР°РµС‚ РІРѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёСЏ СЃРѕРµРґРёРЅРµРЅРёСЏ...")
                    .setCancelable(false)
                    .setPositiveButton("РџРѕРІС‚РѕСЂРёС‚СЊ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // РџСЂРё РЅР°Р¶Р°С‚РёРё РєРЅРѕРїРєРё "РџРѕРІС‚РѕСЂРёС‚СЊ" РјРѕР¶РЅРѕ РїРѕРІС‚РѕСЂРЅРѕ РёРЅРёС†РёР°Р»РёР·РёСЂРѕРІР°С‚СЊ РїСЂРѕРІРµСЂРєСѓ СЃРµС‚Рё
                            if (networkManager != null) {
                                NetworkState currentState = networkManager.getCurrentNetworkState();
                                if (currentState != NetworkState.DISCONNECTED) {
                                    dismissNoInternetDialog();
                                } else {
                                    Toast.makeText(MainActivity.this, "Р’СЃРµ РµС‰С‘ РЅРµС‚ СЃРѕРµРґРёРЅРµРЅРёСЏ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
            noInternetDialog = builder.create();
            noInternetDialog.show();
        }
    }

    /**
     * Р—Р°РєСЂС‹РІР°РµС‚ РґРёР°Р»РѕРіРѕРІРѕРµ РѕРєРЅРѕ, РµСЃР»Рё РѕРЅРѕ РѕС‚РєСЂС‹С‚Рѕ.
     */
    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // РџРѕРґРєР»СЋС‡Р°РµРј СЃР»СѓС€Р°С‚РµР»СЊ СЃРѕСЃС‚РѕСЏРЅРёСЏ Р°СѓС‚РµРЅС‚РёС„РёРєР°С†РёРё РїСЂРё СЃС‚Р°СЂС‚Рµ Р°РєС‚РёРІРЅРѕСЃС‚Рё.
        // РџРѕРєР° РёРґС‘С‚ РїСЂРѕС†РµСЃСЃ РІС‹С…РѕРґР° (isLoggingOut), СЃР»СѓС€Р°С‚РµР»СЊ РЅРµ РІРѕР·РІСЂР°С‰Р°РµРј вЂ”
        // РёРЅР°С‡Рµ РѕРЅ СЃСЂР°Р·Сѓ Р·Р°РїСѓСЃС‚РёС‚ Р°РЅРѕРЅРёРјРЅС‹Р№ РІС…РѕРґ РїРѕСЃР»Рµ signOut.
        if (!isLoggingOut) {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // РћС‚РєР»СЋС‡Р°РµРј СЃР»СѓС€Р°С‚РµР»СЊ РїСЂРё РѕСЃС‚Р°РЅРѕРІРєРµ Р°РєС‚РёРІРЅРѕСЃС‚Рё
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * РћР±СЂР°Р±РѕС‚РєР° СЂРµР·СѓР»СЊС‚Р°С‚Р° Р·Р°РїСЂРѕСЃР° СЂР°Р·СЂРµС€РµРЅРёР№.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                initNetworkManager();
            } else {
                Toast.makeText(this, "No permission to access network state", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * РњРµС‚РѕРґ РґР»СЏ СѓСЃС‚Р°РЅРѕРІРєРё С‚РµРјС‹.
     */
    public void setTheme(boolean isDarkThemeEnabled) {
        if (isDarkThemeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }
    }


    public void logout() {
        // РџРѕРєР° РјС‹ РІ РїСЂРѕС†РµСЃСЃРµ РІС‹С…РѕРґР°, РЅРµ РІС‹РїРѕР»РЅСЏРµРј Р°РЅРѕРЅРёРјРЅС‹Р№ РІС…РѕРґ
        isLoggingOut = true;

        // 1. РћСЃС‚Р°РЅР°РІР»РёРІР°РµРј Р•Р”РРќР«Р™ (singleton) SessionManager.
        //    Р Р°РЅСЊС€Рµ Р·РґРµСЃСЊ СЃРѕР·РґР°РІР°Р»СЃСЏ РЅРѕРІРѕР№ РїСѓСЃС‚РѕР№ СЌРєР·РµРјРїР»СЏСЂ,
        //    Рё СЂРµР°Р»СЊРЅРѕ СЂР°Р±РѕС‚Р°СЋС‰РёР№ РїР»Р°РЅРёСЂРѕРІС‰РёРє РЅРµ РѕСЃС‚Р°РЅР°РІР»РёРІР°Р»СЃСЏ.
        SessionManager.getInstance().stop();

        // 2. РЎРЅРёРјР°РµРј AuthStateListener РґРѕ signOut(), С‡С‚РѕР±С‹ РѕРЅ
        //    РЅРµ Р·Р°РїСѓСЃС‚РёР» signInAnonymously() СЃСЂР°Р·Сѓ РїРѕСЃР»Рµ РІС‹С…РѕРґР°
        //    (РёРЅР°С‡Рµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ "РІРѕР·РІСЂР°С‰Р°РµС‚СЃСЏ" РІ Р°РІС‚РѕСЂРёР·РѕРІР°РЅРЅРѕРµ СЃРѕСЃС‚РѕСЏРЅРёРµ).
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);

        // 3. РСЃРїРѕР»СЊР·СѓРµРј LogoutUseCase РІРјРµСЃС‚Рѕ РїСЂСЏРјРѕРіРѕ РІС‹Р·РѕРІР° Firebase.
        //    Р’ РєРѕР»Р±СЌРєРµ РѕС‡РёСЃС‚РєРё Р»РѕРєР°Р»СЊРЅРѕР№ СЃРµСЃСЃРёРё СѓРґР°Р»СЏРµРј Р•Р”РРќРћР• С…СЂР°РЅРёР»РёС‰Рµ
        //    (login status, username, uid, СЃС‡С‘С‚, Р»РёРјРёС‚С‹) вЂ” РѕРґРЅРѕРіРѕ clearData()
        //    РґРѕСЃС‚Р°С‚РѕС‡РЅРѕ, РІС‚РѕСЂРѕРіРѕ С„Р°Р№Р»Р° Р±РѕР»СЊС€Рµ РЅРµС‚.
        UserPrefsRepository prefs = new UserPrefsRepository(getApplicationContext());

        LogoutUseCase logoutUseCase = new LogoutUseCase(() -> {
            prefs.clearData();
            Log.d("logout", "Р›РѕРєР°Р»СЊРЅР°СЏ СЃРµСЃСЃРёСЏ РѕС‡РёС‰РµРЅР°");
        });

        logoutUseCase.execute(new LogoutUseCase.OnLogoutCompleteCallback() {
            @Override
            public void onLogoutComplete() {
                // РЈСЃРїРµС€РЅС‹Р№ РІС‹С…РѕРґ - РІС‹РїРѕР»РЅСЏРµРј РЅР°РІРёРіР°С†РёСЋ
                runOnUiThread(() -> navigateToLogin());
            }

            @Override
            public void onError(String errorMessage) {
                // Р”Р°Р¶Рµ РїСЂРё РѕС€РёР±РєРµ Firebase - РІС‹РїРѕР»РЅСЏРµРј РЅР°РІРёРіР°С†РёСЋ Рё РѕС‡РёСЃС‚РєСѓ
                runOnUiThread(() -> {
                    navigateToLogin();
                    Toast.makeText(MainActivity.this,
                        "Р’С‹С…РѕРґ РІС‹РїРѕР»РЅРµРЅ (РѕС€РёР±РєР° Firebase: " + errorMessage + ")",
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * РџРµСЂРµС…РѕРґ РЅР° СЌРєСЂР°РЅ РІС…РѕРґР° РїРѕСЃР»Рµ РІС‹С…РѕРґР° РёР· СЃРёСЃС‚РµРјС‹.
     * РћС‡РёС‰Р°РµС‚ РІРµСЃСЊ back stack (С‡С‚РѕР±С‹ Back РЅРµ РІРµСЂРЅСѓР» РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ
     * РІ СЌРєСЂР°РЅС‹ Р°РІС‚РѕСЂРёР·РѕРІР°РЅРЅРѕР№ С‡Р°СЃС‚Рё) Рё Р·Р°РјРµРЅСЏРµС‚ loginFragment РЅР° startDestination,
     * С‚РѕР¶Рµ РЅРµ РѕСЃС‚Р°РІР»СЏСЏ РµРіРѕ РІ back stack.
     */
    private void navigateToLogin() {
        if (navController == null) {
            return;
        }
        navController.popBackStack(R.id.loginFragment, true);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(navController.getGraph().getStartDestination(), false)
                .build();
        navController.navigate(R.id.loginFragment, null, navOptions);
        // РўРµРїРµСЂСЊ РЅР° СЌРєСЂР°РЅРµ РІС…РѕРґР° Back РґРѕР»Р¶РµРЅ Р·Р°РєСЂС‹РІР°С‚СЊ РїСЂРёР»РѕР¶РµРЅРёРµ,
        // Р° РЅРµ РІРѕР·РІСЂР°С‚СЊСЃСЏ РЅР° РїСѓСЃС‚РѕР№ СЃС‚РµРє
        isLoggingOut = false;
        // Р’РѕР·РІСЂР°С‰Р°РµРј СЃР»СѓС€Р°С‚РµР»СЊ СЃРѕСЃС‚РѕСЏРЅРёСЏ Р°СѓС‚РµРЅС‚РёС„РёРєР°С†РёРё
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

}


