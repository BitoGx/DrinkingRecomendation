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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import id.web.bitocode.drinkingrecomendation.fragment.DashboardFragment;
import id.web.bitocode.drinkingrecomendation.model.SelectUserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
  private DrawerLayout drawer;
  private ProgressDialog progressDialog;
  
  private SessionUtil sessionUtil;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboards);
    
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    drawer = findViewById(R.id.drawer_dashboards_layout);
    NavigationView navigationView = findViewById(R.id.nav_dash_view);
    navigationView.setNavigationItemSelectedListener(this);
    
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    
    if (savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
              new DashboardFragment()).commit();
      navigationView.setCheckedItem(R.id.nav_dashboard);
    }
  
    sessionUtil = new SessionUtil(this);
    
    progressDialog = ProgressDialog.show(DashboardsActivity.this, "", "Load Data.....", true, false);
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
  
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item)
  {
    int id = item.getItemId();
    
    if(id == R.id.nav_activityrecognition)
    {
      Intent intent = new Intent(this, ActivityRecognitionActivity.class);
      startActivity(intent);
    }
    
    if(id == R.id.nav_dashboard)
    {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
              new DashboardFragment()).commit();
    }
  
    if(id == R.id.nav_logout)
    {
      sessionUtil.logout(DashboardsActivity.this);
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
      finish();
    }
    return true;
  }
  
  private void loadUserData()
  {
  }
}