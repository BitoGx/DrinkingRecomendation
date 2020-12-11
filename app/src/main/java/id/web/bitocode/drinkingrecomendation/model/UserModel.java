package id.web.bitocode.drinkingrecomendation.model;

import java.util.List;

public class UserModel
{
  private String userid;
  private String nama;
  private String username;
  private String email;
  private String jeniskelamin;
  private String tanggallahir;
  private Integer tinggibadan;
  private Integer beratbadan;
  private Integer rekomendasipersonal;
  private String password;
  private String usertoken;
  
  public String getUsername()
  {
    return username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public String getEmail()
  {
    return email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getUsertoken()
  {
    return usertoken;
  }
  
  public void setUsertoken(String usertoken)
  {
    this.usertoken = usertoken;
  }
  
  public String getUserid()
  {
    return userid;
  }
  
  public void setUserid(String userid)
  {
    this.userid = userid;
  }
  
  public String getNama()
  {
    return nama;
  }
  
  public void setNama(String nama)
  {
    this.nama = nama;
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
  
  public Integer getTinggibadan()
  {
    return tinggibadan;
  }
  
  public void setTinggibadan(Integer tinggibadan)
  {
    this.tinggibadan = tinggibadan;
  }
  
  public Integer getBeratbadan()
  {
    return beratbadan;
  }
  
  public void setBeratbadan(Integer beratbadan)
  {
    this.beratbadan = beratbadan;
  }
  
  public Integer getRekomendasipersonal()
  {
    return rekomendasipersonal;
  }
  
  public void setRekomendasipersonal(Integer rekomendasipersonal)
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
  
  public String getUserToken()
  {
    return usertoken;
  }
  
  public void setUserToken(String usertoken)
  {
    this.usertoken = usertoken;
  }
  
  public class UserDataModel extends MessageModel
  {
    private List<UserModel> results;
    
    public List<UserModel> getResults() {
      return results;
    }
    
    public void setResults(List<UserModel> results) {
      this.results = results;
    }
  }
}
