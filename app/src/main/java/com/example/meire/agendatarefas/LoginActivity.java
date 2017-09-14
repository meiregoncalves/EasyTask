package com.example.meire.agendatarefas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meire.agendatarefas.dao.loginDAO;
import com.example.meire.agendatarefas.model.LoginModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private String msg = "";
    private UserLoginTask mAuthTask = null;
    private loginDAO loginDAO;

    private EditText tvLogin;
    private EditText tvPassword;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox cbKeepConected;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                executeGraphRequest(loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.login_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin = (EditText) findViewById(R.id.user);
        cbKeepConected = (CheckBox) findViewById(R.id.cbKeepConected);
        tvPassword = (EditText) findViewById(R.id.password);
        tvPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void executeGraphRequest(final String userId){
        GraphRequest request =new GraphRequest(AccessToken.getCurrentAccessToken(), userId, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                final LoginModel loginface = new LoginModel();

                try {
                    loginface.setUser(response.getJSONObject().get("name").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!cbKeepConected.isChecked()){
                    LoginManager.getInstance().logOut();
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("LOGIN", loginface);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        tvLogin.setError(null);
        tvPassword.setError(null);

        String user = tvLogin.getText().toString();
        String password = tvPassword.getText().toString();
        Boolean keepConected = cbKeepConected.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            tvPassword.setError(getString(R.string.error_field_required));
            focusView = tvPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            tvLogin.setError(getString(R.string.error_field_required));
            focusView = tvLogin;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password, keepConected);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUser;
        private final String mPassword;
        private final Boolean mKeepConected;

        UserLoginTask(String user, String password, Boolean keepConected) {
            mUser = user;
            mPassword = password;
            mKeepConected = keepConected;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                msg = "error";
                return false;
            }

            LoginModel loginInfo = new LoginModel();
            loginInfo.setUser(mUser);
            loginInfo.setPassword(mPassword);

            loginDAO = new loginDAO(LoginActivity.this);

            if (!loginDAO.getUserExists(loginInfo.getUser())){
                msg = "euser";
                return false;
            }
            else if (!loginDAO.getUserExists(loginInfo)){
                msg = "epass";
                return false;
            }
            else
            {
                msg = "";
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                LoginModel loginModel = new LoginModel();
                loginModel.setUser(mUser);
                loginModel.setPassword(mPassword);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor e = sp.edit();

                try {
                    if (mKeepConected) {
                        e.putString("user", mUser);
                        e.putString("password", mPassword);
                        e.putBoolean("keepConected", mKeepConected);
                    } else {
                        e.putString("user", "");
                        e.putString("password", "");
                        e.putBoolean("keepConected", false);
                    }
                    e.apply();
                }
                catch (Exception e1){
                    Toast.makeText(LoginActivity.this,  R.string.error_login, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("LOGIN", loginModel);
                startActivity(intent);
                LoginActivity.this.finish();
            } else if (msg == "euser"){
                tvLogin.setError(getString(R.string.error_incorrect_login));
                tvLogin.requestFocus();
                msg = "";
            } else if (msg == "epass"){
                tvPassword.setError(getString(R.string.error_incorrect_password));
                tvPassword.requestFocus();
                msg = "";
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

