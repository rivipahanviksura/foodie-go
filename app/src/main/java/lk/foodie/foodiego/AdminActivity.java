package lk.foodie.foodiego;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import lk.foodie.foodiego.activities.LoginActivity;
import lk.foodie.foodiego.fragments.CustomerFragment;
import lk.foodie.foodiego.DashboardFragment;
import lk.foodie.foodiego.FoodsFragment;


public class AdminActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.nav_dashboard){
                    loadFragment(new DashboardFragment());
                } else if (item.getItemId()==R.id.nav_orders) {
                    loadFragment(new OrdersFragment());
                }else if (item.getItemId()==R.id.nav_foods) {
                    loadFragment(new FoodsFragment());
                }else if (item.getItemId()==R.id.nav_customers) {
                    loadFragment(new CustomerFragment());
                }else if (item.getItemId()==R.id.nav_logout) {
                    showLogoutDialog();
                }

                return true;
            }});
    }
    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, null)
                .setReorderingAllowed(true)
                .commit();
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Log.d("Logout", "User signed out");

        // Redirect to LoginActivity and clear back stack
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}