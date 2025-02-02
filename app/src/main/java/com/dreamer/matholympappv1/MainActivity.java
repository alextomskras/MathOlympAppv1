package com.dreamer.matholympappv1;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.NetworkManager;
import com.dreamer.matholympappv1.utils.NetworkManager.NetworkState;
import com.dreamer.matholympappv1.utils.PermissionManager;
import com.dreamer.matholympappv1.utils.PermissionManager.PermissionCallback;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private Integer solutionlimits;
    private static final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    };

    private static final String[] DANGEROUS_PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };

    private NavController navController;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Theme setup
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkTheme = preferences.getBoolean("dark_theme_enabled", false);
        setTheme(isDarkTheme ? R.style.Theme_App : R.style.Theme_App);
        super.onCreate(savedInstanceState);

        // Проверяем разрешения в зависимости от версии Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            handleAndroid11Permissions();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-10 (API 23-29)
            handleAndroid6Permissions();
        } else {
            // Android 5.1 и ниже (API 22-)
            // Разрешения предоставляются при установке
            proceedWithAppInitialization();
        }
    }

    private void handleAndroid11Permissions() {
        // Проверяем необходимость запроса MANAGE_EXTERNAL_STORAGE
        if (needsManageExternalStorage()) {
            requestManageExternalStorage();
        } else {
            // Проверяем обычные runtime permissions
            handleAndroid6Permissions();
        }

        // Проверяем разрешения на уведомления
        checkNotificationPermission();
    }

    private boolean needsManageExternalStorage() {
        // Проверяем, нужен ли доступ ко всем файлам
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && 
               !android.os.Environment.isExternalStorageManager();
    }

    private void requestManageExternalStorage() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
        }
    }

    private void handleAndroid6Permissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        
        // Проверяем каждое опасное разрешение
        for (String permission : DANGEROUS_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Запрашиваем все необходимые разрешения разом
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS
            );
        } else {
            // Все разрешения уже предоставлены
            proceedWithAppInitialization();
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ требует отдельное разрешение для уведомлений
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
            }
        } else {
            // Для более старых версий проверяем через NotificationManager
            NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                showNotificationPermissionDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            Map<String, Boolean> permissionResults = new HashMap<>();
            
            // Проверяем результаты для каждого разрешения
            for (int i = 0; i < permissions.length; i++) {
                permissionResults.put(permissions[i], 
                    grantResults[i] == PackageManager.PERMISSION_GRANTED);
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }

            if (allGranted) {
                // Все разрешения получены
                proceedWithAppInitialization();
            } else {
                // Показываем объяснение для отклоненных разрешений
                handleDeniedPermissions(permissionResults);
            }
        } else if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && 
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Уведомления разрешены
                initializeNotifications();
            } else {
                // Показываем объяснение важности уведомлений
                showNotificationPermissionDialog();
            }
        }
    }

    private void handleDeniedPermissions(Map<String, Boolean> permissionResults) {
        boolean shouldShowRationale = false;
        StringBuilder deniedPermissionsMessage = new StringBuilder();

        for (Map.Entry<String, Boolean> entry : permissionResults.entrySet()) {
            if (!entry.getValue()) {
                String permissionName = getReadablePermissionName(entry.getKey());
                deniedPermissionsMessage.append("- ").append(permissionName).append("\n");
                
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, entry.getKey())) {
                    shouldShowRationale = true;
                }
            }
        }

        if (shouldShowRationale) {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_required_title))
                .setMessage(getString(R.string.permission_required_message, 
                    deniedPermissionsMessage.toString()))
                .setPositiveButton(getString(R.string.try_again), 
                    (dialog, which) -> handleAndroid6Permissions())
                .setNegativeButton(getString(R.string.proceed_anyway), 
                    (dialog, which) -> proceedWithAppInitialization())
                .show();
        } else {
            // Пользователь выбрал "Больше не спрашивать"
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_denied_title))
                .setMessage(getString(R.string.permission_denied_message))
                .setPositiveButton(getString(R.string.open_settings), 
                    (dialog, which) -> openAppSettings())
                .setNegativeButton(getString(R.string.proceed_anyway), 
                    (dialog, which) -> proceedWithAppInitialization())
                .show();
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private String getReadablePermissionName(String permission) {
        // Преобразуем системное имя разрешения в читаемое
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return getString(R.string.permission_storage_read);
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return getString(R.string.permission_storage_write);
            case Manifest.permission.CAMERA:
                return getString(R.string.permission_camera);
            default:
                return permission;
        }
    }

    private void proceedWithAppInitialization() {
        setContentView(R.layout.activity_main);

        // Инициализация NetworkManager
        networkManager = new NetworkManager(this);
        networkManager.getNetworkStateLiveData().observe(this, this::handleNetworkStateChange);

        // Проверка состояния сети
        if (networkManager.isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.network_connection_good), 
                Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.network_connection_none), 
                Toast.LENGTH_LONG).show();
        }

        // Инициализация навигации
        NavHost navHost = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        if (navHost != null) {
            navController = navHost.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.notification_permission_title)
            .setMessage(R.string.notification_permission_message)
            .setPositiveButton(R.string.settings, (dialog, which) -> {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                }
                startActivity(intent);
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void handleNetworkStateChange(NetworkState state) {
        String message;
        switch (state) {
            case CONNECTED_WIFI:
                message = getString(R.string.network_status_wifi);
                // Пример: загрузить тяжелый контент
                loadContent(true);
                break;
            case CONNECTED_MOBILE:
                message = getString(R.string.network_status_mobile);
                // Пример: загрузить легкий контент
                loadContent(false);
                break;
            case CONNECTED_ETHERNET:
                message = getString(R.string.network_status_ethernet);
                // Пример: загрузить тяжелый контент
                loadContent(true);
                break;
            case DISCONNECTED:
                message = getString(R.string.network_status_disconnected);
                // Пример: показать офлайн контент
                showOfflineContent();
                break;
            default:
                message = getString(R.string.network_status_checking);
                break;
        }
        
        // Показываем состояние сети пользователю
        showNetworkStatus(message);
    }

    private void showNetworkStatus(String message) {
        // Показываем статус в нижней части экрана
        Snackbar.make(findViewById(android.R.id.content), 
                     message, 
                     Snackbar.LENGTH_SHORT).show();
    }

    private void loadContent(boolean isHighBandwidth) {
        // Пример загрузки контента в зависимости от типа соединения
        if (isHighBandwidth) {
            // Загружаем HD изображения, видео и т.д.
            loadHighQualityContent();
        } else {
            // Загружаем оптимизированный контент
            loadOptimizedContent();
        }
    }

    private void loadHighQualityContent() {
        // Пример: загрузка HD контента
        // imageView.setImageQuality(HIGH);
        // videoPlayer.setQuality(HD);
    }

    private void loadOptimizedContent() {
        // Пример: загрузка оптимизированного контента
        // imageView.setImageQuality(MEDIUM);
        // videoPlayer.setQuality(SD);
    }

    private void showOfflineContent() {
        // Пример: показ офлайн контента
        // loadCachedData();
        // showOfflineMessage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_MANAGE_STORAGE = 101;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 102;
}