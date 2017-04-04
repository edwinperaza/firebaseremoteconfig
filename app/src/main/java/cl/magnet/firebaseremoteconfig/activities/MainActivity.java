package cl.magnet.firebaseremoteconfig.activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import cl.magnet.firebaseremoteconfig.R;
import cl.magnet.firebaseremoteconfig.utils.Constant;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    long cacheExpiration = 60;
    TextView mLandingMessageTextView;

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
                            Log.d(TAG, "Fetch Succeeded");
                            mFirebaseRemoteConfig.activateFetched();
                            displayWelcomeMessage();
                        } else {
                            Log.d(TAG, "Fetch Failed");
                        }

                    }
                });
    }

    /**
     * Set initial value to toolbar (Title, Background Color)
     */
    private void setupToolbar(int bgColor, int titleColor) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if ((bgColor != 0) && (titleColor != 0)) {
            mToolbar.setTitleTextColor(titleColor);
            mToolbar.setBackgroundColor(bgColor);
        } else {
            mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }

    /**
     * Display message with data from firebase server
     */
    public void displayWelcomeMessage() {
        String welcomeMessage = mFirebaseRemoteConfig.getString(Constant.WELCOME_MESSAGE);
        String tbTitleColor = mFirebaseRemoteConfig.getString(Constant.TOOLBAR_TITLE_COLOR);
        String tbBackgroundColor = mFirebaseRemoteConfig.getString(Constant.TOOLBAR_BACKGROUND_COLOR);

        int bgColor = Color.parseColor(tbBackgroundColor);
        int titleColor = Color.parseColor(tbTitleColor);
        setupToolbar(bgColor, titleColor);
        mLandingMessageTextView.setText(welcomeMessage);
    }
}
