package id.web.bitocode.drinkingrecomendation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelectUserModel extends MessageModel
{
  @SerializedName("userid")
  private String userid;
  @SerializedName("nama")
  private String nama;
  @SerializedName("username")
  private String username;
  @SerializedName("email")
  private String email;
  @SerializedName("jeniskelamin")
  private String jeniskelamin;
  @SerializedName("tanggallahir")
  private String tanggallahir;
  @SerializedName("tinggibadan")
  private String tinggibadan;
  @SerializedName("beratbadan")
  private String beratbadan;
  @SerializedName("rekomendasipersonal")
  private String rekomendasipersonal;
  @SerializedName("password")
  private String password;
  @SerializedName("usertoken")
  private String usertoken;
  
  
  public String getUserid()
  {
    return userid;
  }
  
  public void setUserid(String userid)
  {
    this.userid = userid;
  }
  
  public String getEmail()
  {
    return email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getNama()
  {
    return nama;
  }
  
  public void setNama(String nama)
  {
    this.nama = nama;
  }
  
  public String getUsername()
  {
    return username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public String getJeniskelamin()
  {
    return jeniskelamin;
  }
  
  public void setJeniskelamin(String jeniskelamin)
  {
    this.jeniskelamin = jeniskelamin;
  }
  
  public String getTanggallahir()
  {
    return tanggallahir;
  }
  
  public void setTanggallahir(String tanggallahir)
  {
    this.tanggallahir = tanggallahir;
  }
  
  public String getTinggibadan()
  {
    return tinggibadan;
  }
  
  public void setTinggibadan(String tinggibadan)
  {
    this.tinggibadan = tinggibadan;
  }
  
  public String getBeratbadan()
  {
    return beratbadan;
  }
  
  public void setBeratbadan(String beratbadan)
  {
    this.beratbadan = beratbadan;
  }
  
  public String getRekomendasipersonal()
  {
    return rekomendasipersonal;
  }
  
  public void setRekomendasipersonal(String rekomendasipersonal)
  {
    this.rekomendasipersonal = rekomendasipersonal;
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getUsertoken()
  {
    return usertoken;
  }
  
  public void setUsertoken(String usertoken)
  {
    this.usertoken = usertoken;
  }
  
}
