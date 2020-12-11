package id.web.bitocode.drinkingrecomendation.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import id.web.bitocode.drinkingrecomendation.config.Constants;
import id.web.bitocode.drinkingrecomendation.model.UserModel;

public class SessionUtil
{
  public static boolean login(Context context, UserModel userModel)
  {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    String userJson = new Gson().toJson(userModel);
    editor.putString(Constants.USER_SESSION, userJson);
    editor.apply();
    return true;
  }
  
  public static boolean isLoggedIn(Context context)
  {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    
    String userJson = sharedPreferences.getString(Constants.USER_SESSION, null);
    return userJson != null;
  }
  
  public static UserModel getLoggedUser(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    String userJson = sharedPreferences.getString(Constants.USER_SESSION, null);
    if (userJson != null)
    {
      return new Gson().fromJson(userJson, UserModel.class);
    }
    else
      return null;
  }
  
  public static void logout(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.KEY_USER_SESSION, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
  
  }
}
