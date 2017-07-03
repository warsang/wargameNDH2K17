package com.example.android_auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity
  extends ActionBarActivity
{
  public MainActivity() {}
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968604);
    startActivity(new Intent(this, LoginActivity.class));
  }
}
