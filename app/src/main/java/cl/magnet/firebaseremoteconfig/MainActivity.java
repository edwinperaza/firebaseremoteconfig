package cl.magnet.firebaseremoteconfig;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends AppCompatActivity {

    public static String WELCOME_MESSAGE = "welcome_message";
    public static String TOOLBAR_TITLE_COLOR = "toolbar_title_color";
    public static String TOOLBAR_BACKGROUND_COLOR = "toolbar_background_color";
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    long cacheExpiration = 0L;
    TextView mLandingMessageTextView;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        mLandingMessageTextView = (TextView) findViewById(R.id.tv_landing_message);

        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            displayWelcomeMessage();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * Set initial value to toolbar (Title, Visibility, Arrow Icon)
     */
    private void setupToolbar(int bgColor, int titleColor) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if ((bgColor != 0) && (titleColor != 0)) {
            mToolbar.setTitleTextColor(titleColor);
            mToolbar.setBackgroundColor(bgColor);
        } else {
            mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }

    public void displayWelcomeMessage() {
        String welcomeMessage = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE);
        String tbTitleColor = mFirebaseRemoteConfig.getString(TOOLBAR_TITLE_COLOR);
        String tbBackgroundColor = mFirebaseRemoteConfig.getString(TOOLBAR_BACKGROUND_COLOR);

        int bgColor = Color.parseColor(tbBackgroundColor);
        int titleColor = Color.parseColor(tbTitleColor);
        setupToolbar(bgColor, titleColor);
        mLandingMessageTextView.setText(welcomeMessage);
    }
}
