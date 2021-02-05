package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import id.web.bitocode.drinkingrecomendation.model.UserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GantiPasswordActivity extends AppCompatActivity
{
  private EditText etoldpass,etnewfirstpass,etnewsecondpass;
  private Button btnupdate;
  private ProgressDialog progressDialog;
  private SessionUtil sessionUtil;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ganti_password);
  
    setUpActionBar();
    inisialisasi();
    
    btnUpdateListener(this);
  }
  
  private void setUpActionBar()
  {
    ActionBar actionbar = getSupportActionBar();
    assert actionbar != null;
    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle(R.string.menu_ganti_password);
  }
  
  private void inisialisasi()
  {
    sessionUtil = new SessionUtil(this);
    
    etoldpass       = findViewById(R.id.et_gantipass_oldpass);
    etnewfirstpass  = findViewById(R.id.et_gantipass_firstnewpass);
    etnewsecondpass = findViewById(R.id.et_gantipass_secondnewpass);
    
    btnupdate = findViewById(R.id.btn_gantipass_update);
  }
  
  private boolean validateData()
  {
    return TextUtils.isEmpty(etoldpass.getText().toString())
            || TextUtils.isEmpty(etnewfirstpass.getText().toString())
            || TextUtils.isEmpty(etnewsecondpass.getText().toString());
  }
  
  private boolean validatePassword()
  {
    return etnewfirstpass.getText().toString().equals(etnewsecondpass.getText().toString());
  }
  
  private void btnUpdateListener(final Context context)
  {
    btnupdate.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(validateData())
        {
          Toast.makeText(context, "Maaf semua field wajib diisi",Toast.LENGTH_SHORT).show();
        }
        else
        {
          if(!validatePassword())
          {
            Toast.makeText(context, "Maaf password pertama dan kedua tidak sama",Toast.LENGTH_SHORT).show();
            etnewfirstpass.requestFocus();
          }
          else
          {
            progressDialog = ProgressDialog.show(context, "", "Updating...", true, false);
            updatePassword(context);
          }
        }
      }
    });
  }
  
  private void updatePassword(final Context context)
  {
    String id      = sessionUtil.getLoggedUser(context).getUserid();
    String oldpass = etoldpass.getText().toString();
    String newpass = etnewfirstpass.getText().toString();
  
    Call<UserModel.UserDataModel> call = APIService.Factory.create().postUpdatePassword(id,oldpass,newpass);
    
    call.enqueue(new Callback<UserModel.UserDataModel>()
    {
      @Override
      public void onResponse(@NonNull Call<UserModel.UserDataModel> call, @NonNull Response<UserModel.UserDataModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          if (response.body() != null)
          {
            if (response.body().getMessage().equalsIgnoreCase("Berhasil"))
            {
              Toast.makeText(context, "Password berhasil diganti", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(context, DashboardActivity.class);
              startActivity(intent);
              finish();
            }
            else
            {
              Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
          else
          {
            Toast.makeText(context, "Maaf server memberikan response yang salah", Toast.LENGTH_SHORT).show();
          }
        }
      }
  
      @Override
      public void onFailure(@NonNull Call<UserModel.UserDataModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}