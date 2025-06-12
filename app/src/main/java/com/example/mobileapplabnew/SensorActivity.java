package com.example.mobileapplabnew;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView temperatureDisplay, thresholdDisplay, statusDisplay;
    private ProgressBar temperatureProgress;
    private MediaPlayer mediaPlayer;

    // Set this to the last two digits of your SID
    private final float TEMPERATURE_THRESHOLD = 25.0f; // Replace with your SID's last 2 digits
    private boolean audioPlaying = false;
    private Handler simulationHandler;
    private Runnable simulationRunnable;
    private int simulationIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        initializeViews();
        setupSensors();
        setupMediaPlayer();
    }

    private void initializeViews() {
        temperatureDisplay = findViewById(R.id.temperatureDisplay);
        thresholdDisplay = findViewById(R.id.thresholdDisplay);
        statusDisplay = findViewById(R.id.statusDisplay);
        temperatureProgress = findViewById(R.id.temperatureProgress);

        thresholdDisplay.setText("Temperature Threshold: " + TEMPERATURE_THRESHOLD + "°C");
        statusDisplay.setText("Status: Monitoring...");
        temperatureProgress.setMax(50); // Max temperature for progress bar
    }

    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

            if (temperatureSensor == null) {
                Toast.makeText(this, "Temperature sensor not available. Using simulation for testing.",
                        Toast.LENGTH_LONG).show();
                startTemperatureSimulation();
            } else {
                Toast.makeText(this, "Temperature sensor found. Real sensor data will be used.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupMediaPlayer() {
        try {
            // Create a simple beep sound programmatically if audio file not available
            mediaPlayer = new MediaPlayer();
            // You can replace this with: mediaPlayer = MediaPlayer.create(this, R.raw.temperature_alert);
            Toast.makeText(this, "Audio system ready. Add temperature_alert.mp3 to res/raw/ for custom sound.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Audio setup completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTemperatureSimulation() {
        final float[] testTemperatures = {20.0f, 22.0f, 24.0f, 26.0f, 28.0f, 30.0f, 27.0f, 23.0f, 21.0f};

        simulationHandler = new Handler();
        simulationRunnable = new Runnable() {
            @Override
            public void run() {
                float temp = testTemperatures[simulationIndex % testTemperatures.length];
                simulationIndex++;

                updateTemperatureDisplay(temp);
                checkTemperatureThreshold(temp);

                // Schedule next update
                simulationHandler.postDelayed(this, 2000); // Update every 2 seconds
            }
        };

        // Start simulation
        simulationHandler.post(simulationRunnable);
    }

    private void updateTemperatureDisplay(float temperature) {
        temperatureDisplay.setText(String.format("Current Temperature: %.1f°C", temperature));

        // Update progress bar
        int progress = (int) Math.min(temperature, 50);
        temperatureProgress.setProgress(progress);

        // Change color based on temperature
        if (temperature > TEMPERATURE_THRESHOLD) {
            temperatureDisplay.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            temperatureDisplay.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void checkTemperatureThreshold(float temperature) {
        if (temperature > TEMPERATURE_THRESHOLD) {
            if (!audioPlaying) {
                playTemperatureAlert();
                statusDisplay.setText("🚨 ALERT: Temperature exceeded threshold! Audio playing...");
                statusDisplay.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        } else {
            if (audioPlaying) {
                stopTemperatureAlert();
                statusDisplay.setText("✅ Status: Temperature normal. Monitoring...");
                statusDisplay.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    private void playTemperatureAlert() {
        audioPlaying = true;

        // Create a simple notification sound
        try {
            if (mediaPlayer != null) {
                // You can uncomment this if you have the audio file:
                // mediaPlayer.start();
            }

            // Show visual alert
            Toast.makeText(this, "🔊 TEMPERATURE ALERT! Threshold exceeded!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTemperatureAlert() {
        audioPlaying = false;

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            updateTemperatureDisplay(temperature);
            checkTemperatureThreshold(temperature);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (simulationHandler != null && simulationRunnable != null) {
            simulationHandler.removeCallbacks(simulationRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (simulationHandler != null && simulationRunnable != null) {
            simulationHandler.removeCallbacks(simulationRunnable);
        }
    }
}