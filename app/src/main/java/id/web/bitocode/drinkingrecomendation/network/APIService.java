package id.web.bitocode.drinkingrecomendation.network;

import java.util.concurrent.TimeUnit;

import id.web.bitocode.drinkingrecomendation.config.Constants;
import id.web.bitocode.drinkingrecomendation.model.MessageModel;
import id.web.bitocode.drinkingrecomendation.model.RiwayatModel;
import id.web.bitocode.drinkingrecomendation.model.SelectRiwayatModel;
import id.web.bitocode.drinkingrecomendation.model.SelectUserModel;
import id.web.bitocode.drinkingrecomendation.model.UserModel;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService
{
  @Multipart
  @POST("API/register.php")
  Call<MessageModel>postRegister(@Part("email") RequestBody email,
                                 @Part("username") RequestBody username,
                                 @Part("password") RequestBody password,
                                 @Part("nama") RequestBody nama,
                                 @Part("ttl") RequestBody ttl,
                                 @Part("tinggi") RequestBody tinggi,
                                 @Part("berat") RequestBody berat,
                                 @Part("jeniskelamin") RequestBody jeniskelamin);

  @Multipart
  @POST("API/simpanRiwayat.php")
  Call<MessageModel> postRiwayat(@Part("iduser") RequestBody iduser,
                                 @Part("jenisaktivitas") RequestBody jenisaktivitas,
                                 @Part("tanggalaktivitas") RequestBody tanggalaktivitas,
                                 @Part("jarak") RequestBody jarak,
                                 @Part("waktu") RequestBody waktu,
                                 @Part("rekomendasiawal") RequestBody rekomendasiawal,
                                 @Part("rekomendasiakhir") RequestBody rekomendasiakhir);


  @FormUrlEncoded
  @POST("API/login.php")
  Call<UserModel.UserDataModel> postLogin(@Field("username") String username,
                                          @Field("password") String password);
  
  @FormUrlEncoded
  @POST("API/lupaPassword.php")
  Call<MessageModel> postLupaPassword(@Field("email") String email);
  
  @FormUrlEncoded
  @POST("API/updatePassword.php")
  Call<UserModel.UserDataModel> postUpdatePassword(@Field("id") String id,
                                                   @Field("oldpass") String oldpass,
                                                   @Field("newpass") String newpass);
  
  @FormUrlEncoded
  @POST("API/updateProfile.php")
  Call<UserModel.UserDataModel> postUpdateProfile(@Field("id") String id,
                                                  @Field("email") String email,
                                                  @Field("nama") String nama,
                                                  @Field("ttl") String ttl,
                                                  @Field("personalrec") String personalrec,
                                                  @Field("tinggi") String tinggi,
                                                  @Field("berat") String berat,
                                                  @Field("jeniskelamin") String jeniskelamin,
                                                  @Field("usertoken") String usertoken);
  
  @FormUrlEncoded
  @POST("API/selectUser.php")
  Call<SelectUserModel> postSelectUser(@Field("id") String id);
  
  @FormUrlEncoded
  @POST("API/selectRiwayat.php")
  Call<SelectRiwayatModel> postSelectRiwayat(@Field("id") String id,
                                             @Field("type") String type);

  @FormUrlEncoded
  @POST("API/selectRiwayat.php")
  Call<RiwayatModel.RiwayatDataModel> postGetRiwayat(@Field("id") String id,
                                                     @Field("type") String type);


  class Factory
  {
    public static APIService create()
    {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.readTimeout(20, TimeUnit.SECONDS);
      builder.connectTimeout(20, TimeUnit.SECONDS);
      builder.writeTimeout(20, TimeUnit.SECONDS);
      
      OkHttpClient client = builder.build();
      
      Retrofit retrofit = new Retrofit.Builder()
              .baseUrl(Constants.URL)
              .client(client)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
      
      return retrofit.create(APIService.class);
    }
  }
}
