package id.web.bitocode.drinkingrecomendation.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import id.web.bitocode.drinkingrecomendation.config.Constants;

public class BackgroundDetectedActivitiesService extends Service
{
  
  IBinder mBinder = new BackgroundDetectedActivitiesService.LocalBinder();
  private PendingIntent mPendingIntent;
  private ActivityRecognitionClient mActivityRecognitionClient;
  
  public BackgroundDetectedActivitiesService()
  {
  
  }
  
  @Override
  public void onCreate()
  {
    super.onCreate();
    mActivityRecognitionClient = new ActivityRecognitionClient(this);
    Intent mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
    mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
    requestActivityUpdatesButtonHandler();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent)
  {
    return mBinder;
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }
  
  public void requestActivityUpdatesButtonHandler()
  {
    Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
      Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
      mPendingIntent);
    
    task.addOnSuccessListener(new OnSuccessListener<Void>()
    {
      @Override
      public void onSuccess(Void result)
      {
        Toast.makeText(getApplicationContext(),
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT)
                .show();
      }
    });
    
    task.addOnFailureListener(new OnFailureListener()
    {
      @Override
      public void onFailure(@NonNull Exception e)
      {
        Toast.makeText(getApplicationContext(),
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT)
                .show();
      }
    });
  }
  
  public void removeActivityUpdatesButtonHandler()
  {
    Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
            mPendingIntent);
    task.addOnSuccessListener(new OnSuccessListener<Void>()
    {
      @Override
      public void onSuccess(Void result)
      {
        Toast.makeText(getApplicationContext(),
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT)
                .show();
      }
    });
    
    task.addOnFailureListener(new OnFailureListener()
    {
      @Override
      public void onFailure(@NonNull Exception e)
      {
        Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                Toast.LENGTH_SHORT).show();
      }
    });
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    removeActivityUpdatesButtonHandler();
  }
  
  public class LocalBinder extends Binder
  {
    public BackgroundDetectedActivitiesService getServerInstance()
    {
      return BackgroundDetectedActivitiesService.this;
    }
  }
}