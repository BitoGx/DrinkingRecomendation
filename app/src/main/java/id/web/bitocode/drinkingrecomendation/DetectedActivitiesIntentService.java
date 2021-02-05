package id.web.bitocode.drinkingrecomendation;

import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import id.web.bitocode.drinkingrecomendation.config.Constants;

public class DetectedActivitiesIntentService  extends IntentService {
  
  protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();
  
  public DetectedActivitiesIntentService()
  {
    // Use the TAG to name the worker thread.
    super(TAG);
  }
  
  @Override
  public void onCreate()
  {
    super.onCreate();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected void onHandleIntent(Intent intent)
  {
    ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
    ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
    
    for (DetectedActivity activity : detectedActivities) {
      Log.i(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
      broadcastActivity(activity);
    }
  }
  
  private void broadcastActivity(DetectedActivity activity) {
    Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
    intent.putExtra("type", activity.getType());
    intent.putExtra("confidence", activity.getConfidence());
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }
}
