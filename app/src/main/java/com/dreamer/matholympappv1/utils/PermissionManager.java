package com.dreamer.matholympappv1.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private static final int PERMISSION_REQUEST_CODE = 123;
    
    public interface PermissionCallback {
        void onPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) 
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, PermissionCallback callback, 
                                        String... permissions) {
        if (activity == null) return;

        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsNeeded.toArray(new String[0]),
                PERMISSION_REQUEST_CODE
            );
        } else {
            callback.onPermissionsGranted();
        }
    }

    public static void handlePermissionResult(int requestCode, String[] permissions,
                                            int[] grantResults, PermissionCallback callback) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            List<String> deniedPermissions = new ArrayList<>();
            
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }

            if (deniedPermissions.isEmpty()) {
                callback.onPermissionsGranted();
            } else {
                callback.onPermissionsDenied(deniedPermissions);
            }
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, 
                                                             String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
}
