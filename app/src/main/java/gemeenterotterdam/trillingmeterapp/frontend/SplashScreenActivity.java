package gemeenterotterdam.trillingmeterapp.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gemeenterotterdam.trillingmeterapp.R;

/**
 * This launches our splash screen.
 * This activity has no history.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_INTERVAL = 2000;
    private static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        active = true;

        ImageView imageView = findViewById(R.id.imageViewSplashScreen);
        imageView.setOnClickListener((View v) ->
                onClickAny()
        );

        startTimer();
    }

    /**
     * This will start a timer.
     */
    private void startTimer() {
        ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(1);
        scheduler.schedule(() -> {
                    onClickAny();
                    scheduler.shutdown();
                },
                SPLASH_SCREEN_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * This launches the main activity and
     * closes this activity. No history will
     * be present for this activity.
     */
    void onClickAny() {
        if (active) {
            active = false;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
