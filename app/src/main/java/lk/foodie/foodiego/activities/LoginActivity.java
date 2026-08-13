package lk.foodie.foodiego.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.foodie.foodiego.AdminActivity;
import lk.foodie.foodiego.MainActivity;
import lk.foodie.foodiego.R;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView forgetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        forgetpassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();

        Button siginin = findViewById(R.id.button);

        TextView login_register = findViewById(R.id.login_register);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = findViewById(R.id.editText2);
                String email = editTextEmail.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(LoginActivity.this, "Reset link sent!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        siginin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editTextEmail = findViewById(R.id.editText2);
                EditText editTextPassword = findViewById(R.id.editText3);

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email and Password are required!", Toast.LENGTH_SHORT).show();
                    triggerVibration();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    fetchUserData(user.getUid());  // Fetch user data from Firestore
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                triggerVibration();
                            }
                        });
            }
        });
    }

    private void fetchUserData(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore
                .collection("user")
                .document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String status = documentSnapshot.getString("status");

                        Log.i("FoodieGo", "User Data: " + fullName + " | " + email + " | Status: " + status);

                        Toast.makeText(LoginActivity.this, "Welcome " + fullName + "!", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if ("admin".equalsIgnoreCase(status)) {

                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                        } else {

                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }

                        intent.putExtra("firstName", fullName);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void triggerVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

}