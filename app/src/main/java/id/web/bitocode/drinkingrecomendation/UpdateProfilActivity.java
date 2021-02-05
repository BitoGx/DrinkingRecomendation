package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import id.web.bitocode.drinkingrecomendation.model.UserModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfilActivity extends AppCompatActivity
{
  private EditText etemail,etnama,etttl,etpersonalrec,ettinggi,etberat;
  private Calendar selectedCalendar, thisCalendar;
  private DatePickerDialog.OnDateSetListener onDateSetListener;
  private RadioGroup rgjeniskelamin;
  private RadioButton rbjeniskelamin,rbpria,rbwanita;
  private Button btnupdate;
  private SessionUtil sessionUtil;
  private ProgressDialog progressDialog;
  private AlertDialog.Builder alertDialogbuilder;
  
  private String email;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_profil);
  
    setUpActionBar();
    inisialisasi();
    showUserData();
    editCalendar(this);
    btnUpdateListener(this);
  }
  
  private void setUpActionBar()
  {
    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.menu_update_profil);
  }
  
  private void inisialisasi()
  {
    sessionUtil        = new SessionUtil(this);
    alertDialogbuilder = new AlertDialog.Builder(UpdateProfilActivity.this);
    
    selectedCalendar = Calendar.getInstance();
    thisCalendar     = Calendar.getInstance();
  
    etemail       = findViewById(R.id.et_upprofile_email);
    etnama        = findViewById(R.id.et_upprofile_nama);
    etttl         = findViewById(R.id.et_upprofile_ttl);
    etpersonalrec = findViewById(R.id.et_upprofile_rekomendasipersonal);
    ettinggi      = findViewById(R.id.et_upprofile_tinggi);
    etberat       = findViewById(R.id.et_upprofile_berat);
    
    rgjeniskelamin = findViewById(R.id.rg_upprofile_jeniskelamin);
    rbpria         = findViewById(R.id.rb_upprofile_pria);
    rbwanita       = findViewById(R.id.rb_upprofile_wanita);
  
    btnupdate = findViewById(R.id.btn_upprofile_update);
  }
  
  private void showUserData()
  {
    etemail.setText(sessionUtil.getLoggedUser(this).getEmail());
    etnama.setText(sessionUtil.getLoggedUser(this).getNama());
    etttl.setText(String.valueOf(sessionUtil.getLoggedUser(this).getTanggallahir()));
    etpersonalrec.setText(String.valueOf(sessionUtil.getLoggedUser(this).getRekomendasipersonal()));
    ettinggi.setText(String.valueOf(sessionUtil.getLoggedUser(this).getTinggibadan()));
    etberat.setText(String.valueOf(sessionUtil.getLoggedUser(this).getBeratbadan()));
    if(sessionUtil.getLoggedUser(this).getJeniskelamin().equalsIgnoreCase("Pria"))
    {
      rbpria.setChecked(true);
    }
    else
    {
      rbwanita.setChecked(true);
    }
  }
  
  private void editCalendar(final Context context)
  {
    etttl.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        new DatePickerDialog(context, onDateSetListener,
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
        Date now = thisCalendar.getTime();
        Date selecteddate = selectedCalendar.getTime();
        
        if(selecteddate.compareTo(now) >= 0)
        {
          etttl.setText(getResources().getString(R.string.validasi_tanggal));
          btnupdate.setClickable(false);
        }
        else
        {
          String newdate = simpleDateFormat.format(selectedCalendar.getTime());
          etttl.setText(newdate);
          btnupdate.setClickable(true);
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
  
  private boolean validateData()
  {
    int selectedradiobutton = rgjeniskelamin.getCheckedRadioButtonId();
    rbjeniskelamin = findViewById(selectedradiobutton);
    
    return TextUtils.isEmpty(etemail.getText().toString())
            || TextUtils.isEmpty(etnama.getText().toString())
            || TextUtils.isEmpty(etttl.getText().toString())
            || TextUtils.isEmpty(etpersonalrec.getText().toString())
            || TextUtils.isEmpty(ettinggi.getText().toString())
            || TextUtils.isEmpty(etberat.getText().toString())
            || TextUtils.isEmpty(rbjeniskelamin.getText().toString());
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
          if(checkEmail(etemail.getText().toString()))
          {
            setUpConfirmationDialog(context);
          }
          else
          {
            progressDialog = ProgressDialog.show(context, "", "Updating.....", true, false);
            updateProfil(context);
          }
        }
      }
    });
  }
  
  private boolean checkEmail(String email)
  {
    return !email.equalsIgnoreCase(sessionUtil.getLoggedUser(this).getEmail());
  }
  
  private void setUpConfirmationDialog(final Context context)
  {
    alertDialogbuilder.setTitle("Konfirmasi");
    alertDialogbuilder.setMessage("Apakah anda yakin ingin mengubah email anda ?");
    
    alertDialogbuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        progressDialog = ProgressDialog.show(context, "", "Updating.....", true, false);
        updateProfil(context);
        dialog.dismiss();
      }
    });
    alertDialogbuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        dialog.dismiss();
      }
    });
  
    alertDialogbuilder.create();
    alertDialogbuilder.show();
  }
  
  private void updateProfil(final Context context)
  {
    String id           = sessionUtil.getLoggedUser(this).getUserid();
    email               = etemail.getText().toString();
    String nama         = etnama.getText().toString();
    String ttl          = etttl.getText().toString();
    String personalrec  = etpersonalrec.getText().toString();
    String tinggi       = ettinggi.getText().toString();
    String berat        = etberat.getText().toString();
    String jeniskelamin = rbjeniskelamin.getText().toString();
    String usertoken    = sessionUtil.getLoggedUser(this).getUserToken();
    
    Call<UserModel.UserDataModel> call = APIService.Factory.create().postUpdateProfile(id, email,
            nama, ttl, personalrec, tinggi, berat, jeniskelamin, usertoken);
    
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
            if(checkEmail(email))
            {
              if(response.body().getMessage().equalsIgnoreCase("Silahkan Cek Email Anda"))
              {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                sessionUtil.logout(context);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
              }
              else
              {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DashboardActivity.class);
                startActivity(intent);
                finish();
              }
            }
            else
            {
              Toast.makeText(context, "Maaf server memberikan response yang salah", Toast.LENGTH_SHORT).show();
            }
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