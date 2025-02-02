package id.web.bitocode.drinkingrecomendation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.web.bitocode.drinkingrecomendation.adapter.RiwayatRecyclerViewAdapter;
import id.web.bitocode.drinkingrecomendation.model.RiwayatModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatActivity extends AppCompatActivity
{
  private String userid;
  private RecyclerView rv_riwayat;
  private TextView tv_title;
  private ProgressDialog progressDialog;

  private RiwayatRecyclerViewAdapter riwayatRecyclerViewAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_riwayat);

    setUpActionBar();
    inisialisasi();
    initRecyclerView();
    loadRiwayatUser();
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
    SessionUtil sessionUtil = new SessionUtil(this);
    userid = sessionUtil.getLoggedUser(this).getUserid();

    rv_riwayat = findViewById(R.id.rv_riwayat);
    tv_title   = findViewById(R.id.tv_riwayat_title);
  }

  private void initRecyclerView()
  {
    riwayatRecyclerViewAdapter = new RiwayatRecyclerViewAdapter(new ArrayList<RiwayatModel>());
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RiwayatActivity.this);
    rv_riwayat.setLayoutManager(layoutManager);
    rv_riwayat.setItemAnimator(new DefaultItemAnimator());
    rv_riwayat.setAdapter(riwayatRecyclerViewAdapter);
  }

  private void loadRiwayatUser()
  {
    progressDialog = ProgressDialog.show(RiwayatActivity.this, "", "Load Data.....", true, false);
    String type = "Riwayat";

    Call<RiwayatModel.RiwayatDataModel> call = APIService.Factory.create().postGetRiwayat(userid, type);
    call.enqueue(new Callback<RiwayatModel.RiwayatDataModel>()
    {
      @Override
      public void onResponse(@NonNull Call<RiwayatModel.RiwayatDataModel> call, @NonNull Response<RiwayatModel.RiwayatDataModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          if (response.body() != null)
          {
            if (!response.body().getMessage().equalsIgnoreCase("0"))
            {
              tv_title.setVisibility(View.GONE);
              riwayatRecyclerViewAdapter.publishData(response.body().getResults());
            }
          }
          else
          {
            Toast.makeText(RiwayatActivity.this, "Maaf server memberikan response yang salah", Toast.LENGTH_SHORT).show();
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<RiwayatModel.RiwayatDataModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(RiwayatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}