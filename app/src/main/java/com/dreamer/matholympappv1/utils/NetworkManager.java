package com.dreamer.matholympappv1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkManager {
    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<NetworkState> networkStateLiveData;

    public enum NetworkState {
        CONNECTED_WIFI,      // Подключено через WiFi
        CONNECTED_MOBILE,    // Подключено через мобильную сеть
        CONNECTED_ETHERNET,  // Подключено через Ethernet
        DISCONNECTED,        // Нет подключения
        CONNECTING          // В процессе подключения
    }

    public NetworkManager(Context context) {
        connectivityManager = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkStateLiveData = new MutableLiveData<>();

        // Начальное состояние
        updateNetworkState();

        // Регистрируем callback для отслеживания изменений сети
        registerNetworkCallback();
    }

    private void registerNetworkCallback() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                updateNetworkState();
            }

            @Override
            public void onLost(@NonNull Network network) {
                networkStateLiveData.postValue(NetworkState.DISCONNECTED);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network,
                                            @NonNull NetworkCapabilities capabilities) {
                updateNetworkState();
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void updateNetworkState() {
        NetworkState state = getCurrentNetworkState();
        networkStateLiveData.postValue(state);
    }

    public NetworkState getCurrentNetworkState() {
        if (connectivityManager == null) return NetworkState.DISCONNECTED;

        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = connectivityManager.getActiveNetwork();
        }
        if (activeNetwork == null) return NetworkState.DISCONNECTED;

        NetworkCapabilities capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) return NetworkState.DISCONNECTED;

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return NetworkState.CONNECTED_WIFI;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return NetworkState.CONNECTED_MOBILE;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return NetworkState.CONNECTED_ETHERNET;
        }

        return NetworkState.DISCONNECTED;
    }

    public boolean isNetworkAvailable() {
        NetworkState currentState = getCurrentNetworkState();
        return currentState != NetworkState.DISCONNECTED;
    }

    public boolean isWifiConnected() {
        return getCurrentNetworkState() == NetworkState.CONNECTED_WIFI;
    }

    public boolean isMobileConnected() {
        return getCurrentNetworkState() == NetworkState.CONNECTED_MOBILE;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    // Получить информацию о текущем состоянии сети в виде строки
    public String getNetworkInfo() {
        NetworkState state = getCurrentNetworkState();
        StringBuilder info = new StringBuilder();

        switch (state) {
            case CONNECTED_WIFI:
                info.append("Подключено через WiFi\n");
                // Можно добавить дополнительную информацию о WiFi соединении
                break;
            case CONNECTED_MOBILE:
                info.append("Подключено через мобильную сеть\n");
                // Можно добавить информацию о типе мобильной сети (3G/4G/5G)
                break;
            case CONNECTED_ETHERNET:
                info.append("Подключено через Ethernet\n");
                break;
            case DISCONNECTED:
                info.append("Нет подключения к сети\n");
                break;
            case CONNECTING:
                info.append("Устанавливается соединение...\n");
                break;
        }

        return info.toString();
    }
}
