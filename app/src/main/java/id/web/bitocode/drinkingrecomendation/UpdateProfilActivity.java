package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdateProfilActivity extends AppCompatActivity
{
  private EditText etpasslama,etnewfirstpass,etnewsecondpass;
  private Button btnupdate;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_profil);
  
    setUpActionBar();
    //inisialisasi();
    //btnUpdateListener();
  }
  
  private void setUpActionBar()
  {
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle(R.string.menu_update_profil);
  }
  
  private void inisialisasi()
  {

  }
  
  private void btnUpdateListener()
  {
    btnupdate.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
    
      }
    });
  }
  
  private void updateProfil()
  {
  
  }
}