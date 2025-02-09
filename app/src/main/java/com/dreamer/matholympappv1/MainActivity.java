//package com.dreamer.matholympappv1;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.lifecycle.Observer;
//import androidx.navigation.NavController;
//
//import com.dreamer.matholympappv1.utils.MyArrayList;
//import com.dreamer.matholympappv1.utils.NetworkManager;
//import com.dreamer.matholympappv1.utils.NetworkManager.NetworkState;
//
//public class MainActivity extends AppCompatActivity {
//    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
//    private NavController navController;
//    private NetworkManager networkManager;
//    private AlertDialog noInternetDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initTheme();
//        setContentView(R.layout.activity_main);
//
//        MyArrayList myArrayList = new MyArrayList();
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
//        } else {
//            initNetworkManager();
//        }
//    }
//
//    private void initTheme() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isDarkThemeEnabled = preferences.getBoolean("dark_theme_enabled", false);
//        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
//    }
//
//    private void initNetworkManager() {
//        networkManager = new NetworkManager(this);
//        networkManager.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
//            @Override
//            public void onChanged(NetworkState networkState) {
//                handleNetworkState(networkState);
//            }
//        });
//    }
//
//    private void handleNetworkState(NetworkState networkState) {
//        switch (networkState) {
//            case CONNECTED_WIFI:
//                dismissNoInternetDialog();
//                Toast.makeText(MainActivity.this, "Connected via Wi-Fi", Toast.LENGTH_SHORT).show();
//                break;
//            case CONNECTED_MOBILE:
//                dismissNoInternetDialog();
//                Toast.makeText(MainActivity.this, "Connected via Mobile network", Toast.LENGTH_SHORT).show();
//                break;
//            case CONNECTED_ETHERNET:
//                dismissNoInternetDialog();
//                Toast.makeText(MainActivity.this, "Connected via Ethernet", Toast.LENGTH_SHORT).show();
//                break;
//            case DISCONNECTED:
//                Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_LONG).show();
//                showNoInternetDialog();
//                break;
//            case CONNECTING:
//                Toast.makeText(MainActivity.this, "Connecting to network...", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
//
//    private void showNoInternetDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("No internet connection")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//        noInternetDialog = builder.create();
//        noInternetDialog.show();
//    }
//
//    private void dismissNoInternetDialog() {
//        if (noInternetDialog != null && noInternetDialog.isShowing()) {
//            noInternetDialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
//                initNetworkManager();
//            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//}
//

package com.dreamer.matholympappv1;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.NetworkManager;
import com.dreamer.matholympappv1.utils.NetworkManager.NetworkState;

public class MainActivity extends AppCompatActivity {

    private Integer solutionlimits;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
    private NavController navController;
    // Экземпляр NetworkManager для отслеживания состояния сети
    private NetworkManager networkManager;
    // Диалог, информирующий пользователя об отсутствии подключения
    private AlertDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Инициализация настроек темы
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkThemeEnabled = preferences.getBoolean("dark_theme_enabled", false);
        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
        Log.e(TAG, "isDarkThemeEnabled= " + isDarkThemeEnabled);
        Toast.makeText(this, "Theme: " + (isDarkThemeEnabled ? "Dark" : "Light"), Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Пример использования кастомного класса
        MyArrayList myArrayList = new MyArrayList();

        // Проверка и запрос разрешения для доступа к состоянию сети
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        } else {
            // Если разрешение уже получено, инициализируем NetworkManager
            initNetworkManager();
        }

        // Инициализация навигации
        NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHost != null) {
            navController = navHost.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    /**
     * Инициализирует NetworkManager и устанавливает наблюдателя за изменениями состояния сети.
     */
    private void initNetworkManager() {
        networkManager = new NetworkManager(this);
        networkManager.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                switch (networkState) {
                    case CONNECTED_WIFI:
                        dismissNoInternetDialog();
                        Toast.makeText(MainActivity.this, "Connected via WiFi1", Toast.LENGTH_SHORT).show();
                        // Здесь можно возобновить сетевые операции
                        break;
                    case CONNECTED_MOBILE:
                        dismissNoInternetDialog();
                        Toast.makeText(MainActivity.this, "Connected via Mobile network", Toast.LENGTH_SHORT).show();
                        // Здесь можно возобновить сетевые операции
                        break;
                    case CONNECTED_ETHERNET:
                        dismissNoInternetDialog();
                        Toast.makeText(MainActivity.this, "Connected via Ethernet", Toast.LENGTH_SHORT).show();
                        // Здесь можно возобновить сетевые операции
                        break;
                    case DISCONNECTED:
                        Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                        showNoInternetDialog();
                        // Здесь можно приостановить сетевые операции
                        break;
                    case CONNECTING:
                        Toast.makeText(MainActivity.this, "Connecting to network...", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * Отображает диалоговое окно, информирующее об отсутствии подключения.
     * Диалог не закрывается автоматически и ждёт восстановления сети.
     */
    private void showNoInternetDialog() {
        if (noInternetDialog == null || !noInternetDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Нет подключения к интернету")
                    .setMessage("Подключение к сети отсутствует. Приложение ожидает восстановления соединения...")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // При нажатии кнопки "Повторить" можно повторно инициализировать проверку сети
                            if (networkManager != null) {
                                NetworkState currentState = networkManager.getCurrentNetworkState();
                                if (currentState != NetworkState.DISCONNECTED) {
                                    dismissNoInternetDialog();
                                } else {
                                    Toast.makeText(MainActivity.this, "Все ещё нет соединения", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
            noInternetDialog = builder.create();
            noInternetDialog.show();
        }
    }

    /**
     * Закрывает диалоговое окно, если оно открыто.
     */
    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * Обработка результата запроса разрешений.
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
     * Метод для установки темы.
     */
    public void setTheme(boolean isDarkThemeEnabled) {
        if (isDarkThemeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }
    }
}


//package com.dreamer.matholympappv1;
//
//import static android.content.ContentValues.TAG;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.navigation.NavController;
//import androidx.navigation.NavHost;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.navigation.ui.NavigationUI;
//import androidx.preference.PreferenceManager;
//
//import com.dreamer.matholympappv1.utils.MyArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    private Integer solutionlimits;
//    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
//    NavController navController;
//
//    // Диалоговое окно, которое будет показываться при отсутствии подключения
//    private AlertDialog noInternetDialog;
//
//    // BroadcastReceiver для отслеживания изменений подключения
//    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Получаем информацию о сети
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (connectivityManager != null) {
//                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
//                if (!isConnected) {
//                    // Если подключения нет, показываем диалог
//                    showNoInternetDialog();
//                } else {
//                    // Если сеть восстановилась, закрываем диалог (если он открыт)
//                    dismissNoInternetDialog();
//                    Toast.makeText(context, "Internet connection restored", Toast.LENGTH_SHORT).show();
//                    // Здесь можно возобновить сетевые операции
//                }
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // Инициализация настроек темы
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isDarkThemeEnabled = preferences.getBoolean("dark_theme_enabled", false);
//        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
//        Log.e(TAG, "isDarkThemeEnabled= " + isDarkThemeEnabled);
//        Toast.makeText(this, "Theme: " + (isDarkThemeEnabled ? "Dark" : "Light"), Toast.LENGTH_SHORT).show();
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Пример использования кастомного класса
//        MyArrayList myArrayList = new MyArrayList();
//
//        // Проверка и запрос разрешения для доступа к состоянию сети
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
//                    MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
//        } else {
//            checkNetworkConnection();
//        }
//
//        // Инициализация навигации
//        NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        if (navHost != null) {
//            navController = navHost.getNavController();
//            NavigationUI.setupActionBarWithNavController(this, navController);
//        }
//    }
//
//    /**
//     * Метод для проверки подключения к интернету при запуске Activity.
//     */
//    private void checkNetworkConnection() {
//        ConnectivityManager connectivityManager =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            if (networkInfo != null && networkInfo.isConnected()) {
//                Toast.makeText(this, "Internet connection good", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
//                showNoInternetDialog();
//            }
//        } else {
//            Toast.makeText(this, "ConnectivityManager not available", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * Отображает диалоговое окно, информирующее о потере интернет-соединения.
//     * Диалог не закрывается автоматически и ждёт восстановления сети.
//     */
//    private void showNoInternetDialog() {
//        if (noInternetDialog == null || !noInternetDialog.isShowing()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Нет подключения к интернету")
//                    .setMessage("Подключение к сети отсутствует. Приложение ожидает восстановления соединения...")
//                    .setCancelable(false)
//                    .setPositiveButton("Повторить", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // При нажатии кнопки "Повторить" можем снова проверить состояние сети
//                            checkNetworkConnection();
//                        }
//                    });
//            noInternetDialog = builder.create();
//            noInternetDialog.show();
//        }
//    }
//
//    /**
//     * Закрывает диалоговое окно, если оно открыто.
//     */
//    private void dismissNoInternetDialog() {
//        if (noInternetDialog != null && noInternetDialog.isShowing()) {
//            noInternetDialog.dismiss();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Регистрируем ресивер для отслеживания изменений подключения
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkReceiver, filter);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Отменяем регистрацию ресивера, чтобы избежать утечек памяти
//        unregisterReceiver(networkReceiver);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return navController.navigateUp() || super.onSupportNavigateUp();
//    }
//
//    /**
//     * Обработка результата запроса разрешений.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
//                checkNetworkConnection();
//            } else {
//                Toast.makeText(this, "No permission to access network state", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    /**
//     * Метод для установки темы.
//     */
//    public void setTheme(boolean isDarkThemeEnabled) {
//        if (isDarkThemeEnabled) {
//            setTheme(R.style.AppTheme_Dark);
//        } else {
//            setTheme(R.style.AppTheme);
//        }
//    }
//}
//
//
////package com.dreamer.matholympappv1;
////
////import static android.content.ContentValues.TAG;
////
////import android.content.Context;
////import android.content.SharedPreferences;
////import android.content.pm.PackageManager;
////import android.net.ConnectivityManager;
////import android.net.NetworkInfo;
////import android.os.Bundle;
////import android.util.Log;
////import android.widget.Toast;
////
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.navigation.NavController;
////import androidx.navigation.NavHost;
////import androidx.navigation.fragment.NavHostFragment;
////import androidx.navigation.ui.NavigationUI;
////import androidx.preference.PreferenceManager;
////
////import com.dreamer.matholympappv1.utils.MyArrayList;
////
////public class MainActivity extends AppCompatActivity {
////
////    private Integer solutionlimits;
////    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
////    NavController navController;
////
////    //    private Toolbar toolbar;
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        //init FirebaseDB
////
////        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
////        boolean isDarkThemeEnabled = preferences.getBoolean("dark_theme_enabled", false);
//////        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
//////        setTheme(isDarkThemeEnabled);
////        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
////        Log.e(TAG, "isDarkThemeEnabled= " + isDarkThemeEnabled);
////        Toast.makeText(this, "R.style.AppTheme_Dark" + isDarkThemeEnabled, Toast.LENGTH_SHORT).show();
////
////        super.onCreate(savedInstanceState);
////
//////        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
////
//////        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//////        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//////        if (getResources().getBoolean(R.bool.is_dark_theme_enabled)) {
//////            setTheme(R.style.AppTheme_Dark);
//////        } else {
//////            setTheme(R.style.AppTheme);
//////        }
////        setContentView(R.layout.activity_main);
////
//////    sharedPreffsSaveSolutionLimits(3);
////// Create a new instance of MyArrayList
////        MyArrayList myArrayList = new MyArrayList();
////
////        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//////    private Object getSystemService(String connectivityService) {
//////        return null;
//////    }
////
////
//////        NetworkInfo networkInfo;
////        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
////
//////    {
//////        assert connectivityManager != null;
//////        networkInfo = connectivityManager.getActiveNetworkInfo();
//////    }
////
////        if (networkInfo != null && networkInfo.isConnected()) {
////            // Internet is available
////            Toast.makeText(this, "Internet connection good", Toast.LENGTH_SHORT).show();
////        } else {
////            // Internet is not available
////            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
////        }
////
//////    //check if android11
//////    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
//////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
//////        } else {
//////            // Permission already granted, do whatever you need to do
//////        }
//////    } else {
//////        // For Android versions lower than 11, the app is always considered to be in the foreground when it is started
//////    }
////
//////        toolbar = findViewById(R.id.toolbar);
//////        setSupportActionBar(toolbar);
////        NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//////        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
////        navController = navHost.getNavController();
////
////        NavigationUI.setupActionBarWithNavController(this, navController);
////
////
//////        if (savedInstanceState == null) {
//////            getSupportFragmentManager().beginTransaction()
////////                    .replace(R.id.login_fragment, MainFragment.newInstance())
////////                    .replace(R.id.login_fragment, LoginFragment.newInstance())
//////                    .replace(R.id.login_fragment, ScrollingFragment.newInstance())
//////                    .commitNow();
//////        }
////    }
////
////
//////    @Override
//////    public boolean onSupportNavigateUp() {
//////        navController.navigateUp();
//////        return super.onSupportNavigateUp();
//////    }
////
////    @Override
////    public boolean onSupportNavigateUp() {
////        return navController.navigateUp() || super.onSupportNavigateUp();
////    }
////
////    @Override
////    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE) {
////            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                // Permission granted, do whatever you need to do
////                Toast.makeText(this, "Permission is granted", Toast.LENGTH_LONG).show();
////            } else {
////                // Permission denied, handle the situation
////                Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
////            }
////        }
////    }
////
////    public void setTheme(boolean isDarkThemeEnabled) {
////        if (isDarkThemeEnabled) {
////            setTheme(R.style.AppTheme_Dark);
////        } else {
////            setTheme(R.style.AppTheme);
////        }
////    }
////    //    @Override
//////    protected void onSupportNavigateUp(): Boolean {
//////        return navController.navigateUp() || super.onSupportNavigateUp()
//////    }
////
//////    @Override
//////    public boolean onCreateOptionsMenu(Menu menu) {
//////        getMenuInflater().inflate(R.menu.scroll_frag_menu, menu);
//////        return true;
//////    }
////}