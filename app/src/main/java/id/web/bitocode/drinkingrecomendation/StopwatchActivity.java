package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity
{
  private int seconds = 0;
  private boolean running, wasRunning;
  private Button btnstart;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stopwatch);

    inisialisasi();
    runTimer();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("seconds", seconds);
    savedInstanceState.putBoolean("running", running);
    savedInstanceState.putBoolean("wasRunning", wasRunning);
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    wasRunning = running;
    running = false;
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    if (wasRunning)
    {
      running = true;
    }
  }

  private void inisialisasi()
  {
    btnstart = findViewById(R.id.btn_stopwatch_start);
  }


  public void onStopwatchClick(View view)
  {
    if(!running)
    {
      running = true;
      btnstart.setText(R.string.pause);
    }
    else
    {
      running = false;
      btnstart.setText(R.string.start);
    }
  }

  public void onClickReset(View view)
  {
    running = false;
    seconds = 0;
  }
  
  private void runTimer()
  {
    final TextView timeView = findViewById(R.id.time_view);
    final Handler handler = new Handler();

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
        
        timeView.setText(time);

        if (running)
        {
          seconds++;
        }
        handler.postDelayed(this, 1000);
      }
    });
  }
}