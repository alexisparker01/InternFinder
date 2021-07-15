package com.example.internfinder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;


public class OtherUserProfileAdapter extends RecyclerView.Adapter<OtherUserProfileAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;


    public OtherUserProfileAdapter(Context context, List<Post> allPosts) {
        this.context = context;
        this.posts = allPosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_other_user_profile, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePictureItemOtherUser;
        private TextView tvUsernameItemOtherUser;
        private TextView tvFirstnameItemOtherUser;
        private TextView tvLastnameItemOtherUser;
        private TextView tvDescriptionItemOtherUser;
        private TextView tvCreatedAtItemOtherUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePictureItemOtherUser = itemView.findViewById(R.id.ivProfilePictureItemOtherUser);
            tvUsernameItemOtherUser = itemView.findViewById(R.id.tvUsernameItemOtherUser);
            tvFirstnameItemOtherUser = itemView.findViewById(R.id.tvFirstnameItemOtherUser);
            tvLastnameItemOtherUser = itemView.findViewById(R.id.tvLastnameItemOtherUser);
            tvDescriptionItemOtherUser = itemView.findViewById(R.id.tvDescriptionItemOtherUser);
            tvCreatedAtItemOtherUser = itemView.findViewById(R.id.tvCreatedAtItemOtherUser);

        }

        public void bind(Post post) {

            // Bind the post data to the view elements
            tvDescriptionItemOtherUser.setText(post.getDescription());

            tvUsernameItemOtherUser.setText("@" + post.getUser().getUsername());

            tvFirstnameItemOtherUser.setText(post.getUser().getString("firstname"));

            tvLastnameItemOtherUser.setText(post.getUser().getString("lastname"));

            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvCreatedAtItemOtherUser.setText(timeAgo);


            ParseFile profilePicItem = post.getUser().getParseFile("profilePicture");
            if (profilePicItem != null) {
                Glide.with(context).load(profilePicItem.getUrl()).into(ivProfilePictureItemOtherUser);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, OpenPost.class);
                    //go to postdetails
                    //  i.putExtra("Post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });


        }


    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {

        posts.addAll(list);
        notifyDataSetChanged();
    }






}