package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.Service.BackgroundDetectedActivitiesService;
import id.web.bitocode.drinkingrecomendation.config.Constants;

public class ActivityRecognitionActivity extends AppCompatActivity
{
  BroadcastReceiver broadcastReceiver;
  private int seconds = 0;
  private String TAG = ActivityRecognitionActivity.class.getSimpleName();
  private Boolean active, running, wasRunning;
  private TextView tvactivity, tvconfidence,tvtimeview;
  private ImageView imgactivity;
  private Button btnstart;
  private Handler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activityrecognition);

    setUpActionBar();

    inisialisasi();
    runTimer();

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
  }

  private void setUpActionBar()
  {
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle(R.string.menu_activity_recognition);
  }

  private void inisialisasi()
  {
    handler = new Handler();

    btnstart = findViewById(R.id.btn_activityrecog_start);

    active     = false;
    running    = false;
    wasRunning = false;

    tvactivity   = findViewById(R.id.tv_activityrecog_activity);
    tvconfidence = findViewById(R.id.tv_activityrecog_confidence);
    tvtimeview   = findViewById(R.id.tv_activityrecog_time_view);

    imgactivity  = findViewById(R.id.img_activityrecog_activity);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("seconds", seconds);
    savedInstanceState.putBoolean("running", running);
    savedInstanceState.putBoolean("wasRunning", wasRunning);
  }

  public void onStartClick(View view)
  {
    if(!running && !active)
    {
      running = true;
      btnstart.setText(R.string.pause);
      startTracking();
    }
    else
    {
      running = false;
      btnstart.setText(R.string.start);
      stopTracking();
    }

  }

  public void onResetClick(View view)
  {
    running = false;
    seconds = 0;
    btnstart.setText(R.string.start);
    stopTracking();
  }

  private void runTimer()
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours,
                minutes, secs);

        tvtimeview.setText(time);

        if (running)
        {
          seconds++;
        }
        handler.postDelayed(this, 1000);
      }
    });
  }

  private void handleUserActivity(int type, int confidence)
  {
    String label = getString(R.string.activity_still);
    int icon = R.drawable.ic_still;

    switch (type)
    {
      case DetectedActivity.ON_BICYCLE:
      {
        label = getString(R.string.activity_on_bicycle);
        icon = R.drawable.ic_on_bicycle;
        break;
      }
      case DetectedActivity.RUNNING:
      {
        label = getString(R.string.activity_running);
        icon = R.drawable.ic_running;
        break;
      }
      case DetectedActivity.STILL:
      {
        label = getString(R.string.activity_still);
        break;
      }
      case DetectedActivity.WALKING:
      {
        label = getString(R.string.activity_walking);
        icon = R.drawable.ic_walking;
        break;
      }
    }

    Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

    if (confidence > Constants.CONFIDENCE)
    {
      tvactivity.setText(label);
      tvconfidence.setText("Confidence: " + confidence);
      imgactivity.setImageResource(icon);
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    if (wasRunning)
    {
      running = true;
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    wasRunning = running;
    running = false;
  }

  private void startTracking()
  {
    Intent intent = new Intent(ActivityRecognitionActivity.this, BackgroundDetectedActivitiesService.class);
    active = true;
    startService(intent);
  }

  private void stopTracking()
  {
    Intent intent = new Intent(ActivityRecognitionActivity.this, BackgroundDetectedActivitiesService.class);
    active = false;
    stopService(intent);
  }
}