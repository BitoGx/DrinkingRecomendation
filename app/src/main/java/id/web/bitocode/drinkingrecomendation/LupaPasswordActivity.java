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


import id.web.bitocode.drinkingrecomendation.model.MessageModel;
import id.web.bitocode.drinkingrecomendation.model.UserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LupaPasswordActivity extends AppCompatActivity
{
  private EditText etidentifier;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lupa_password);

    inisialisasi();
  }

  private void inisialisasi()
  {
    etidentifier = findViewById(R.id.et_lupapass_identifier);
  }

  private boolean validateData()
  {
    return TextUtils.isEmpty(etidentifier.getText().toString());
  }

  private void sendEmail(final Context context)
  {
    String identifier = etidentifier.getText().toString();

    Call<MessageModel> call = APIService.Factory.create().postLupaPassword(identifier);

    call.enqueue(new Callback<MessageModel>()
    {
      @Override
      public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response)
      {
        progressDialog.dismiss();
        if (response.isSuccessful())
        {
          if (response.body() != null)
          {
            if (response.body().getMessage().equalsIgnoreCase("Sended"))
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
      public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void onLupaPasswordConfirmClik(View view)
  {
    if (!validateData())
    {
      progressDialog = ProgressDialog.show(this, "", "Checking", true, false);
      sendEmail(this);
    }
    else
    {
      Toast.makeText(this, "Maaf silahkan masukkan username atau email", Toast.LENGTH_SHORT).show();
    }
  }

  public void onLupaPasswordBackClick(View view)
  {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
    finish();
  }
}