package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import id.web.bitocode.drinkingrecomendation.model.UserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{
  private EditText etusername,etpassword;
  private TextView tvlupapassword;
  private Button btnregister,btnLogin;
  private ProgressDialog progressDialog;
  private static final String TAG = "Find Me";
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    inisialisasi();
    
    if (SessionUtil.isLoggedIn(this)){
      Intent intent = new Intent(this, DashboardActivity.class);
      startActivity(intent);
      finish();
    }
    
    btnLogin.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(!validateData())
        {
          progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true, false);
          userLogin();
        }
        else
        {
          Toast.makeText(LoginActivity.this, "Maaf semua field wajib diisi",Toast.LENGTH_SHORT).show();
        }
      }
    });
    
    btnregister.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
      }
    });
  
    String udata="Lupa password klik disini";
    SpannableString content = new SpannableString(udata);
    content.setSpan(new UnderlineSpan(), 19, udata.length(), 0);
    tvlupapassword.setText(content);
    
    tvlupapassword.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(LoginActivity.this, LupaPasswordActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
  
  private void inisialisasi()
  {
    etusername  = findViewById(R.id.et_log_username);
    etpassword  = findViewById(R.id.et_log_password);
    
    tvlupapassword = findViewById(R.id.tv_log_lupapassword);
  
    btnLogin    = findViewById(R.id.btn_log_login);
    btnregister = findViewById(R.id.btn_log_registrasi);
  }
  
  private boolean validateData()
  {
    return TextUtils.isEmpty(etusername.getText().toString())
            || TextUtils.isEmpty(etpassword.getText().toString());
  }
  
  private void userLogin()
  {
    String username = etusername.getText().toString();
    String password = etpassword.getText().toString();
  
    Call<UserModel.UserDataModel> call = APIService.Factory.create().postLogin(username, password);
    
    call.enqueue(new Callback<UserModel.UserDataModel>()
    {
      @Override
      public void onResponse(@NonNull Call<UserModel.UserDataModel> call, @NonNull Response<UserModel.UserDataModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          assert response.body() != null;
          if (response.body().getMessage().equalsIgnoreCase("Welcome"))
          {
            if (SessionUtil.login(LoginActivity.this, response.body().getResults().get(0)))
            {
              Log.i(TAG, "Nice find  me "+response.body().getMessage());
              Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
              startActivity(intent);
              finish();
            }
          }
          else
          {
            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
          }
        }
      }
  
      @Override
      public void onFailure(@NonNull Call<UserModel.UserDataModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Log.i(TAG, "Welp find  me "+t.getMessage());
        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
    
    
  }
}