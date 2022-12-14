package com.example.whim.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whim.Models.Notes;
import com.example.whim.NotesClickListener;
import com.example.whim.R;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NotesViewHolder>{
    Context context;

    public NoteListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    List<Notes> list;
    NotesClickListener listener;

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent,  false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_title.setSelected(true);
        holder.note_content.setText(list.get(position).getNotes());
        holder.time_text.setText(list.get(position).getDate());

        holder.notes_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onClick((list.get(holder.getAdapterPosition())));
            }
        });


        // deal with long click
        holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notes_container);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Notes> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    CardView notes_container;
    TextView textView_title, note_content, time_text;
    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        note_content = itemView.findViewById(R.id.note_content);
        time_text = itemView.findViewById(R.id.note_time);
    }
}
