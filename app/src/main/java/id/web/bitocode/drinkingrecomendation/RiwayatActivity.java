package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.web.bitocode.drinkingrecomendation.adapter.RiwayatRecyclerViewAdapter;
import id.web.bitocode.drinkingrecomendation.model.RiwayatModel;
import id.web.bitocode.drinkingrecomendation.model.SelectUserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatActivity extends AppCompatActivity
{
  private SessionUtil sessionUtil;
  private RecyclerView rv_riwayat;
  private List<RiwayatModel> riwayatModels;
  private ProgressDialog progressDialog;
  
  private RiwayatRecyclerViewAdapter riwayatRecyclerViewAdapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_riwayat);
    
    setUpActionBar();
    inisialisasi();
    //initRecyclerView();
    //loadRiwayatUser();
    
  }
  
  private void setUpActionBar()
  {
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle("Riwayat");
  }
  
  private void inisialisasi()
  {
    rv_riwayat = findViewById(R.id.rv_riwayat);
  }
  /*
  private void initRecyclerView()
  {
    riwayatRecyclerViewAdapter = new RiwayatRecyclerViewAdapter(riwayatModels);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RiwayatActivity.this);
    rv_riwayat.setLayoutManager(layoutManager);
    rv_riwayat.setItemAnimator(new DefaultItemAnimator());
    rv_riwayat.setAdapter(riwayatRecyclerViewAdapter);
  }

  private void loadRiwayatUser()
  {
    progressDialog = ProgressDialog.show(RiwayatActivity.this, "", "Load Data.....", true, false);
    
    String id = sessionUtil.getLoggedUser(RiwayatActivity.this).getUserid();
    
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
        tvopen.setText(response.body().getNama());
      }
      
      @Override
      public void onFailure(@NonNull Call<SelectUserModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(RiwayatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }*/
}