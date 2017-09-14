package com.example.meire.agendatarefas.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meire.agendatarefas.MainActivity;
import com.example.meire.agendatarefas.R;
import com.example.meire.agendatarefas.adapter.OnItemClickListener;
import com.example.meire.agendatarefas.adapter.OnLongClickListener;
import com.example.meire.agendatarefas.adapter.TaskAdapter;
import com.example.meire.agendatarefas.api.APIUtils;
import com.example.meire.agendatarefas.dao.tasksDAO;
import com.example.meire.agendatarefas.model.TaskModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowTasksFragment extends Fragment {

    private RecyclerView rvTasks;
    private tasksDAO tasksDAO;
    private TaskAdapter taskAdapter;
    private Activity activity;
    private MenuInflater menuInflater;
    private Menu menuFrag;
    private ArrayList<TaskModel> taskModelListSelected;
    private AlertDialog alerta;
    private TextView tvNoTasks;

    public ShowTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        taskModelListSelected = new ArrayList<TaskModel>();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemview = inflater.inflate(R.layout.fragment_show_tasks, container, false);

        tvNoTasks = (TextView) itemview.findViewById(R.id.tvNoTasks);
        rvTasks = (RecyclerView) itemview.findViewById(R.id.rvTasks);
        taskAdapter = new TaskAdapter(new ArrayList<TaskModel>(), new OnItemClickListener() {
            @Override
            public void onIemClick(View v, TaskModel item) {
                if (taskModelListSelected.size() > 0) {
                    if (!taskModelListSelected.contains(item)) {
                        v.setBackgroundColor(Color.argb(255,141,156,239));
                        menuFrag.getItem(3).setVisible(true);
                        if (item.getDone().equals("S")) {
                            menuFrag.getItem(2).setVisible(true);
                        }
                        if (item.getDone().equals("N")) {
                            menuFrag.getItem(1).setVisible(true);
                        }
                        taskModelListSelected.add(item);
                    }
                    else {
                        v.setBackgroundColor(Color.WHITE);
                        taskModelListSelected.remove(item);

                        if (taskModelListSelected.size() == 0) {
                            getActivity().onBackPressed();
                        }
                    }
                }
                else {
                    CadastroFragment cadastroFragment = new CadastroFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("task", item);
                    cadastroFragment.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, cadastroFragment);
                    transaction.addToBackStack("FRAG_CAD");
                    transaction.commit();
                }
            }
        }, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, TaskModel item) {
                v.setBackgroundColor(Color.argb(255,141,156,239));
                menuFrag.getItem(3).setVisible(true);
                if (item.getDone().equals("S")) {
                    menuFrag.getItem(2).setVisible(true);
                }
                if (item.getDone().equals("N")) {
                    menuFrag.getItem(1).setVisible(true);
                }
                taskModelListSelected.add(item);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("FRAG_CAD");
                transaction.commit();
                return true;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTasks.setLayoutManager(layoutManager);
        rvTasks.setAdapter(taskAdapter);
        rvTasks.setHasFixedSize(true);

        carregaDados();

        return itemview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuInflater = inflater;
        menuFrag = menu;
        menuInflater.inflate(R.menu.fast_access, menuFrag);
        menuFrag.getItem(1).setVisible(false);
        menuFrag.getItem(2).setVisible(false);
        menuFrag.getItem(3).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (taskModelListSelected.size() > 1) {
                    builder.setMessage(R.string.confirm_exclude_many);
                } else if (taskModelListSelected.size() == 1) {
                    builder.setMessage(R.string.confirm_del);
                }

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (TaskModel t : taskModelListSelected) {
                            if (tasksDAO.del(t.getId())) {
                                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), R.string.erro_deleting, Toast.LENGTH_SHORT).show();
                            }
                        }
                        getActivity().onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().onBackPressed();
                    }
                });
                alerta = builder.create();
                alerta.show();
                break;
            }
            case R.id.action_done: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (taskModelListSelected.size() > 1) {
                    builder.setMessage(R.string.mark_done);
                } else if (taskModelListSelected.size() == 1) {
                    builder.setMessage(R.string.mark_one_done);
                }

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (TaskModel t : taskModelListSelected) {
                            t.setDone("S");
                            tasksDAO.update(t);
                        }
                        getActivity().onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().onBackPressed();
                    }
                });
                alerta = builder.create();
                alerta.show();
                break;
            }
            case R.id.action_undone: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (taskModelListSelected.size() > 1) {
                    builder.setMessage(R.string.mark_undone);
                } else if (taskModelListSelected.size() == 1) {
                    builder.setMessage(R.string.mark_one_undone);
                }

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (TaskModel t : taskModelListSelected) {
                            t.setDone("N");
                            tasksDAO.update(t);
                        }
                        getActivity().onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().onBackPressed();
                    }
                });
                alerta = builder.create();
                alerta.show();
                break;
            }
        }

        return true;
    }

    private void  carregaDados()
    {
        tasksDAO = new tasksDAO(getContext());
        List<TaskModel> taskModels = tasksDAO.getAll();

        if (taskModels.size() > 0) {
            taskAdapter.update(taskModels);
            tvNoTasks.setVisibility(View.INVISIBLE);
        }
        else {
            tvNoTasks.setVisibility(View.VISIBLE);
            rvTasks.setVisibility(View.INVISIBLE);
        }
    }
}
