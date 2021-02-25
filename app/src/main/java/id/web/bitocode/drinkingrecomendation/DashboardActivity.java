package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import id.web.bitocode.drinkingrecomendation.model.SelectUserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
  private DrawerLayout drawer;
  private ProgressDialog progressDialog;
  private NavigationView navigationView;
  private Toolbar toolbar;
  private TextView tv_nama, tv_ttl, tv_berat, tv_tinggi;
  
  private SessionUtil sessionUtil;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    
    setUpActionBar();
    inisialisasi();
    hideItem();
    
    actionBarListener();
    
    loadUserData(this);
  }
  
  @Override
  public void onBackPressed()
  {
    if (drawer.isDrawerOpen(GravityCompat.START))
    {
      drawer.closeDrawer(GravityCompat.START);
    } else
    {
      super.onBackPressed();
    }
  }
  
  private void setUpActionBar()
  {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }
  
  private void inisialisasi()
  {
    sessionUtil = new SessionUtil(this);
    
    drawer = findViewById(R.id.drawer_dashboard_layout);
    
    navigationView = findViewById(R.id.nav_dashboard_view);
    navigationView.setNavigationItemSelectedListener(this);
    
    tv_nama = findViewById(R.id.tv_dashboard_nama);
    tv_ttl = findViewById(R.id.tv_dashboard_ttl);
    tv_berat = findViewById(R.id.tv_dashboard_berat);
    tv_tinggi = findViewById(R.id.tv_dashboard_tinggi);
  }
  
  private void hideItem()
  {
    Menu nav_Menu = navigationView.getMenu();
    nav_Menu.findItem(R.id.nav_dashboard).setVisible(false);
  }
  
  private void actionBarListener()
  {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    int id = item.getItemId();
    
    if (id == R.id.nav_activityrecognition)
    {
      Intent intent = new Intent(this, ActivityRecognitionActivity.class);
      startActivity(intent);
    }
    
    if (id == R.id.nav_rekomendasi)
    {
      Intent intent = new Intent(this, MapsActivity.class);
      startActivity(intent);
    }
    
    if (id == R.id.nav_riwayat)
    {
      Intent intent = new Intent(this, RiwayatActivity.class);
      startActivity(intent);
    }
    
    if (id == R.id.nav_updateprofile)
    {
      Intent intent = new Intent(this, UpdateProfilActivity.class);
      startActivity(intent);
    }
    
    if (id == R.id.nav_gantipassword)
    {
      Intent intent = new Intent(this, GantiPasswordActivity.class);
      startActivity(intent);
    }
    
    if (id == R.id.nav_logout)
    {
      sessionUtil.logout(DashboardActivity.this);
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
      finish();
    }
    return true;
  }
  
  private void loadUserData(final Context context)
  {
    progressDialog = ProgressDialog.show(context, "", "Load Data.....", true, false);
    
    String id = sessionUtil.getLoggedUser(context).getUserid();
    
    Call<SelectUserModel> call = APIService.Factory.create().postSelectUser(id);
    call.enqueue(new Callback<SelectUserModel>()
    {
      @Override
      public void onResponse(@NonNull Call<SelectUserModel> call, @NonNull Response<SelectUserModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          if (response.body() != null)
          {
            tv_nama.setText(response.body().getNama());
            tv_ttl.setText(response.body().getTanggallahir());
            tv_berat.setText(response.body().getBeratbadan());
            tv_tinggi.setText(response.body().getTinggibadan());
          }
          else
          {
            Toast.makeText(context, "Maaf server memberikan response yang salah", Toast.LENGTH_SHORT).show();
          }
        }
      }
      
      @Override
      public void onFailure(@NonNull Call<SelectUserModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}