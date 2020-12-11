package id.web.bitocode.drinkingrecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.web.bitocode.drinkingrecomendation.util.PrefManager;

public class SliderActivity extends AppCompatActivity
{
  private ViewPager viewPager;
  private LinearLayout dotsLayout;
  private int[] layouts;
  private Button btnSkip, btnNext;
  private PrefManager prefManager;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    prefManager = new PrefManager(this);
    if(!prefManager.isFirstTimeLaunch())
    {
      launchHomeScreen();
      finish();
    }
    
    setContentView(R.layout.activity_slider);
  
    viewPager  = findViewById(R.id.view_pager);
    dotsLayout = findViewById(R.id.layoutDots);
    btnSkip    = findViewById(R.id.btn_skip);
    btnNext    = findViewById(R.id.btn_next);
    
    layouts = new int[]{
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3};
    
    addBottomDots(0);
  
    MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
    viewPager.setAdapter(myViewPagerAdapter);
    viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
  
    btnSkip.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        launchHomeScreen();
      }
    });
    
    btnNext.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        int current = getItem(+1);
        if (current < layouts.length)
        {
          viewPager.setCurrentItem(current);
        }
        else
        {
          launchHomeScreen();
        }
      }
    });
  }
  
  private void addBottomDots(int currentPage)
  {
    TextView[] dots = new TextView[layouts.length];
    
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
  
  private int getItem(int i)
  {
    return viewPager.getCurrentItem() + i;
  }
  
  private void launchHomeScreen()
  {
    prefManager.setFirstTimeLaunch(false);
    startActivity(new Intent(SliderActivity.this, LoginActivity.class));
    finish();
  }

  ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
  {
    
    @Override
    public void onPageSelected(int position)
    {
      addBottomDots(position);
      
      if (position == layouts.length - 1)
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
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    
    }
    
    @Override
    public void onPageScrollStateChanged(int arg0) {
    
    }
  };
  
  public class MyViewPagerAdapter extends PagerAdapter
  {
  
    public MyViewPagerAdapter()
    {
    }
    
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
      LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      
      View view = layoutInflater.inflate(layouts[position], container, false);
      container.addView(view);
      
      return view;
    }
    
    @Override
    public int getCount()
    {
      return layouts.length;
    }
    
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj)
    {
      return view == obj;
    }
    
    
    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object)
    {
      View view = (View) object;
      container.removeView(view);
    }
  }
}