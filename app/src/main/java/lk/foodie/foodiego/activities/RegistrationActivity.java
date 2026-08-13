package lk.foodie.foodiego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import lk.foodie.foodiego.MainActivity;
import lk.foodie.foodiego.R;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        EditText editTextFullname = findViewById(R.id.editText);
        EditText editTextEmail = findViewById(R.id.editText2);
        EditText editTextPassword = findViewById(R.id.editText3);
        EditText editTextMobile = findViewById(R.id.mnumber);

        Button registerButton = findViewById(R.id.button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = String.valueOf(editTextFullname.getText());
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String mobile = String.valueOf(editTextMobile.getText());

// Validation
                if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegistrationActivity.this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(RegistrationActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully registered
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                                // Set the display name
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullname) // Set the user's display name
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    // Successfully updated profile
                                                    // Save user data to Firestore
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("fullName", fullname);
                                                    userData.put("mobile", mobile);
                                                    userData.put("email", email);
                                                    userData.put("status", "user");
                                                    userData.put("uid", user.getUid());

                                                    firestore.collection("user").document(user.getUid()).set(userData)
                                                            .addOnSuccessListener(unused -> {
                                                                Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(RegistrationActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    // Profile update failed
                                                    Toast.makeText(RegistrationActivity.this, "Profile update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Registration failed
                                    Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }


    public void login(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    public void mainActivity(View view) {
        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
    }
}