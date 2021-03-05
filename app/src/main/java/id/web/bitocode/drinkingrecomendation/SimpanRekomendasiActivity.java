package id.web.bitocode.drinkingrecomendation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.web.bitocode.drinkingrecomendation.model.MessageModel;
import id.web.bitocode.drinkingrecomendation.network.APIService;
import id.web.bitocode.drinkingrecomendation.util.SessionUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpanRekomendasiActivity extends AppCompatActivity
{
  private String rekomendasiawal, rekomendasiakhir, jaraktempuh, tanggal, waktu;
  private EditText etjenisaktivitas, ettanggal, etjarak, etwaktu, etrekomendasiawal, etrekomendasiakhir;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simpan_rekomendasi);

    inisialisasi();
    setUpRiwayat();
  }

  private void inisialisasi()
  {
    Intent intent = getIntent();
    jaraktempuh = (String) intent.getStringExtra("jarak");
    waktu = (String) intent.getStringExtra("waktu");
    rekomendasiawal = (String) intent.getStringExtra("rekomendasiawal");
    rekomendasiakhir = (String) intent.getStringExtra("rekomendasiakhir");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    tanggal = dateFormat.format(new Date());

    etjenisaktivitas = findViewById(R.id.et_simpan_jenisaktivitas);
    ettanggal = findViewById(R.id.et_simpan_tanggal);
    etjarak = findViewById(R.id.et_simpan_jarak);
    etwaktu = findViewById(R.id.et_simpan_waktu);
    etrekomendasiawal = findViewById(R.id.et_simpan_rekomendasiawal);
    etrekomendasiakhir = findViewById(R.id.et_simpan_rekomendasiakhir);
  }

  private void setUpRiwayat()
  {
    ettanggal.setText(tanggal);
    etjarak.setText(jaraktempuh);
    etwaktu.setText(waktu);
    etrekomendasiawal.setText(rekomendasiawal);
    etrekomendasiakhir.setText(rekomendasiakhir);
  }

  private void saveRiwayatUser(final Context context)
  {
    SessionUtil sessionUtil = new SessionUtil(this);
    String jenisaktivitas = etjenisaktivitas.getText().toString();
    String iduser = sessionUtil.getLoggedUser(this).getUserid();

    RequestBody requestBodyJenisAktivitas = RequestBody.create(MediaType.parse("text/plain"), jenisaktivitas);
    RequestBody requestBodyTanggalAktivitas = RequestBody.create(MediaType.parse("text/plain"), tanggal);
    RequestBody requestBodyJarakTempuh = RequestBody.create(MediaType.parse("text/plain"), jaraktempuh);
    RequestBody requestBodyWaktu = RequestBody.create(MediaType.parse("text/plain"), waktu);
    RequestBody requestBodyRekomendasiAwal = RequestBody.create(MediaType.parse("text/plain"), rekomendasiawal);
    RequestBody requestBodyRekomendasiAkhir = RequestBody.create(MediaType.parse("text/plain"), rekomendasiakhir);
    RequestBody requestBodyIdUser = RequestBody.create(MediaType.parse("text/plain"), iduser);

    Call<MessageModel> call = APIService.Factory.create().postRiwayat(requestBodyIdUser,
                                                                      requestBodyJenisAktivitas,
                                                                      requestBodyTanggalAktivitas,
                                                                      requestBodyJarakTempuh,
                                                                      requestBodyWaktu,
                                                                      requestBodyRekomendasiAwal,
                                                                      requestBodyRekomendasiAkhir);

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
            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            String msg = response.body().getMessage();
            if (msg.equalsIgnoreCase("Aktivitas Berhasil Disimpan"))
            {
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
      public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t)
      {
        progressDialog.dismiss();
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private boolean validateData()
  {
    return TextUtils.isEmpty(etjenisaktivitas.getText().toString())
      || TextUtils.isEmpty(ettanggal.getText().toString())
      || TextUtils.isEmpty(etjarak.getText().toString())
      || TextUtils.isEmpty(etwaktu.getText().toString())
      || TextUtils.isEmpty(etrekomendasiawal.getText().toString())
      || TextUtils.isEmpty(etrekomendasiakhir.getText().toString());
  }

  public void onSimpanRiwayatClick(View view)
  {
    if (validateData())
    {
      Toast.makeText(this, "Maaf semua field wajib diisi", Toast.LENGTH_SHORT).show();
    }
    else
    {
      progressDialog = ProgressDialog.show(this, "", "Menyimpan.....", true, false);
      saveRiwayatUser(this);
    }
  }
}