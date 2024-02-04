package com.dreamer.matholympappv1.ui.ui.razdel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Razdel;

import java.util.List;

public class RazdelAdapter extends RecyclerView.Adapter<RazdelAdapter.RazdelViewHolder> {
    private List<Razdel> razdelList;
    private OnItemClickListener listener;

    public RazdelAdapter(List<Razdel> razdelList, OnItemClickListener listener) {
        this.razdelList = razdelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RazdelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_razdel, parent, false);
        return new RazdelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RazdelViewHolder holder, int position) {
        Razdel razdel = razdelList.get(position);
        holder.bind(razdel, listener);
    }

    @Override
    public int getItemCount() {
        return razdelList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Razdel razdel);
    }

    public static class RazdelViewHolder extends RecyclerView.ViewHolder {
        private TextView razdelNameTextView;

        public RazdelViewHolder(@NonNull View itemView) {
            super(itemView);
            razdelNameTextView = itemView.findViewById(R.id.razdel_content);
        }

        public void bind(final Razdel razdel, final OnItemClickListener listener) {
            razdelNameTextView.setText(razdel.getRazdelname());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(razdel);
                    }
                }
            });
        }
    }
}
