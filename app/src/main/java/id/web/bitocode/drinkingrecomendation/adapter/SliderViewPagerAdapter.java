package id.web.bitocode.drinkingrecomendation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import id.web.bitocode.drinkingrecomendation.model.SliderModel;

public class SliderViewPagerAdapter extends PagerAdapter
{
  private final Context context;
  private SliderModel sliderModel;
  
  public SliderViewPagerAdapter(Context context)
  {
    this.context = context;
  }
  
  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position)
  {
    sliderModel = SliderModel.values()[position];
    LayoutInflater inflater = LayoutInflater.from(context);
    ViewGroup layout = (ViewGroup) inflater.inflate(sliderModel.getLayoutResId(), container, false);
    container.addView(layout);
    return layout;
  }
  
  @Override
  public void destroyItem(ViewGroup collection, int position, @NonNull Object view)
  {
    collection.removeView((View) view);
  }
  
  @Override
  public int getCount()
  {
    return SliderModel.values().length;
  }
  
  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
  {
    return view == object;
  }
  
  @Override
  public CharSequence getPageTitle(int position)
  {
    sliderModel = SliderModel.values()[position];
    return context.getString(sliderModel.getTitleResId());
  }
}
