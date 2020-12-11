package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

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
  private static final String TAG = "Find Me";
  private NavigationView navigationView;
  private Toolbar toolbar;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
  
    inisialisasi();
    hideItem();
    
    setSupportActionBar(toolbar);
    
    navigationView.setNavigationItemSelectedListener(this);
  
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  
    progressDialog = ProgressDialog.show(DashboardActivity.this, "", "Load Data.....", true, false);
    loadUserData();
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
  
  private void inisialisasi()
  {
    drawer = findViewById(R.id.drawer_dashboard_layout);
    navigationView = findViewById(R.id.nav_dashboard_view);
    toolbar = findViewById(R.id.toolbar);
  }
  
  private void hideItem()
  {
    Menu nav_Menu = navigationView.getMenu();
    nav_Menu.findItem(R.id.nav_dashboard).setVisible(false);
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    int id = item.getItemId();
    
    if(id == R.id.nav_activityrecognition)
    {
      Intent intent = new Intent(this, ActivityRecognitionActivity.class);
      startActivity(intent);
    }
  
    if(id == R.id.nav_updateprofile)
    {
      Intent intent = new Intent(this, UpdateProfilActivity.class);
      startActivity(intent);
    }
  
    if(id == R.id.nav_gantipassword)
    {
      Intent intent = new Intent(this, GantiPasswordActivity.class);
      startActivity(intent);
    }
  
    if(id == R.id.nav_logout)
    {
      SessionUtil.logout(DashboardActivity.this);
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
    }
    return true;
  }
  
  private void loadUserData()
  {
    String id = SessionUtil.getLoggedUser(DashboardActivity.this).getUserid();
    
    Call<SelectUserModel> call = APIService.Factory.create().postSelectUser(id);
    call.enqueue(new Callback<SelectUserModel>()
    {
      @Override
      public void onResponse(@NonNull Call<SelectUserModel> call, @NonNull Response<SelectUserModel> response)
      {
        progressDialog.dismiss();
        TextView tvopen = findViewById(R.id.tv_dashboard_open);
        assert response.body() != null;
        String msg = response.body().getNama();
        Log.i(TAG, "Nuce find  me "+msg);
        tvopen.setText(response.body().getNama());
      }
      
      @Override
      public void onFailure(@NonNull Call<SelectUserModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}