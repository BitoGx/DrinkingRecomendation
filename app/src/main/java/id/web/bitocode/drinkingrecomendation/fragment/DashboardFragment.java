package id.web.bitocode.drinkingrecomendation.fragment;

import android.hardware.SensorEventListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.web.bitocode.drinkingrecomendation.R;

public class DashboardFragment extends Fragment
{
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
    
  }
}