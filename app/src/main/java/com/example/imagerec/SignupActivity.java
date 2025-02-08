package com.example.imagerec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.imagerec.databinding.ActivitySignupBinding;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signupEmail.getText().toString().trim();
                String password = binding.signupPassword.getText().toString().trim();
                String confirmPassword = binding.signupConfirm.getText().toString().trim();

                // Validate fields
                boolean valid = true;

                // Email Validation
                if (email.isEmpty()) {
                    binding.signupEmail.setError("Email is required");
                    valid = false;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.signupEmail.setError("Please enter a valid email");
                    valid = false;
                } else {
                    binding.signupEmail.setError(null); // Clear any previous error
                }

                // Password Validation
                if (password.isEmpty()) {
                    binding.signupPassword.setError("Password is required");
                    valid = false;
                } else if (!isValidPassword(password)) {
                    binding.signupPassword.setError("Password must be at least 8 characters, include uppercase, lowercase, digit, and special character");
                    valid = false;
                } else {
                    binding.signupPassword.setError(null); // Clear any previous error
                }

                // Confirm Password Validation
                if (confirmPassword.isEmpty()) {
                    binding.signupConfirm.setError("Please confirm your password");
                    valid = false;
                } else if (!password.equals(confirmPassword)) {
                    binding.signupConfirm.setError("Passwords do not match!");
                    valid = false;
                } else {
                    binding.signupConfirm.setError(null); // Clear any previous error
                }

                // Proceed if all validations pass
                if (valid) {
                    if (!databaseHelper.checkEmail(email)) {
                        boolean insert = databaseHelper.insertData(email, password);
                        if (insert) {
                            Toast.makeText(SignupActivity.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    // Password Validation Function
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }
}