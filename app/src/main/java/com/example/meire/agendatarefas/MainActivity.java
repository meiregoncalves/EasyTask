package com.example.meire.agendatarefas;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meire.agendatarefas.dao.loginDAO;
import com.example.meire.agendatarefas.dao.tasksDAO;
import com.example.meire.agendatarefas.fragments.AboutFragment;
import com.example.meire.agendatarefas.fragments.CadastroFragment;
import com.example.meire.agendatarefas.fragments.ShowTasksFragment;
import com.example.meire.agendatarefas.model.LoginModel;
import com.example.meire.agendatarefas.model.TaskModel;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LoginModel loginModel;
    private TextView tvUser;
    private Toolbar toolbar;
    private AlertDialog alerta;
    private tasksDAO tasksDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null)
        {
            loginModel = getIntent().getParcelableExtra("LOGIN");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvUser = (TextView) navigationView.getHeaderView(0).findViewById (R.id.tvUser);
        tvUser.setText(loginModel.getUser());
        OpenList();

        if (getIntent() != null)
        {
            if (getIntent().getIntExtra("IDTASK", 0) > 0) {
                tasksDAO = new tasksDAO(this);
                TaskModel taskModel = tasksDAO.getBy(getIntent().getIntExtra("IDTASK", 0));
                CadastroFragment cadastroFragment = new CadastroFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("task", taskModel);
                cadastroFragment.setArguments(bundle);

                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_main, cadastroFragment);

                transaction.addToBackStack("FRAG_CAD");
                transaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int valueBackStack = fragmentManager.getBackStackEntryCount();
        if (valueBackStack> 0 ) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if(String.valueOf(fragmentManager.getBackStackEntryAt(valueBackStack-1).getName()).equals("FRAG_CAD")){
                    OpenList();
                }
                else if(String.valueOf(fragmentManager.getBackStackEntryAt(valueBackStack-1).getName()).equals("FRAG_SHOW")){
                    finish();
                }
                else {
                    super.onBackPressed();
                }
            }
        }else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case (R.id.logout): {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.confirm_disconect);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor e = sp.edit();

                            e.putString("user", "");
                            e.putString("password", "");
                            e.putBoolean("keepConected", false);
                            e.commit();
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                            finish();
                        } catch (Exception e1) {
                            Toast.makeText(MainActivity.this, R.string.error_closing, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alerta = builder.create();
                alerta.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case (R.id.nav_new): {
                Opencadastro();
                break;
            }
            case (R.id.nav_about):{
                OpenAbout();
                break;
            }
            case (R.id.nav_share): {
                ShareTasks();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Opencadastro()
    {
        CadastroFragment cadastroFragment = new CadastroFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, cadastroFragment);
        transaction.addToBackStack("FRAG_CAD");
        transaction.commit();
        toolbar.setTitle(R.string.new_task);
    }

    public void OpenList(){
        ShowTasksFragment showTasksFragment = new ShowTasksFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, showTasksFragment);
        transaction.addToBackStack("FRAG_SHOW");
        transaction.commit();
        toolbar.setTitle(R.string.app_name);
    }

    public void OpenAbout(){
        AboutFragment aboutFragment = new AboutFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, aboutFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void ShareTasks() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        tasksDAO tasksDAO = new tasksDAO(MainActivity.this);
        List<TaskModel> taskModels = tasksDAO.getAll();

        String texto = getString(R.string.my_tasks);

        for (TaskModel t : taskModels) {
            if (t.getDone().equals("N")) {
                texto += "\n\n- " + t.getTitle() + " \n " + t.getDescription();
            }
        }

        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    }
}
