package id.web.bitocode.drinkingrecomendation.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.web.bitocode.drinkingrecomendation.R;

public class SensorFragment extends Fragment implements SensorEventListener
{
  
  private TextView xText, yText, zText;
  private Sensor mySensor;
  private SensorManager SM;
  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_sensor, container, false);
    // Create our Sensor Manager
    SM = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    
    
    // Accelerometer Sensor
    mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    
    // Register sensor Listener
    SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
    // Assign TextView
    xText = view.findViewById(R.id.xText);
    yText = view.findViewById(R.id.yText);
    zText = view.findViewById(R.id.zText);
    return view;
  }
  
  @Override
  public void onSensorChanged(SensorEvent event)
  {
    xText.setText("X: " + event.values[0]);
    yText.setText("Y: " + event.values[1]);
    zText.setText("Z: " + event.values[2]);
  }
  
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy)
  {
  
  }
}