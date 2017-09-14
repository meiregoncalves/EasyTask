package com.example.meire.agendatarefas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.meire.agendatarefas.R;
import com.example.meire.agendatarefas.model.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.AndroidViewHolder> {
    private List<TaskModel> tasks;
    private OnItemClickListener listener;
    private OnLongClickListener listenerlong;

    public TaskAdapter(List<TaskModel> tasks)
    {
        this.tasks = tasks;
    }

    public TaskAdapter(List<TaskModel> tasks, OnItemClickListener listener, OnLongClickListener listenerlong) {
        this.tasks = tasks;
        this.listener = listener;
        this.listenerlong = listenerlong;
    }

    @Override
    public AndroidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View meuLayout = inflater.inflate(R.layout.taskrow, parent, false);

        return new AndroidViewHolder(meuLayout);
    }

    @Override
    public void onBindViewHolder(final AndroidViewHolder holder, final int position) {
        holder.tvTitulo.setText(tasks.get(position).getTitle());
        if (tasks.get(position).getDescription().length() > 80) {
            holder.tvSubtitulo.setText(tasks.get(position).getDescription().substring(0, 80) + " ...");
        }
        else {
            holder.tvSubtitulo.setText(tasks.get(position).getDescription());
        }
        holder.ivEdit.setImageResource(R.drawable.edit);

        if (tasks.get(position).getDone().equals("S")) {
            holder.ivLogo.setImageResource(R.drawable.todoicon);
        }
        else {
            holder.ivLogo.setImageResource(R.drawable.todoicon_not);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIemClick(v, tasks.get(position));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return listenerlong.onLongClick(view, tasks.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class AndroidViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView ivLogo;
        public ImageView ivEdit;
        public TextView tvTitulo;
        public TextView tvSubtitulo;

        public AndroidViewHolder(View itemView) {
            super(itemView);

            ivLogo = (ImageView) itemView.findViewById(R.id.ivLogo);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = (TextView) itemView.findViewById(R.id.tvSubTitulo);
        }
    }

    public void update(List<TaskModel> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }
}