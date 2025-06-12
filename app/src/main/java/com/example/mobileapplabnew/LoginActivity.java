package com.example.mobileapplabnew;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    private TextView sidDisplay;
    private EditText usernameInput, passwordInput;
    private Button loginButton;
    private ImageView backgroundImage;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        sidDisplay = findViewById(R.id.sidDisplay);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        backgroundImage = findViewById(R.id.backgroundImage);

        // Replace with your actual SID
        sidDisplay.setText("Your SID: [Your Student ID]");
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database
        boolean isInserted = databaseHelper.insertUser(username, password);

        if (isInserted) {
            Toast.makeText(this, "Login data saved successfully!", Toast.LENGTH_LONG).show();
            // Navigate to Maps Activity
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
        } else {
            Toast.makeText(this, "Failed to save login data", Toast.LENGTH_SHORT).show();
        }
    }
}