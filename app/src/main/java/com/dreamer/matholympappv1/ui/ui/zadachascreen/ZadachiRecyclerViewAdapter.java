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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "TAG";
    //    private List<Users> usersList;
    NavController navController;
    MainActivity mainActivity;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    private List<Zadachi> zadachiList;
    private List listFilesFirestore;
    private List listSolutionFilesFirestore;
    private Context context;
    private String spotOfSearchImages;


    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
        listFilesFirestore = new ArrayList<>();
        listSolutionFilesFirestore = new ArrayList<>();
        this.zadachiList = zadachiList;
        this.context = context;
        spotOfSearchImages = "answersimages";
        listAllFilesDirestore(spotOfSearchImages);
        spotOfSearchImages = "solutionimages";
        listAllFilesDirestore(spotOfSearchImages);
        intNavcontroller();
    }

    private void listAllFilesDirestore(String spotOfSearchImages) {
        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child("answersimages");
        StorageReference listRef = storageReference.getReference().child(this.spotOfSearchImages);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.

                            Log.d(TAG, "____DATEprefix= " + prefix);
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.e(TAG, "____DATEitem= " + item);
                            if (Objects.equals(spotOfSearchImages, "answersimages")) {

                                listFilesFirestore.add(item);
                                Log.e(TAG, "____DATEitem= " + listFilesFirestore);
                                int listSize = listFilesFirestore.size();
                                Log.e(TAG, "____DATEitem2= " + listSize);
                            } else {
                                listSolutionFilesFirestore.add(item);
                                Log.e(TAG, "____DATEitem= " + listSolutionFilesFirestore);
                                int listSize = listSolutionFilesFirestore.size();
                                Log.e(TAG, "____DATEitem2= " + listSize);
                            }


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
        if (listFilesFirestore.size() != 0) {
            Object stringFileName = listFilesFirestore.get(1);
            Log.e(TAG, "____DATEitem1= " + stringFileName);
        }

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
        Log.e(TAG, "iconImageViewOnClick at position10 " + zadachiList.size());

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
                Log.e(TAG, "_Dateitem=10 " + listFilesFirestore.size());
                bundle.putString("MyArgZadacha_id", user_id);
                bundle.putStringArrayList("MyArgZadacha_listFilesFirestore", (ArrayList<String>) listFilesFirestore);
                bundle.putStringArrayList("MyArgZadacha_listSolutionFilesFirestore", (ArrayList<String>) listSolutionFilesFirestore);
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
