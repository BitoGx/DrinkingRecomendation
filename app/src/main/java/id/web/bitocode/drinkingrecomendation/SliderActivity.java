package id.web.bitocode.drinkingrecomendation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.web.bitocode.drinkingrecomendation.adapter.SliderViewPagerAdapter;
import id.web.bitocode.drinkingrecomendation.model.SliderModel;
import id.web.bitocode.drinkingrecomendation.util.PrefManager;

public class SliderActivity extends AppCompatActivity
{
  private ViewPager viewPager;
  private ViewPager.OnPageChangeListener viewPagerPageChangeListener;
  private LinearLayout dotsLayout;
  private Button btnSkip, btnNext;
  private PrefManager prefManager;
  private SliderViewPagerAdapter sliderViewPagerAdapter;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_slider);
    
    checkFirstTimeLaunch();
    inisialisasi();
  }
  
  private void launchLoginActivity(Context context)
  {
    prefManager.setFirstTimeLaunch(false);
    startActivity(new Intent(context, LoginActivity.class));
    finish();
  }
  
  private void checkFirstTimeLaunch()
  {
    prefManager = new PrefManager(this);
    if(!prefManager.isFirstTimeLaunch())
    {
      launchLoginActivity(this);
    }
  }
  
  private void addBottomDots(int currentPage)
  {
    TextView[] dots = new TextView[SliderModel.values().length];
    
    int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
    int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
    
    dotsLayout.removeAllViews();
    
    for (int i = 0; i < dots.length; i++)
    {
      dots[i] = new TextView(this);
      dots[i].setText(Html.fromHtml("&#8226;"));
      dots[i].setTextSize(35);
      dots[i].setTextColor(colorsInactive[currentPage]);
      dotsLayout.addView(dots[i]);
    }
    
    if (dots.length > 0)
      dots[currentPage].setTextColor(colorsActive[currentPage]);
  }
  
  private int getItem()
  {
    return viewPager.getCurrentItem() + 1;
  }
  
  private void inisialisasi()
  {
    viewPager  = findViewById(R.id.view_pager);
    dotsLayout = findViewById(R.id.layoutDots);
    btnSkip    = findViewById(R.id.btn_skip);
    btnNext    = findViewById(R.id.btn_next);
  
    addBottomDots(0);
  
    sliderViewPagerAdapter = new SliderViewPagerAdapter(this);
    viewPager.setAdapter(sliderViewPagerAdapter);
    ViewPagerListener();
    viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
  }
  
  private void ViewPagerListener()
  {
    viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
    {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
      {
      
      }
    
      @Override
      public void onPageSelected(int position)
      {
        addBottomDots(position);
      
        if (position == SliderModel.values().length - 1)
        {
          btnNext.setText(getString(R.string.start));
          btnSkip.setVisibility(View.GONE);
        }
        else
        {
          btnNext.setText(getString(R.string.next));
          btnSkip.setVisibility(View.VISIBLE);
        }
      }
    
      @Override
      public void onPageScrollStateChanged(int state)
      {
      
      }
    };
  }

  public void onNextClick(View view)
  {
    int current = getItem();
    if (current < SliderModel.values().length)
    {
      viewPager.setCurrentItem(current);
    }
    else
    {
      launchLoginActivity(this);
    }
  }


  public void onSkipClick(View view)
  {
    launchLoginActivity(this);
  }
}