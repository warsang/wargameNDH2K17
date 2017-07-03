package com.example.android_auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
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

public class LoginActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_STORAGE = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "LoginActivity";
    private Button _loginButton;
    private EditText _passwordText;
    private EditText _usernameText;
    private AppCompatActivity activity = this;
    private Api apiLib;
    private String exampleUrl = "https://zxylieipe.wargame.ndh/login";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this._usernameText = (EditText) findViewById(R.id.input_username);
        this._passwordText = (EditText) findViewById(R.id.input_password);
        this._loginButton = (Button) findViewById(R.id.btn_login);
        this._usernameText.setText(getString(R.string.default_user));
        this._loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.login();
            }
        });
        verifyStoragePermissions(this);
    }

    public void login() {
        Log.d(TAG, "Login");
        if (validate()) {
            this._loginButton.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme.Dark.Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
            doRequest(this._usernameText.getText().toString(), this._passwordText.getText().toString());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    LoginActivity.this.onLoginFailed();
                    progressDialog.dismiss();
                }
            }, 3000);
            return;
        }
        onLoginFailed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == -1) {
            finish();
        }
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        this._loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.auth_error), 1).show();
        this._loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = this._usernameText.getText().toString();
        String password = this._passwordText.getText().toString();
        if (username.isEmpty()) {
            this._usernameText.setError(getString(R.string.error_invalid_username));
            valid = false;
        } else {
            this._usernameText.setError(null);
        }
        if (!password.isEmpty() && password.length() == 15 && password.matches("[a-z]+")) {
            this._passwordText.setError(null);
            return valid;
        }
        this._passwordText.setError(getString(R.string.error_invalid_password));
        return false;
    }

    private void doRequest(String username, String password) {
        final String innerUsername = username;
        final String innerPassword = password;
        try {
            AuthenticationParameters authParams = new AuthenticationParameters();
            authParams.setClientCertificate(getClientCertFile());
            authParams.setClientCertificatePassword("password");
            authParams.setCaCertificate(readCaCert());
            this.apiLib = new Api(authParams);
            new AsyncTask() {
                protected Object doInBackground(Object... objects) {
                    try {
                        String result = LoginActivity.this.apiLib.doPost(LoginActivity.this.exampleUrl, new Builder().appendQueryParameter("username", innerUsername).appendQueryParameter("password", innerPassword).build().getEncodedQuery());
                        if (LoginActivity.this.apiLib.getLastResponseCode() == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                            publishProgress(new Object[]{result});
                        } else {
                            publishProgress(new Object[]{"HTTP Response Code: " + LoginActivity.this.apiLib.getLastResponseCode()});
                        }
                    } catch (Throwable ex) {
                        PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
                        ex.printStackTrace(writer);
                        writer.flush();
                        writer.close();
                        publishProgress(new Object[]{ex.toString() + " : " + baos.toString()});
                    }
                    return null;
                }

                protected void onProgressUpdate(Object... values) {
                    StringBuilder buf = new StringBuilder();
                    for (Object value : values) {
                        buf.append(value.toString());
                    }
                    Log.i(LoginActivity.TAG, buf.toString());
                }

                protected void onPostExecute(Object result) {
                    Log.i(LoginActivity.TAG, "Done!");
                }
            }.execute(new Object[0]);
        } catch (Exception ex) {
            Log.e(TAG, "failed to create timeApi", ex);
        }
    }

    private File getClientCertFile() throws IOException {
        InputStream is = getResources().openRawResource(getResources().getIdentifier("client", "raw", getPackageName()));
        File f = new File(Environment.getExternalStorageDirectory(), "client_tmp.pfx");
        OutputStream os = new FileOutputStream(f, true);
        try {
            byte[] bytes = new byte[1048576];
            while (true) {
                int count = is.read(bytes, 0, 1048576);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
            is.close();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return f;
    }

    private String readCaCert() throws Exception {
        return IOUtil.readFully(getResources().openRawResource(getResources().getIdentifier("ca", "raw", getPackageName())));
    }

    public static void verifyStoragePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }
}
