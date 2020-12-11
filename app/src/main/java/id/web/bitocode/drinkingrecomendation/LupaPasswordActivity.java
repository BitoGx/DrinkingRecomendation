package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import id.web.bitocode.drinkingrecomendation.model.UserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LupaPasswordActivity extends AppCompatActivity
{
  private EditText etidentifier;
  private Button btnback,btnconfirm;
  private ProgressDialog progressDialog;
  private static final String TAG = "Find Me";
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lupa_password);
    
    inisialisasi();
    
    btnconfirm.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(!validateData())
        {
          progressDialog = ProgressDialog.show(LupaPasswordActivity.this, "", "Checking", true, false);
          sendEmail();
        }
        else
        {
          Toast.makeText(LupaPasswordActivity.this, "Maaf silahkan masukkan username atau email",Toast.LENGTH_SHORT).show();
        }
      }
    });
    
    btnback.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(LupaPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
  
  private void inisialisasi()
  {
    etidentifier = findViewById(R.id.et_lupapass_identifier);
    
    btnback    = findViewById(R.id.btn_lupapass_back);
    btnconfirm = findViewById(R.id.btn_lupapass_confirm);
  }
  
  private boolean validateData()
  {
    return TextUtils.isEmpty(etidentifier.getText().toString());
  }
  
  private void sendEmail()
  {
    String identifier = etidentifier.getText().toString();
  
    Call<UserModel.UserDataModel> call = APIService.Factory.create().postLupaPassword(identifier);
    
    call.enqueue(new Callback<UserModel.UserDataModel>()
    {
      @Override
      public void onResponse(@NonNull Call<UserModel.UserDataModel> call, @NonNull Response<UserModel.UserDataModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          assert response.body() != null;
          if (response.body().getMessage().equalsIgnoreCase("Sended"))
          {
            Log.i(TAG, "Nice find  me "+response.body().getMessage());
            Toast.makeText(LupaPasswordActivity.this, "Silahkan cek email anda", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LupaPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
          }
          else
          {
            Toast.makeText(LupaPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
          }
        }
      }
  
      @Override
      public void onFailure(@NonNull Call<UserModel.UserDataModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Log.i(TAG, "Welp find  me "+t.getMessage());
        Toast.makeText(LupaPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
    
  }
}