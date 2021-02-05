package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
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
  private SessionUtil sessionUtil;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    inisialisasi(this);
    checkSession(this);
    btnRegisterListener(this);
    tvLupaPasswordListener(this);
    btnLoginListener(this);
    
  }
  
  private void inisialisasi(Context context)
  {
    String udata="Lupa password klik disini";
    SpannableString content = new SpannableString(udata);
    content.setSpan(new UnderlineSpan(), 19, udata.length(), 0);
    
    sessionUtil = new SessionUtil(context);
    
    etusername  = findViewById(R.id.et_log_username);
    etpassword  = findViewById(R.id.et_log_password);
    
    tvlupapassword = findViewById(R.id.tv_log_lupapassword);
    tvlupapassword.setText(content);
  
    btnLogin    = findViewById(R.id.btn_log_login);
    btnregister = findViewById(R.id.btn_log_registrasi);
  }
  
  private void checkSession(Context context)
  {
    if (sessionUtil.isLoggedIn(context))
    {
      Intent intent = new Intent(context, DashboardActivity.class);
      startActivity(intent);
      finish();
    }
  }
  
  private boolean validateData()
  {
    return TextUtils.isEmpty(etusername.getText().toString())
            || TextUtils.isEmpty(etpassword.getText().toString());
  }
  
  private void btnLoginListener(final Context context)
  {
    btnLogin.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(!validateData())
        {
          progressDialog = ProgressDialog.show(context, "", "Logging in...", true, false);
          userLogin(context);
        }
        else
        {
          Toast.makeText(context, "Maaf semua field wajib diisi",Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
  
  private void userLogin(final Context context)
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
          if (response.body() != null)
          {
            if (response.body().getMessage().equalsIgnoreCase("Welcome"))
            {
              if (sessionUtil.login(context, response.body().getResults().get(0)))
              {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DashboardActivity.class);
                startActivity(intent);
                finish();
              }
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
  
  private void btnRegisterListener(final Context context)
  {
    btnregister.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(context, RegisterActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
  
  private void tvLupaPasswordListener(final Context context)
  {
    tvlupapassword.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(context, LupaPasswordActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
}