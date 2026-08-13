package lk.foodie.foodiego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class wifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Toast.makeText(context, "WiFi Enabled", Toast.LENGTH_SHORT).show();
            } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Toast.makeText(context, "WiFi Disabled", Toast.LENGTH_SHORT).show();
            }
        }

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (networkInfo != null && networkInfo.isConnected()) {
                Toast.makeText(context, "Connected to WiFi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Disconnected from WiFi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
