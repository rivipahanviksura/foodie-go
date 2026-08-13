package lk.foodie.foodiego;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import lk.foodie.foodiego.activities.LoginActivity;
import lk.foodie.foodiego.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private TextView usernameTextView, emailTextView;
    Button btn_sign_out;
    NavController navController;

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String phoneNumber = "0382252154";

    private WifiReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        Button callButton = findViewById(R.id.makeCallButton);
        callButton.setOnClickListener(v -> makeCall());

        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.header_username); // Replace with your username TextView ID
        emailTextView = headerView.findViewById(R.id.header_email); // Replace with your email TextView ID
        btn_sign_out = findViewById(R.id.btn_sign_out);

        loadUserData();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        setupNavigation(drawer, navigationView);

        btn_sign_out.setOnClickListener(view -> signOut());

        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
    }

    private void setupNavigation(DrawerLayout drawer, NavigationView navigationView) {
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_daily_meal, R.id.nav_favourite, R.id.nav_my_cart, R.id.profileFragment)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.profileFragment) {
                navigateToProfile();
            } else {
                navController.navigate(id);
            }
            drawer.closeDrawers();
            return true;
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.profileFragment) {
                Bundle bundle = new Bundle();
                Intent intent = getIntent();
                String fullName = intent.getStringExtra("fullName");
                bundle.putString("fullName", fullName);

                navController.navigate(R.id.profileFragment, bundle);
            }
            navController.navigate(id);
            drawer.closeDrawers();
            return true;
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usernameTextView.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            emailTextView.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Email not available");
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToProfile() {
        Bundle bundle = new Bundle();
        String fullName = usernameTextView.getText().toString();
        bundle.putString("fullName", fullName);
        navController.navigate(R.id.profileFragment, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void makeCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                String message = "";
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        message = "WiFi is ENABLED";
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        message = "WiFi is DISABLED";
                        break;
                    default:
                        message = "WiFi state changed";
                        break;
                }

                showDialog(context, message);
            }

        }

        private void showDialog(Context context, String message) {
            // Ensure context is of type Activity to avoid window leak
            if (!(context instanceof android.app.Activity)) {
                return;
            }

            new AlertDialog.Builder(context)
                    .setTitle("WiFi Status")
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        }

    }
}
