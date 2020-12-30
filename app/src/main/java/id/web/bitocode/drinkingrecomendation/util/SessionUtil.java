package id.web.bitocode.drinkingrecomendation.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import id.web.bitocode.drinkingrecomendation.config.Constants;
import id.web.bitocode.drinkingrecomendation.model.UserModel;

public class SessionUtil
{
  SharedPreferences pref;
  SharedPreferences.Editor editor;
  Context _context;
  
  public SessionUtil(Context context)
  {
    this._context = context;
  }
  
  public boolean login(Context context, UserModel userModel)
  {
    pref = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    
    editor = pref.edit();
    
    String userJson = new Gson().toJson(userModel);
    editor.putString(Constants.USER_SESSION, userJson);
    editor.apply();
    return true;
  }
  
  public boolean isLoggedIn(Context context)
  {
    pref = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    
    String userJson = pref.getString(Constants.USER_SESSION, null);
    return userJson != null;
  }
  
  public UserModel getLoggedUser(Context context)
  {
    pref = context.getSharedPreferences(
           Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    String userJson = pref.getString(Constants.USER_SESSION, null);
    if (userJson != null)
    {
      return new Gson().fromJson(userJson, UserModel.class);
    }
    else
      return null;
  }
  
  public void logout(Context context)
  {
    pref = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    editor = pref.edit();
    editor.clear();
    editor.apply();
  
  }
}
