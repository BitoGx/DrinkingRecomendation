package id.web.bitocode.drinkingrecomendation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.Rationale.PermissionRationalActivity;
import id.web.bitocode.drinkingrecomendation.Service.BackgroundDetectedActivitiesService;
import id.web.bitocode.drinkingrecomendation.config.Constants;
import id.web.bitocode.drinkingrecomendation.util.DirectionsJSONParser;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;

import static id.web.bitocode.drinkingrecomendation.config.Constants.MAPVIEW_BUNDLE_KEY;

public class ActivityRecognitionActivity extends AppCompatActivity implements OnMapReadyCallback
{
  private BroadcastReceiver broadcastReceiver;
  private int totalwaktu, totalwaktujalan, tempwaktujalan, tempwaktulari, totalwaktulari, rekomendasi, weight, height, weightcategory;
  private final boolean runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
  private String rekomendasiawal, response, jaraktempuh;
  private double longitude, latitude;
  private Boolean active, running, wasRunning;
  private TextView tvactivity, tvconfidence, tvtimeview, tvwalkingtime, tvrunningtime,tvactivityrecogrekomendasi;
  private ImageView imgactivity;
  private Button btnstart;
  private Handler handler;
  private GoogleMap googlemap;
  private MapView mMapView;
  private LatLng userlocation;
  private Polyline mPolyline;
  private Button btnactivityrecognitionstop;
  private final float zoomLevel = 16.0f;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activityrecognition);

    setUpActionBar();
    inisialisasi();

    checkActivityRecognitionPermission();
    initGoogleMap(savedInstanceState);
  }

  private void setUpActionBar()
  {
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle(R.string.menu_activity_recognition);
  }

  private int checkBMI(float bmi, String jk)
  {
    /*
    Kurus = 0
    Normal = 1
    Obesitas = 2
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
      else
      {
        return 2;
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
      else
      {
        return 2;
      }
    }
  }

  private float countBMI()
  {
    float heightinmeter = (float) height / 100;
    double bmi = weight / Math.pow(heightinmeter, 2);

    return (float) bmi;
  }

  private void inisialisasi()
  {
    Intent intent = getIntent();
    jaraktempuh     = intent.getStringExtra("jarak");
    rekomendasiawal = intent.getStringExtra("rekomendasiawal");
    response        = intent.getStringExtra("route");

    handler = new Handler();

    SessionUtil sessionUtil = new SessionUtil(this);
    weight = Integer.parseInt(sessionUtil.getLoggedUser(this).getBeratbadan());
    height = Integer.parseInt(sessionUtil.getLoggedUser(this).getTinggibadan());
    String jeniskelamin = sessionUtil.getLoggedUser(this).getJeniskelamin();
    weightcategory = checkBMI(countBMI(), jeniskelamin);

    btnstart = findViewById(R.id.btn_activityrecog_start);

    active = false;
    running = false;
    wasRunning = false;

    totalwaktu = 0;
    totalwaktulari = 0;
    tempwaktulari = 0;
    totalwaktujalan = 0;
    tempwaktujalan = 0;
    rekomendasi = 0;

    btnactivityrecognitionstop = findViewById(R.id.btn_activityrecog_stop);
    btnactivityrecognitionstop.setVisibility(View.GONE);

    tvactivity = findViewById(R.id.tv_activityrecog_activity);
    tvconfidence = findViewById(R.id.tv_activityrecog_confidence);
    tvtimeview = findViewById(R.id.tv_activityrecog_time_view);
    tvwalkingtime = findViewById(R.id.tv_activityrecog_walking_time);
    tvrunningtime = findViewById(R.id.tv_activityrecog_running_time);
    tvactivityrecogrekomendasi = findViewById(R.id.tv_activityrecog_rekomendasi);

    imgactivity = findViewById(R.id.img_activityrecog_activity);
  }

  @SuppressLint("InlinedApi")
  private boolean activityRecognitionPermissionApproved()
  {
    if (runningQOrLater)
    {
      return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACTIVITY_RECOGNITION
      );
    }
    else
    {
      return true;
    }
  }

  public void checkActivityRecognitionPermission()
  {
    if (activityRecognitionPermissionApproved())
    {
      runTimer();
      BroadcastReceiverListener();
    }
    else
    {
      Intent startIntent = new Intent(this, PermissionRationalActivity.class);
      startActivityForResult(startIntent, 0);
    }
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
      tvactivity.setText(label);
      tvconfidence.setText(getString(R.string.confidence, confidence));
      imgactivity.setImageResource(icon);
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

  private void runTimer()
  {
    handler.post(new Runnable()
    {
      @SuppressLint("SetTextI18n")
      @Override
      public void run()
      {
        int totalHours = totalwaktu / 3600;
        int totalMinutes = (totalwaktu % 3600) / 60;
        int totalSecs = totalwaktu % 60;
        String totalTime = String.format(Locale.getDefault(),
                                         "%d:%02d:%02d", totalHours,
                                         totalMinutes, totalSecs);
        tvtimeview.setText(totalTime);

        int walkingHours = totalwaktujalan / 3600;
        int walkingMinutes = (totalwaktujalan % 3600) / 60;
        int walkingSecs = totalwaktujalan % 60;
        String walkingTime = String.format(Locale.getDefault(),
                                           "%d:%02d:%02d", walkingHours,
                                           walkingMinutes, walkingSecs);
        tvwalkingtime.setText(walkingTime);

        int runningHours = totalwaktulari / 3600;
        int runningMinutes = (totalwaktulari % 3600) / 60;
        int runningSecs = totalwaktulari % 60;
        String runningTime = String.format(Locale.getDefault(),
                                           "%d:%02d:%02d", runningHours,
                                           runningMinutes, runningSecs);
        tvrunningtime.setText(runningTime);

        if (running)
        {
          totalwaktu++;
          if (tvactivity.getText().toString().equals(getString(R.string.activity_running)))
          {
            totalwaktulari++;
            tempwaktulari++;
          }
          if (tvactivity.getText().toString().equals(getString(R.string.activity_walking)))
          {
            totalwaktujalan++;
            tempwaktujalan++;
          }
          if (totalwaktu >= 900)
          {
            if ((totalwaktu % 900) == 0)
            {
              if (tempwaktulari >= tempwaktujalan)
              {
                tempwaktulari = 0;
                rekomendasi += 200;
              }
              else
              {
                tempwaktujalan = 0;
                rekomendasi += 150;
              }
              tvactivityrecogrekomendasi.setText(Integer.toString(rekomendasi));
            }
          }
        }
        handler.postDelayed(this, 1000);
      }
    });
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

  @Override
  public void onSaveInstanceState(@NonNull Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("totalwaktu", totalwaktu);
    savedInstanceState.putInt("totalwaktulari", totalwaktulari);
    savedInstanceState.putInt("totalwaktujalan", totalwaktujalan);
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
      btnactivityrecognitionstop.setVisibility(View.VISIBLE);
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
    totalwaktu = 0;
    totalwaktujalan = 0;
    totalwaktulari = 0;
    tempwaktujalan = 0;
    tempwaktulari = 0;
    rekomendasi = 0;
    btnstart.setText(R.string.start);
    stopTracking();
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

  private boolean checkMapPermission()
  {
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  private void drawRoute()
  {
    ActivityRecognitionActivity.ParserTask parserTask = new ActivityRecognitionActivity.ParserTask();
    parserTask.execute(response);
  }

  @SuppressLint("StaticFieldLeak")
  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
  {

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
    {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;
      try
      {
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();
        routes = parser.parse(jObject);
      } catch (Exception e)
      {
        e.printStackTrace();
      }
      return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result)
    {
      ArrayList<LatLng> points;
      PolylineOptions lineOptions = null;

      for (int i = 0; i < result.size(); i++)
      {
        points = new ArrayList<>();
        lineOptions = new PolylineOptions();

        List<HashMap<String, String>> path = result.get(i);

        for (int j = 0; j < path.size(); j++)
        {
          HashMap<String, String> point = path.get(j);

          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.color(Color.RED);
      }

      if (lineOptions != null)
      {
        if (mPolyline != null)
        {
          mPolyline.remove();
        }
        mPolyline = googlemap.addPolyline(lineOptions);
      }
      else
      {
        Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onMapReady(GoogleMap map)
  {
    googlemap = map;
    if(checkMapPermission())
    {
      map.setMyLocationEnabled(true);
      FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
      {
        @Override
        public void onSuccess(Location location)
        {
          if (location != null)
          {
            longitude = location.getLongitude();
            latitude  = location.getLatitude();
            userlocation = new LatLng(latitude, longitude);
            googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, zoomLevel));
          }
        }
      });
    }
    else
    {
      Log.e("CheckPermission", "Request Permission" );
    }
    drawRoute();
  }

  private double checkObesity(Integer rekomendasi)
  {
    if(weightcategory == 2)
    {
      return rekomendasi + (rekomendasi * 0.008);
    }
    else
    {
      return rekomendasi;
    }
  }

  public void onActivityRecognitionStopClick(View view)
  {
    Intent intent = new Intent(this, SimpanRekomendasiActivity.class);
    intent.putExtra("jarak", jaraktempuh);
    intent.putExtra("waktu", Integer.toString(totalwaktu));
    intent.putExtra("rekomendasiawal", rekomendasiawal);
    String rekomendasiakhir = String.valueOf((int) checkObesity(rekomendasi));
    intent.putExtra("rekomendasiakhir", rekomendasiakhir);
    startActivity(intent);
    finish();
  }
}