package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.Service.BackgroundDetectedActivitiesService;
import id.web.bitocode.drinkingrecomendation.config.Constants;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;

import static id.web.bitocode.drinkingrecomendation.config.Constants.MAPVIEW_BUNDLE_KEY;

public class ActivityRecognitionActivity extends AppCompatActivity implements OnMapReadyCallback
{
  BroadcastReceiver broadcastReceiver;
  private int secondsfortotal, secondsforwalking, secondsforwalkingtemp, secondsforrunningtemp, secondsforrunning, needtodrink, weight, height, weightcategory;
  private String jeniskelamin;
  private Boolean active, running, wasRunning;
  private TextView tv_activity, tv_confidence, tv_timeview, tv_walkingtime, tv_runningtime;
  private ImageView imgactivity;
  private Button btnstart;
  private Handler handler;

  private MapView mMapView;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activityrecognition);

    setUpActionBar();

    inisialisasi();
    initGoogleMap(savedInstanceState);

    runTimer();
    BroadcastReceiverListener();
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
    SessionUtil sessionUtil = new SessionUtil(this);
    weight = Integer.parseInt(sessionUtil.getLoggedUser(this).getBeratbadan());
    height = Integer.parseInt(sessionUtil.getLoggedUser(this).getTinggibadan());
    jeniskelamin = sessionUtil.getLoggedUser(this).getJeniskelamin();
    weightcategory = checkBMI(countBMI(), jeniskelamin);

    btnstart = findViewById(R.id.btn_activityrecog_start);

    active = false;
    running = false;
    wasRunning = false;

    secondsfortotal = 0;
    secondsforrunning = 0;
    secondsforrunningtemp = 0;
    secondsforwalking = 0;
    secondsforwalkingtemp = 0;
    needtodrink = 0;

    tv_activity = findViewById(R.id.tv_activityrecog_activity);
    tv_confidence = findViewById(R.id.tv_activityrecog_confidence);
    tv_timeview = findViewById(R.id.tv_activityrecog_time_view);
    tv_walkingtime = findViewById(R.id.tv_activityrecog_walking_time);
    tv_runningtime = findViewById(R.id.tv_activityrecog_running_time);

    imgactivity = findViewById(R.id.img_activityrecog_activity);
  }

  private void initGoogleMap(Bundle savedInstanceState)
  {
    Bundle mapViewBundle = null;
    if (savedInstanceState != null)
    {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    }
    mMapView = (MapView) findViewById(R.id.mapView);
    mMapView.onCreate(mapViewBundle);

    mMapView.getMapAsync(this);
  }

  private float countBMI()
  {
    float heightinmeter = (float) height / 100;
    double bmi = weight / Math.pow(heightinmeter, 2);

    return (float) bmi;
  }

  private int checkBMI(float bmi, String jk)
  {
    /*
    Kurus = 0
    Normal = 1
    Kegemukan = 2
    Obesitas = 3
     */
    if (jk.equalsIgnoreCase("Pria"))
    {
      if (Float.compare(bmi, 17f) <= 0)
      {
        return 0;
      }
      else if (Float.compare(bmi, 17f) > 0 && Float.compare(bmi, 24f) <= 0)
      {
        return 1;
      }
      else if (Float.compare(bmi, 24f) > 0 && Float.compare(bmi, 26f) <= 0)
      {
        return 2;
      }
      else
      {
        return 3;
      }
    }
    else
    {
      if (Float.compare(bmi, 16f) <= 0)
      {
        return 0;
      }
      else if (Float.compare(bmi, 16f) > 0 && Float.compare(bmi, 22f) <= 0)
      {
        return 1;
      }
      else if (Float.compare(bmi, 22f) > 0 && Float.compare(bmi, 26f) <= 0)
      {
        return 2;
      }
      else
      {
        return 3;
      }
    }
  }

  private void BroadcastReceiverListener()
  {
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

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("secondsfortotal", secondsfortotal);
    savedInstanceState.putInt("secondsforrunning", secondsforrunning);
    savedInstanceState.putInt("secondsforwalking", secondsforwalking);
    savedInstanceState.putBoolean("running", running);
    savedInstanceState.putBoolean("wasRunning", wasRunning);

    Bundle mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    if (mapViewBundle == null)
    {
      mapViewBundle = new Bundle();
      savedInstanceState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
    }

    mMapView.onSaveInstanceState(mapViewBundle);
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

    if (confidence > Constants.CONFIDENCE)
    {
      tv_activity.setText(label);
      tv_confidence.setText(getString(R.string.confidence, confidence));
      imgactivity.setImageResource(icon);
    }
  }

  private void runTimer()
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        int totalHours = secondsfortotal / 3600;
        int totalMinutes = (secondsfortotal % 3600) / 60;
        int totalSecs = secondsfortotal % 60;
        String totalTime = String.format(Locale.getDefault(),
                                         "%d:%02d:%02d", totalHours,
                                         totalMinutes, totalSecs);

        tv_timeview.setText(totalTime);
        int walkingHours = secondsforwalking / 3600;
        int walkingMinutes = (secondsforwalking % 3600) / 60;
        int walkingSecs = secondsforwalking % 60;
        String walkingTime = String.format(Locale.getDefault(),
                                           "%d:%02d:%02d", walkingHours,
                                           walkingMinutes, walkingSecs);

        tv_walkingtime.setText(walkingTime);

        int runningHours = secondsforrunning / 3600;
        int runningMinutes = (secondsforrunning % 3600) / 60;
        int runningSecs = secondsforrunning % 60;
        String runningTime = String.format(Locale.getDefault(),
                                           "%d:%02d:%02d", runningHours,
                                           runningMinutes, runningSecs);

        tv_runningtime.setText(runningTime);

        if (running)
        {
          secondsfortotal++;
          if (tv_activity.getText().toString().equals(getString(R.string.activity_running)))
          {
            secondsforrunning++;
            secondsforrunningtemp++;
          }
          if (tv_activity.getText().toString().equals(getString(R.string.activity_walking)))
          {
            secondsforwalking++;
            secondsforwalkingtemp++;
          }
          if (secondsfortotal >= 900)
          {
            if ((secondsfortotal % 900) == 0)
            {
              if (secondsforrunningtemp >= secondsforwalkingtemp)
              {
                secondsforrunningtemp = 0;
                needtodrink += 150;
              }
              else
              {
                secondsforwalkingtemp = 0;
                needtodrink += 200;
              }
            }
          }
        }
        handler.postDelayed(this, 1000);
      }
    });
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
    mMapView.onResume();
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    mMapView.onPause();
    wasRunning = running;
    running = false;
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    mMapView.onStart();
  }

  @Override
  protected void onStop()
  {
    super.onStop();
    mMapView.onStop();
  }

  @Override
  public void onMapReady(GoogleMap map)
  {
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
      (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    {
      return;
    }
    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    double longitude = location.getLongitude();
    double latitude = location.getLatitude();

    Log.i("Check Me:", "Lat = " + latitude + " Long : " + longitude);
    LatLng user = new LatLng(latitude, longitude);
    map.addMarker(new MarkerOptions().position(user).title("That's You"));
    map.moveCamera(CameraUpdateFactory.newLatLng(user));
    map.setMyLocationEnabled(true);
  }

  @Override
  protected void onDestroy()
  {
    mMapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onLowMemory()
  {
    super.onLowMemory();
    mMapView.onLowMemory();
  }

  public void onStartClick(View view)
  {
    if (!running && !active)
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
    secondsfortotal = 0;
    secondsforwalking = 0;
    secondsforrunning = 0;
    secondsforwalkingtemp = 0;
    secondsforrunningtemp = 0;
    btnstart.setText(R.string.start);
    stopTracking();
  }

  public void onActivityRecognitionSimpanClick(View view)
  {
    Intent intent = new Intent(this, SimpanRekomendasiActivity.class);
    startActivity(intent);
    finish();
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