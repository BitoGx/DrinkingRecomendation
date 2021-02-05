package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LupaPasswordActivity extends AppCompatActivity
{
  private EditText etidentifier;
  private Button btnback,btnconfirm;
  private ProgressDialog progressDialog;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lupa_password);
    
    inisialisasi();
    btnBackListener(this);
    btnConfirmListener(this);
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
  
  private void btnConfirmListener(final Context context)
  {
    btnconfirm.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(!validateData())
        {
          progressDialog = ProgressDialog.show(context, "", "Checking", true, false);
          sendEmail(context);
        }
        else
        {
          Toast.makeText(context, "Maaf silahkan masukkan username atau email",Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
  
  private void sendEmail(final Context context)
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
          if (response.body() != null)
          {
            if(response.body().getMessage().equalsIgnoreCase("Sended"))
            {
              Toast.makeText(context, "Silahkan cek email anda", Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(context, LoginActivity.class);
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
  
  private void btnBackListener(final Context context)
  {
    btnback.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
}