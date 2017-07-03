package com.example.android_auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android_auth.http.Api;
import com.example.android_auth.http.AuthenticationParameters;
import com.example.android_auth.util.IOUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class LoginActivity
  extends AppCompatActivity
{
  private static String[] PERMISSIONS_STORAGE = { "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static final int REQUEST_SIGNUP = 0;
  private static final String TAG = "LoginActivity";
  private Button _loginButton;
  private EditText _passwordText;
  private EditText _usernameText;
  private AppCompatActivity activity = this;
  private Api apiLib;
  private String exampleUrl = "https://zxylieipe.wargame.ndh/login";
  
  public LoginActivity() {}
  
  private void doRequest(final String paramString1, final String paramString2)
  {
    try
    {
      AuthenticationParameters localAuthenticationParameters = new AuthenticationParameters();
      localAuthenticationParameters.setClientCertificate(getClientCertFile());
      localAuthenticationParameters.setClientCertificatePassword("password");
      localAuthenticationParameters.setCaCertificate(readCaCert());
      this.apiLib = new Api(localAuthenticationParameters);
      new AsyncTask()
      {
        protected Object doInBackground(Object... paramAnonymousVarArgs)
        {
          try
          {
            String str1 = new Uri.Builder().appendQueryParameter("username", paramString1).appendQueryParameter("password", paramString2).build().getEncodedQuery();
            String str2 = LoginActivity.this.apiLib.doPost(LoginActivity.this.exampleUrl, str1);
            int i = LoginActivity.this.apiLib.getLastResponseCode();
            if (i == 200)
            {
              publishProgress(new Object[] { str2 });
            }
            else
            {
              Object[] arrayOfObject2 = new Object[1];
              arrayOfObject2[0] = ("HTTP Response Code: " + i);
              publishProgress(arrayOfObject2);
            }
          }
          catch (Throwable localThrowable)
          {
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream);
            localThrowable.printStackTrace(localPrintWriter);
            localPrintWriter.flush();
            localPrintWriter.close();
            Object[] arrayOfObject1 = new Object[1];
            arrayOfObject1[0] = (localThrowable.toString() + " : " + localByteArrayOutputStream.toString());
            publishProgress(arrayOfObject1);
          }
          return null;
        }
        
        protected void onPostExecute(Object paramAnonymousObject)
        {
          Log.i("LoginActivity", "Done!");
        }
        
        protected void onProgressUpdate(Object... paramAnonymousVarArgs)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          int i = paramAnonymousVarArgs.length;
          for (int j = 0; j < i; j++) {
            localStringBuilder.append(paramAnonymousVarArgs[j].toString());
          }
          Log.i("LoginActivity", localStringBuilder.toString());
        }
      }.execute(new Object[0]);
      return;
    }
    catch (Exception localException)
    {
      Log.e("LoginActivity", "failed to create timeApi", localException);
    }
  }
  
  private File getClientCertFile()
    throws IOException
  {
    InputStream localInputStream = getResources().openRawResource(getResources().getIdentifier("client", "raw", getPackageName()));
    localFile = new File(Environment.getExternalStorageDirectory(), "client_tmp.pfx");
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile, true);
    try
    {
      byte[] arrayOfByte = new byte[1048576];
      for (;;)
      {
        int i = localInputStream.read(arrayOfByte, 0, 1048576);
        if (i == -1)
        {
          localInputStream.close();
          localFileOutputStream.close();
          return localFile;
        }
        localFileOutputStream.write(arrayOfByte, 0, i);
      }
      return localFile;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private String readCaCert()
    throws Exception
  {
    return IOUtil.readFully(getResources().openRawResource(getResources().getIdentifier("ca", "raw", getPackageName())));
  }
  
  public static void verifyStoragePermissions(Activity paramActivity)
  {
    if (ActivityCompat.checkSelfPermission(paramActivity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
      ActivityCompat.requestPermissions(paramActivity, PERMISSIONS_STORAGE, 1);
    }
  }
  
  public void login()
  {
    Log.d("LoginActivity", "Login");
    if (!validate())
    {
      onLoginFailed();
      return;
    }
    this._loginButton.setEnabled(false);
    final ProgressDialog localProgressDialog = new ProgressDialog(this, 2131427496);
    localProgressDialog.setIndeterminate(true);
    localProgressDialog.setMessage(getString(2131296294));
    localProgressDialog.show();
    doRequest(this._usernameText.getText().toString(), this._passwordText.getText().toString());
    new Handler().postDelayed(new Runnable()
    {
      public void run()
      {
        LoginActivity.this.onLoginFailed();
        localProgressDialog.dismiss();
      }
    }, 3000L);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 0) && (paramInt2 == -1)) {
      finish();
    }
  }
  
  public void onBackPressed()
  {
    moveTaskToBack(true);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968603);
    this._usernameText = ((EditText)findViewById(2131689587));
    this._passwordText = ((EditText)findViewById(2131689588));
    this._loginButton = ((Button)findViewById(2131689589));
    this._usernameText.setText(getString(2131296297));
    this._loginButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        LoginActivity.this.login();
      }
    });
    verifyStoragePermissions(this);
  }
  
  public void onLoginFailed()
  {
    Toast.makeText(getBaseContext(), getString(2131296292), 1).show();
    this._loginButton.setEnabled(true);
  }
  
  public void onLoginSuccess()
  {
    this._loginButton.setEnabled(true);
    finish();
  }
  
  public boolean validate()
  {
    boolean bool = true;
    String str1 = this._usernameText.getText().toString();
    String str2 = this._passwordText.getText().toString();
    if (str1.isEmpty())
    {
      this._usernameText.setError(getString(2131296299));
      bool = false;
    }
    while ((str2.isEmpty()) || (str2.length() != 15) || (!str2.matches("[a-z]+")))
    {
      this._passwordText.setError(getString(2131296298));
      return false;
      this._usernameText.setError(null);
    }
    this._passwordText.setError(null);
    return bool;
  }
}
