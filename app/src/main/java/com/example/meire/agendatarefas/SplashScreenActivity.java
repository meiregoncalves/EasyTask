package com.example.meire.agendatarefas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meire.agendatarefas.api.APIUtils;
import com.example.meire.agendatarefas.api.LoginAPI;
import com.example.meire.agendatarefas.dao.loginDAO;
import com.example.meire.agendatarefas.model.LoginModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private int SPLASH_DISPLAY_LENGTH = 2000;
    private LoginAPI loginAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getIntent().getIntExtra("IDTASK",0) > 0) {
            SPLASH_DISPLAY_LENGTH = 0;
        }
        carrega();
    }

    private void carrega(){
        Animation anim = AnimationUtils.loadAnimation(this,
                R.anim.animacaosplash);
        anim.reset();

        ImageView iv = (ImageView) findViewById(R.id.splash);
        if (iv != null) {
            iv.clearAnimation();
            iv.startAnimation(anim);
        }

        loginAPI = APIUtils.getLoginService();
        loginAPI.getLogin().enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    final String user = response.body().getUser();
                    final String passwrd = response.body().getPassword();
                    final LoginModel mlogin = response.body();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);

                    loginDAO loginDAO = new loginDAO(SplashScreenActivity.this);
                    if (!loginDAO.add(response.body())){
                        Toast.makeText(SplashScreenActivity.this,  R.string.failed_to_access, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (AccessToken.getCurrentAccessToken() != null) {
                        executeGraphRequest(AccessToken.getCurrentAccessToken().getUserId());
                    }
                    else if (sp.getBoolean("keepConected", false) && sp.getString("user","").equals(user) && sp.getString("password","").equals(passwrd)){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this,
                                        MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("LOGIN", mlogin);

                                if (getIntent() != null)
                                {
                                    intent.putExtra("IDTASK", getIntent().getIntExtra("IDTASK",0));
                                }

                                startActivity(intent);
                                SplashScreenActivity.this.finish();
                            }
                        }, SPLASH_DISPLAY_LENGTH);
                    }
                    else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this,
                                        LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                SplashScreenActivity.this.finish();
                            }
                        }, SPLASH_DISPLAY_LENGTH);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Toast.makeText(SplashScreenActivity.this, R.string.failed_to_access, Toast.LENGTH_LONG).show();
                onDestroy();
            }
        });
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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreenActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("LOGIN", loginface);

                        if (getIntent() != null)
                        {
                            intent.putExtra("IDTASK", getIntent().getIntExtra("IDTASK",0));
                        }

                        startActivity(intent);
                        SplashScreenActivity.this.finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
