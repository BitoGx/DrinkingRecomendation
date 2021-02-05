package id.web.bitocode.drinkingrecomendation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.web.bitocode.drinkingrecomendation.R;
import id.web.bitocode.drinkingrecomendation.model.RiwayatModel;

public class RiwayatRecyclerViewAdapter extends RecyclerView.Adapter<RiwayatRecyclerViewAdapter.ViewHolder>
{
  private final List<RiwayatModel> riwayatModelList;
  
  public RiwayatRecyclerViewAdapter(List<RiwayatModel> riwayatModelList)
  {
    this.riwayatModelList = riwayatModelList;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
  {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat,null);
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position)
  {
    RiwayatModel model = riwayatModelList.get(position);
    holder.tv_tanggal.setText(model.getTanggalaktivitas());
    holder.tv_jarak.setText(model.getJarak());
    holder.tv_waktu.setText(model.getWaktu());
    holder.tv_rec_awal.setText(model.getRekomendasiawal());
    holder.tv_rec_akhir.setText(model.getRekomendasiakhir());
  }
  
  @Override
  public int getItemCount()
  {
    return riwayatModelList.size();
  }
  
  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    private final TextView tv_tanggal,tv_jarak,tv_waktu,tv_rec_awal,tv_rec_akhir;
    
    public ViewHolder(View view)
    {
      super(view);
      tv_tanggal   = view.findViewById(R.id.tv_item_riwayat_gettanggal);
      tv_jarak     = view.findViewById(R.id.tv_item_riwayat_getjarak);
      tv_waktu     = view.findViewById(R.id.tv_item_riwayat_getwaktu);
      tv_rec_awal  = view.findViewById(R.id.tv_item_riwayat_getrekomendasiawal);
      tv_rec_akhir = view.findViewById(R.id.tv_item_riwayat_getrekomendasiakhir);
    }
  }
  
  protected void publishData(List<RiwayatModel> riwayatModelList)
  {
    riwayatModelList = riwayatModelList;
    notifyDataSetChanged();
  }
}