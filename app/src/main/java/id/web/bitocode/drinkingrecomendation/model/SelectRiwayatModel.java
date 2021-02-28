package id.web.bitocode.drinkingrecomendation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelectRiwayatModel extends MessageModel
{
  @SerializedName("riwayatid")
  private String riwayatid;
  @SerializedName("jenisaktivitas")
  private String jenisaktivitas;
  @SerializedName("tanggalaktivitas")
  private String tanggalaktivitas;
  @SerializedName("waktu")
  private String waktu;
  @SerializedName("jarak")
  private String jarak;
  @SerializedName("rekomendasiawal")
  private String rekomendasiawal;
  @SerializedName("rekomendasiakhir")
  private String rekomendasiakhir;
  
  public String getRiwayatid()
  {
    return riwayatid;
  }
  
  public void setRiwayatid(String riwayatid)
  {
    this.riwayatid = riwayatid;
  }
  
  public String getJenisaktivitas()
  {
    return jenisaktivitas;
  }
  
  public void setJenisaktivitas(String jenisaktivitas)
  {
    this.jenisaktivitas = jenisaktivitas;
  }
  
  public String getTanggalaktivitas()
  {
    return tanggalaktivitas;
  }
  
  public void setTanggalaktivitas(String tanggalaktivitas)
  {
    this.tanggalaktivitas = tanggalaktivitas;
  }
  
  public String getWaktu()
  {
    return waktu;
  }
  
  public void setWaktu(String waktu)
  {
    this.waktu = waktu;
  }
  
  public String getJarak()
  {
    return jarak;
  }
  
  public void setJarak(String jarak)
  {
    this.jarak = jarak;
  }
  
  public String getRekomendasiawal()
  {
    return rekomendasiawal;
  }
  
  public void setRekomendasiawal(String rekomendasiawal)
  {
    this.rekomendasiawal = rekomendasiawal;
  }
  
  public String getRekomendasiakhir()
  {
    return rekomendasiakhir;
  }
  
  public void setRekomendasiakhir(String rekomendasiakhir)
  {
    this.rekomendasiakhir = rekomendasiakhir;
  }
  
  public static class RiwayatDataModel extends MessageModel
  {
    @SerializedName("results")
    private List<RiwayatModel> results;
    
    public List<RiwayatModel> getResults()
    {
      return results;
    }
    
    public void setResults(List<RiwayatModel> results)
    {
      this.results = results;
    }
  }
}
