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
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.NetworkManager;
import com.dreamer.matholympappv1.utils.NetworkManager.NetworkState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;

public class MainActivity extends AppCompatActivity {

    private Integer solutionlimits;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 1;
    private NavController navController;
    // Экземпляр NetworkManager для отслеживания состояния сети
    private NetworkManager networkManager;
    // Диалог, информирующий пользователя об отсутствии подключения
    private AlertDialog noInternetDialog;
    private AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Инициализация настроек темы
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isDarkThemeEnabled = preferences.getBoolean("dark_theme_enabled", false);
//        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark : R.style.AppTheme);
//        Log.e(TAG, "isDarkThemeEnabled= " + isDarkThemeEnabled);
//        Toast.makeText(this, "Theme: " + (isDarkThemeEnabled ? "Dark" : "Light"), Toast.LENGTH_SHORT).show();

//         Перед super.onCreate
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkTheme = prefs.getBoolean("dark_theme_enabled", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

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

        // Настраиваем слушатель состояния аутентификации
        authStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    // Пользователь не авторизован - выполняем анонимный вход
                    mAuth.signInAnonymously();
                }
            }
        };

        // Инициализация навигации
        NavHost navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHost != null) {
//            navController = navHost.getNavController();
//            NavigationUI.setupActionBarWithNavController(this, navController);
            navController = navHost.getNavController();

// 👇 Указываем, что стрелка не нужна только в RAZDELFragment (верхнеуровневый экран)
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.RAZDELFragment
            ).build();

// 👇 Подключаем к AppBar
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            // 🔽 Добавляем изменение заголовка фрагмента через ресурсы
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
                    case R.id.settingsFragment:
                        title = getString(R.string.fragment_title_settings);
                        break;
                    default:
                        title = getString(R.string.app_name);
                }

                getSupportActionBar().setTitle(title);
            });
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
    protected void onStart() {
        super.onStart();
        // Подключаем слушатель состояния аутентификации при старте активности
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Отключаем слушатель при остановке активности
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
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


    public void logout() {
        // 1. Выход из Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Очистка SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().clear().apply();

        // 3. Переход на loginFragment
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)).getNavController();

        navController.navigate(R.id.loginFragment);

        // 4. Очистка backstack, чтобы нельзя было вернуться назад
        navController.popBackStack(R.id.loginFragment, false);
    }

}


