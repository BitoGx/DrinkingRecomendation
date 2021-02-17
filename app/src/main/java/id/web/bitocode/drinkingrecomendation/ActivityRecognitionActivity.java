package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import id.web.bitocode.drinkingrecomendation.Service.BackgroundDetectedActivitiesService;
import id.web.bitocode.drinkingrecomendation.config.Constants;

public class ActivityRecognitionActivity extends AppCompatActivity
{
  
  private String TAG = ActivityRecognitionActivity.class.getSimpleName();
  BroadcastReceiver broadcastReceiver;
  private Boolean running;
  private TextView txtActivity, txtConfidence;
  private ImageView imgActivity;
  private Button btnTracking;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activityrecognition);
  
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle(R.string.menu_activity_recognition);
    
    running = false;
    txtActivity = findViewById(R.id.txt_activity);
    txtConfidence = findViewById(R.id.txt_confidence);
    imgActivity = findViewById(R.id.img_activity);
    btnTracking = findViewById(R.id.btn_start_tracking);
  
    btnTracking.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!running)
        {
          startTracking();
        }
        else
        {
          stopTracking();
        }
      }
    });
    
    broadcastReceiver = new BroadcastReceiver()
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {
        if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY))
        {
          int type = intent.getIntExtra("type", -1);
          int confidence = intent.getIntExtra("confidence", 0);
          handleUserActivity(type, confidence);
        }
      }
    };
    startTracking();
  }
  
  private void handleUserActivity(int type, int confidence)
  {
    String label = getString(R.string.activity_unknown);
    int icon = R.drawable.ic_still;
    
    switch (type) {
      case DetectedActivity.ON_BICYCLE: {
        label = getString(R.string.activity_on_bicycle);
        icon = R.drawable.ic_on_bicycle;
        break;
      }
      case DetectedActivity.RUNNING: {
        label = getString(R.string.activity_running);
        icon = R.drawable.ic_running;
        break;
      }
      case DetectedActivity.STILL: {
        label = getString(R.string.activity_still);
        break;
      }
      case DetectedActivity.WALKING: {
        label = getString(R.string.activity_walking);
        icon = R.drawable.ic_walking;
        break;
      }
      case DetectedActivity.UNKNOWN: {
        label = getString(R.string.activity_unknown);
        break;
      }
    }
    
    Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);
    
    if (confidence > Constants.CONFIDENCE) {
      txtActivity.setText(label);
      txtConfidence.setText("Confidence: " + confidence);
      imgActivity.setImageResource(icon);
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
  }
  
  private void startTracking() {
    Intent intent = new Intent(ActivityRecognitionActivity.this, BackgroundDetectedActivitiesService.class);
    running = true;
    btnTracking.setText(R.string.stop_tracking);
    startService(intent);
  }
  
  private void stopTracking() {
    Intent intent = new Intent(ActivityRecognitionActivity.this, BackgroundDetectedActivitiesService.class);
    running = false;
    btnTracking.setText(R.string.start_tracking);
    stopService(intent);
  }
}