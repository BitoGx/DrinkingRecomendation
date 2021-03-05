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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.model.SelectRiwayatModel;
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
  private TextView tv_nama, tv_ttl, tv_berat, tv_tinggi,tv_jenisaktivitas,tv_tanggalaktivitas,tv_jarak,tv_waktu,tv_rekomendasiawal,tv_rekomendasiakhir,tv_dashboard;
  private String userid;
  private LinearLayout layoutriwayat;
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

    loadAllData(this);
  }

  @Override
  public void onBackPressed()
  {
    if (drawer.isDrawerOpen(GravityCompat.START))
    {
      drawer.closeDrawer(GravityCompat.START);
    }
    else
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
    userid = sessionUtil.getLoggedUser(this).getUserid();

    drawer = findViewById(R.id.drawer_dashboard_layout);

    navigationView = findViewById(R.id.nav_dashboard_view);
    navigationView.setNavigationItemSelectedListener(this);

    layoutriwayat = findViewById(R.id.layout_dashboard_riwayatinfo);

    tv_nama             = findViewById(R.id.tv_dashboard_nama);
    tv_ttl              = findViewById(R.id.tv_dashboard_ttl);
    tv_berat            = findViewById(R.id.tv_dashboard_berat);
    tv_tinggi           = findViewById(R.id.tv_dashboard_tinggi);
    tv_jenisaktivitas   = findViewById(R.id.tv_dashboard_jenisaktifitas);
    tv_tanggalaktivitas = findViewById(R.id.tv_dashboard_tanggalaktivitas);
    tv_jarak            = findViewById(R.id.tv_dashboard_jarak);
    tv_waktu            = findViewById(R.id.tv_dashboard_waktu);
    tv_rekomendasiawal  = findViewById(R.id.tv_dashboard_rec_awal);
    tv_rekomendasiakhir = findViewById(R.id.tv_dashboard_rec_akhir);
    tv_dashboard        = findViewById(R.id.tv_dashboard_title);
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

    Call<SelectUserModel> call = APIService.Factory.create().postSelectUser(userid);
    call.enqueue(new Callback<SelectUserModel>()
    {
      @Override
      public void onResponse(@NonNull Call<SelectUserModel> call, @NonNull Response<SelectUserModel> response)
      {
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

  private void loadRiwayatData(final Context context)
  {
    String type = "Dashboard";
    Call<SelectRiwayatModel> call = APIService.Factory.create().postSelectRiwayat(userid, type);
    call.enqueue(new Callback<SelectRiwayatModel>()
    {
      @Override
      public void onResponse(@NonNull Call<SelectRiwayatModel> call,@NonNull Response<SelectRiwayatModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          if (response.body() != null)
          {
            if (!response.body().getMessage().equalsIgnoreCase("0"))
            {
              tv_dashboard.setVisibility(View.GONE);
              tv_tanggalaktivitas.setText(response.body().getTanggalaktivitas());
              tv_jenisaktivitas.setText(response.body().getJenisaktivitas());

              int totalKm = Integer.parseInt(response.body().getJarak()) / 1000;
              int totalM = (Integer.parseInt(response.body().getJarak()) % 1000);
              String jarak = String.format(Locale.getDefault(),
                                           " %d Km %02d M", totalKm,
                                           totalM);
              tv_jarak.setText(jarak);

              int totalHours = Integer.parseInt(response.body().getWaktu()) / 3600;
              int totalMinutes = (Integer.parseInt(response.body().getWaktu()) % 3600) / 60;
              int totalSecs = Integer.parseInt(response.body().getWaktu()) % 60;
              String waktu = String.format(Locale.getDefault(),
                                           " %d Jam %02d Menit %02d Detik", totalHours,
                                           totalMinutes, totalSecs);
              tv_waktu.setText(waktu);

              tv_rekomendasiawal.setText(response.body().getRekomendasiawal());
              tv_rekomendasiakhir.setText(response.body().getRekomendasiakhir());
            }
            else
            {
              layoutriwayat.setVisibility(View.GONE);
            }
          }
          else
          {
            Toast.makeText(context, "Maaf server memberikan response yang salah", Toast.LENGTH_SHORT).show();
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<SelectRiwayatModel> call,@NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void loadAllData(Context context)
  {
    loadUserData(context);
    loadRiwayatData(context);
  }
}