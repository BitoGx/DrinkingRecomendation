package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.model.MessageModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity
{
  private EditText etemail,etusername,etfirstpass,etsecondpass,etnama,etttl,ettinggi,etberat;
  private Calendar selectedCalendar, thisCalendar;
  private DatePickerDialog.OnDateSetListener onDateSetListener;
  private RadioGroup rgjeniskelamin;
  private RadioButton rbjeniskelamin;
  private Button btnregister,btnback;
  private ProgressDialog progressDialog;
  private static final String TAG = "Find Me";
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
  
    inisialisasi();
    editCalendar();
    btnRegisterListener();
    btnBackListener();
  }
  
  private void btnRegisterListener()
  {
    btnregister.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if(validateData())
        {
          Toast.makeText(RegisterActivity.this, "Maaf semua field wajib diisi",Toast.LENGTH_SHORT).show();
        }
        else
        {
          if(!validatePassword())
          {
            Toast.makeText(RegisterActivity.this, "Maaf password pertama dan kedua tidak sama",Toast.LENGTH_SHORT).show();
            etfirstpass.requestFocus();
          }
          else
          {
            progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Menyimpan.....", true, false);
            saveUserData();
          }
        }
      }
    });
  }
  
  private void btnBackListener()
  {
    btnback.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
  
  private void editCalendar()
  {
    etttl.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        new DatePickerDialog(RegisterActivity.this, onDateSetListener,
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)).show();
      }
    });
    setDateListener();
  }
  
  private void setDateListener()
  {
    onDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
      private void updateLabel()
      {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        Date thisdate = thisCalendar.getTime();
        Date selecteddate = selectedCalendar.getTime();
      
        if(selecteddate.compareTo(thisdate) >= 0)
        {
          etttl.setText(getResources().getString(R.string.validasi_tanggal));
          btnregister.setClickable(false);
        }
        else
        {
          String newdate = simpleDateFormat.format(selectedCalendar.getTime());
          etttl.setText(newdate);
          btnregister.setClickable(true);
        }
      }
    
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
      {
        selectedCalendar.set(Calendar.YEAR, year);
        selectedCalendar.set(Calendar.MONTH, month);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
      }
    };
  }
  
  private void inisialisasi()
  {
    selectedCalendar = Calendar.getInstance();
    thisCalendar     = Calendar.getInstance();
    
    etemail        = findViewById(R.id.et_reg_email);
    etusername     = findViewById(R.id.et_reg_username);
    etfirstpass    = findViewById(R.id.et_reg_firstpassword);
    etsecondpass   = findViewById(R.id.et_reg_secondpassword);
    etnama         = findViewById(R.id.et_reg_nama);
    etttl          = findViewById(R.id.et_reg_ttl);
    ettinggi       = findViewById(R.id.et_reg_tinggi);
    etberat        = findViewById(R.id.et_reg_berat);
    rgjeniskelamin = findViewById(R.id.rg_reg_jeniskelamin);
  
    btnback = findViewById(R.id.btn_reg_back);
    btnregister = findViewById(R.id.btn_reg_register);
  }
  
  private boolean validateData()
  {
    int selectedradiobutton = rgjeniskelamin.getCheckedRadioButtonId();
    rbjeniskelamin = findViewById(selectedradiobutton);
    
    return TextUtils.isEmpty(etemail.getText().toString())
            || TextUtils.isEmpty(etusername.getText().toString())
            || TextUtils.isEmpty(etfirstpass.getText().toString())
            || TextUtils.isEmpty(etsecondpass.getText().toString())
            || TextUtils.isEmpty(etnama.getText().toString())
            || TextUtils.isEmpty(etttl.getText().toString())
            || TextUtils.isEmpty(ettinggi.getText().toString())
            || TextUtils.isEmpty(etberat.getText().toString())
            || TextUtils.isEmpty(rbjeniskelamin.getText().toString());
  }
  
  private boolean validatePassword()
  {
    return etfirstpass.getText().toString().equals(etsecondpass.getText().toString());
  }
  
  private void saveUserData()
  {
    String email        = etemail.getText().toString();
    String username     = etusername.getText().toString();
    String password     = etfirstpass.getText().toString();
    String nama         = etnama.getText().toString();
    String ttl          = etttl.getText().toString();
    String tinggi       = ettinggi.getText().toString();
    String berat        = etberat.getText().toString();
    String jeniskelamin = rbjeniskelamin.getText().toString();
    
    RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"),email);
    RequestBody requestBodyUsername = RequestBody.create(MediaType.parse("text/plain"),username);
    RequestBody requestBodyPassword = RequestBody.create(MediaType.parse("text/plain"),password);
    RequestBody requestBodyNama = RequestBody.create(MediaType.parse("text/plain"),nama);
    RequestBody requestBodyTtl = RequestBody.create(MediaType.parse("text/plain"),ttl);
    RequestBody requestBodyTinggi = RequestBody.create(MediaType.parse("text/plain"),tinggi);
    RequestBody requestBodyBerat = RequestBody.create(MediaType.parse("text/plain"),berat);
    RequestBody requestBodyJenisKelamin = RequestBody.create(MediaType.parse("text/plain"),jeniskelamin);
    
    Call<MessageModel> call = APIService.Factory.create().postRegister(requestBodyEmail,requestBodyUsername , requestBodyPassword,
            requestBodyNama, requestBodyTtl, requestBodyTinggi, requestBodyBerat,
            requestBodyJenisKelamin);
    
    call.enqueue(new Callback<MessageModel>()
    {
      @Override
      public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response)
      {
        progressDialog.dismiss();
        assert response.body() != null;
        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
        String msg = response.body().getMessage();
        Log.i(TAG, "Nuce find  me "+msg);
        if(msg.equals("Cek Email Anda"))
        {
          Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
          startActivity(intent);
          finish();
        }
      }
  
      @Override
      public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Log.i(TAG, "Welp find  me "+t.getMessage());
        Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}