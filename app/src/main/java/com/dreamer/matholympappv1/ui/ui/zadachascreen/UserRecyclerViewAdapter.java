package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Users;
import com.dreamer.matholympappv1.data.model.model.Zadachi;

import java.util.List;


public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "TAG";
    private List<Users> usersList;
    private List<Zadachi> zadachiList;
    private Context context;

    public UserRecyclerViewAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(usersList.get(position).getUsername());
//         CircleImageView circleImageView = holder.circleImageView;
//        Glide.with(context).load(usersList.get(position).getUser_image()).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//
//                holder.progressBar.setVisibility(View.INVISIBLE);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.INVISIBLE);
//                return false;
//            }
//        }).into(circleImageView);

        final String user_id = usersList.get(position).User_id;
//        final String user_name = usersList.get(position).getUsername();
        final String user_name = usersList.get(position).getUsername();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               Intent intent = new Intent(context, SendActivity.class);
//               intent.putExtra("user_id",user_id);
//               intent.putExtra("user_name",user_name);
//               context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "iconImageViewOnClick at position4 " + usersList.size());
        return usersList.size();
    }

    public void setUsersList(List<Users> usersList) {

        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        //        private CircleImageView circleImageView;
        private TextView name;
        private TextView id;

        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
//            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            id = (TextView) view.findViewById(R.id.item_number);
            name = (TextView) view.findViewById(R.id.content);
            progressBar = (ProgressBar) view.findViewById(androidx.appcompat.R.id.progress_circular);
        }
    }
}
