package id.web.bitocode.drinkingrecomendation.Rationale;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;

import java.util.Arrays;

import id.web.bitocode.drinkingrecomendation.DashboardActivity;
import id.web.bitocode.drinkingrecomendation.R;
import id.web.bitocode.drinkingrecomendation.SimpanRekomendasiActivity;

public class PermissionRationalActivity extends AppCompatActivity implements
  ActivityCompat.OnRequestPermissionsResultCallback
{

  private static final String TAG = "Permission";

  private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 45;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
      == PackageManager.PERMISSION_GRANTED)
    {
      finish();
    }

    setContentView(R.layout.activity_permission_rational);
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  public void onClickApprovePermissionRequest(View view)
  {
    Log.d(TAG, "onClickApprovePermissionRequest()");

    ActivityCompat.requestPermissions(
      this,
      new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
      PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
  }

  public void onClickDenyPermissionRequest(View view)
  {
    Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onRequestPermissionsResult(
    int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
  {
    String permissionResult = "Request code: " + requestCode + ", Permissions: " +
      Arrays.toString(permissions) + ", Results: " + Arrays.toString(grantResults);

    Log.d(TAG, "onRequestPermissionsResult(): " + permissionResult);

    if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION)
    {
      finish();
    }
  }
}