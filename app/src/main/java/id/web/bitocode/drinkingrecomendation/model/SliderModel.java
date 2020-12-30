package id.web.bitocode.drinkingrecomendation.model;


import id.web.bitocode.drinkingrecomendation.R;

public enum SliderModel
{
  
  PAGE1(R.string.slide_1_title, R.layout.welcome_slide1),
  PAGE2(R.string.slide_2_title, R.layout.welcome_slide2),
  PAGE3(R.string.slide_3_title, R.layout.welcome_slide3);
  
  private final int mTitleResId;
  private final int mLayoutResId;
  
  SliderModel(int titleResId, int layoutResId)
  {
    mTitleResId = titleResId;
    mLayoutResId = layoutResId;
  }
  
  public int getTitleResId()
  {
    return mTitleResId;
  }
  
  public int getLayoutResId()
  {
    return mLayoutResId;
  }
}
