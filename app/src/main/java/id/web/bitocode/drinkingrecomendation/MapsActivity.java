package id.web.bitocode.drinkingrecomendation;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.web.bitocode.drinkingrecomendation.util.DirectionsJSONParser;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

  private GoogleMap googlemap;
  private LatLng mOrigin;
  private LatLng mDestination;
  private LatLng userlocation;
  private Polyline mPolyline;
  private MarkerOptions options;
  private float zoomLevel = 16.0f;
  private LinearLayout layoutdetail,layoutrekomendasi;
  private TextView tvjarak, tvwaktu, tvinfo, tvrekomendasijarak, tvrekomendasiwaktu, tvrekomendasimedian;
  private EditText etjarakkm, etwaktujam, etwaktumenit;
  private int rekomendasi;
  Double dist = 0.0;
  ArrayList<LatLng> markerList;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_maps);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map);

    mapFragment.getMapAsync(this);
    inisialisasi();
  }

  private void inisialisasi()
  {
    options = new MarkerOptions();
    markerList = new ArrayList<>();

    layoutdetail      = findViewById(R.id.layout_maps_detail);
    layoutrekomendasi = findViewById(R.id.layout_maps_rekomendasi);

    tvjarak = findViewById(R.id.tv_maps_jarak);
    tvwaktu = findViewById(R.id.tv_maps_waktu);
    tvinfo  = findViewById(R.id.tv_maps_rekomendasi);

    etjarakkm    = findViewById(R.id.et_maps_jarak_km);
    etwaktujam   = findViewById(R.id.et_maps_waktu_jam);
    etwaktumenit = findViewById(R.id.et_maps_waktu_menit);

    tvrekomendasijarak = findViewById(R.id.tv_maps_rekomendasiawal_jarak);
    tvrekomendasiwaktu = findViewById(R.id.tv_maps_rekomendasiawal_waktu);
    tvrekomendasimedian = findViewById(R.id.tv_maps_rekomendasiawal_median);

    tvinfo.setVisibility(View.GONE);
    layoutrekomendasi.setVisibility(View.GONE);
    layoutdetail.setVisibility(View.GONE);

  }

  @Override
  public void onMapReady(GoogleMap googleMap)
  {
    googlemap = googleMap;

    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
      (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    {
      return;
    }

    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    double longitude = location.getLongitude();
    double latitude = location.getLatitude();

    userlocation = new LatLng(latitude, longitude);
    markerList.add(userlocation);
    options.position(userlocation);
    googlemap.addMarker(options);
    googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, zoomLevel));

    googlemap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
    {
      @Override
      public void onMapClick(LatLng latLng)
      {
        if (markerList.size() > 1)
        {
          markerList.clear();
          googlemap.clear();
          markerList.add(userlocation);
          options.position(userlocation);
          googlemap.addMarker(options);
          googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
        markerList.add(latLng);
        options.position(latLng);
        googlemap.addMarker(options);

        if (markerList.size() >= 2)
        {
          mOrigin = markerList.get(0);
          mDestination = markerList.get(1);
          drawRoute();
        }
      }
    });
  }

  private String getDirectionsUrl(LatLng origin, LatLng dest)
  {
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
    String mode = "mode=walking";
    String unit = "units=metric";
    String key = "key=" + getString(R.string.google_maps_key);

    String parameters = unit + "&" + str_origin + "&" + str_dest + "&" + mode + "&" + key;
    String output = "json";

    return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
  }

  @SuppressLint("StaticFieldLeak")
  private class DownloadTask extends AsyncTask<String, Void, String>
  {
    @Override
    protected String doInBackground(String... url)
    {
      String data = "";
      try
      {
        data = downloadUrl(url[0]);
      } catch (Exception e)
      {
        Log.d("Background Task", e.toString());
      }
      return data;
    }

    @Override
    protected void onPostExecute(String result)
    {
      super.onPostExecute(result);
      ParserTask parserTask = new ParserTask();
      parserTask.execute(result);
      getDistance(result);
      getTime(result);
    }
  }

  private void drawRoute()
  {
    layoutdetail.setVisibility(View.VISIBLE);
    String url = getDirectionsUrl(mOrigin, mDestination);
    DownloadTask downloadTask = new DownloadTask();
    downloadTask.execute(url);
  }

  private void getDistance(String response)
  {
    JSONObject jsonObject;
    try
    {
      jsonObject = new JSONObject(response);
      JSONArray array = jsonObject.getJSONArray("routes");
      JSONObject routes = array.getJSONObject(0);
      JSONArray legs = routes.getJSONArray("legs");
      JSONObject steps = legs.getJSONObject(0);
      JSONObject distance = steps.getJSONObject("distance");
      dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));
      tvjarak.setText(" : " + distance.getString("text"));
      Log.e("Distance", "getDistance: " + dist);
    } catch (JSONException e)
    {
      e.printStackTrace();
    }
  }

  private void getTime(String response)
  {
    JSONObject jsonObject;
    try
    {
      jsonObject = new JSONObject(response);
      JSONArray array = jsonObject.getJSONArray("routes");
      JSONObject routes = array.getJSONObject(0);
      JSONArray legs = routes.getJSONArray("legs");
      JSONObject steps = legs.getJSONObject(0);
      JSONObject duration = steps.getJSONObject("duration");
      dist = Double.parseDouble(duration.getString("text").replaceAll("[^\\.0123456789]", ""));
      tvwaktu.setText(" : " + duration.getString("text"));
      Log.e("Time", "getTime: " + dist);
    } catch (JSONException e)
    {
      e.printStackTrace();
    }
  }

  private String downloadUrl(String strUrl) throws IOException
  {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try
    {
      URL url = new URL(strUrl);

      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.connect();
      iStream = urlConnection.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

      StringBuilder sb = new StringBuilder();

      String line;
      while ((line = br.readLine()) != null)
      {
        sb.append(line);
      }
      data = sb.toString();
      br.close();
    } catch (Exception e)
    {
      Log.d("Exception on download", e.toString());
    } finally
    {
      iStream.close();
      urlConnection.disconnect();
    }
    return data;
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

  public void onMapsHitungClick(View view)
  {
    layoutrekomendasi.setVisibility(View.VISIBLE);
    if (validateData())
    {
      Toast.makeText(this, "Maaf semua field wajib diisi", Toast.LENGTH_SHORT).show();
    }
    else
    {
      if (validateMenit())
      {
        Toast.makeText(this, "Maaf maximal angka pada menit hanya 60", Toast.LENGTH_SHORT).show();
      }
      else
      {
        if (Integer.parseInt(etwaktujam.getText().toString()) >= 1)
        {
          tvinfo.setVisibility(View.VISIBLE);
          hitungRekomendasiAwal();
        }
        else
        {
          hitungRekomendasiAwal();
        }
      }
    }
  }

  private boolean validateData()
  {
    return TextUtils.isEmpty(etjarakkm.getText().toString())
      || TextUtils.isEmpty(etwaktujam.getText().toString())
      || TextUtils.isEmpty(etwaktumenit.getText().toString());
  }

  private boolean validateMenit()
  {
    return Integer.parseInt(etwaktumenit.getText().toString()) >= 60;
  }

  private void hitungRekomendasiAwal()
  {
    String totaljarak, totalwaktu, totalmedian;
    int jarak, waktu;

    rekomendasi = 0;

    int km = Integer.parseInt(etjarakkm.getText().toString());
    rekomendasi = rekomendasi + (km * 150);
    jarak = rekomendasi;
    totaljarak = Integer.toString(rekomendasi);
    tvrekomendasijarak.setText(totaljarak);

    rekomendasi = 0;

    if ((Integer.parseInt(etwaktumenit.getText().toString()) >= 0) && (Integer.parseInt(etwaktumenit.getText().toString()) <= 20))
    {
      rekomendasi += 200;
    }
    else if ((Integer.parseInt(etwaktumenit.getText().toString()) > 20) && (Integer.parseInt(etwaktumenit.getText().toString()) <= 40))
    {
      rekomendasi += 400;
    }
    else if ((Integer.parseInt(etwaktumenit.getText().toString()) > 40) && (Integer.parseInt(etwaktumenit.getText().toString()) <= 60))
    {
      rekomendasi += 600;
    }

    int jam = Integer.parseInt(etwaktujam.getText().toString());
    rekomendasi = rekomendasi + ((jam * 3) * 200);
    waktu = rekomendasi;
    totalwaktu = Integer.toString(rekomendasi);
    tvrekomendasiwaktu.setText(totalwaktu);

    totalmedian = Integer.toString((jarak + waktu) / 2);
    tvrekomendasimedian.setText(totalmedian);
  }
}