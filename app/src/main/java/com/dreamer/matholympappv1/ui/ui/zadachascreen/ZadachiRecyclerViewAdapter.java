package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.MainActivity;
import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;

import java.util.List;


public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "TAG";
    //    private List<Users> usersList;
    NavController navController;
    MainActivity mainActivity;
    private List<Zadachi> zadachiList;
    private Context context;


    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
        this.zadachiList = zadachiList;
        this.context = context;
        intNavcontroller();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(zadachiList.get(position).getZadachi_list_name());

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

        final String user_id = zadachiList.get(position).Zadachi_id;
//        final String user_name = usersList.get(position).getUsername();
//        final String user_name = zadachiList.get(position).getZadachi_list_name();
        final String zadacha_main_body = zadachiList.get(position).getZadachi_main_body();
        final String zadacha_answer = zadachiList.get(position).getZadachi_Answer();
        final String zadacha_hint = zadachiList.get(position).getZadachi_Hint();
        final String zadacha_solution = zadachiList.get(position).getZadachi_solution();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("MyArgZadacha_main_body", zadacha_main_body);
                bundle.putString("MyArgZadacha_answer", zadacha_answer);
                bundle.putString("MyArgZadacha_hint", zadacha_hint);
                bundle.putString("MyArgZadacha_solution", zadacha_solution);

                @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
                assert MainActivity != null;
                navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
                navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2, bundle);

//               Intent intent = new Intent(context, SendActivity.class);
//               intent.putExtra("user_id",user_id);
//               intent.putExtra("user_name",user_name);
//               context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "iconImageViewOnClick at position8 " + zadachiList.size());
        return zadachiList.size();
    }

    private void intNavcontroller() {
        @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
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
