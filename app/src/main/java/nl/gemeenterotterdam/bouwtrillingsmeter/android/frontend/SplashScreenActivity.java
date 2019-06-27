package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * Show on startup. This initializes the
 * {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend} and uses that
 * time to validate our phones hardware.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}
