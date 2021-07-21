package com.example.internfinder.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.internfinder.OpenPost;
import com.example.internfinder.Post;
import com.example.internfinder.ProfileActivity;
import com.example.internfinder.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;


    public PostAdapter(Context context, List<Post> thePosts) {
        this.context = context;
        this.posts = thePosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvCreatedAt;
        private ImageView ivProfilePic;
        private ImageView ivImagePostFeed;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsernamePD);
            tvDescription = itemView.findViewById(R.id.tvDescriptionPD);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAtPD);
            ivProfilePic = itemView.findViewById(R.id.profilePic);
            ivImagePostFeed = itemView.findViewById(R.id.ivPostImageFeed);

        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            ivImagePostFeed.setVisibility(View.VISIBLE);

            tvDescription.setText(post.getDescription());
            tvUsername.setText("@" + post.getUser().getUsername());
            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvCreatedAt.setText(timeAgo);



            ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePicture");
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).into(ivProfilePic);
            }

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImagePostFeed);
            } else {
                ivImagePostFeed.setVisibility(View.GONE);
            }

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("PostAdapter", "clicked");

                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("Post", Parcels.wrap(post));
                    i.putExtra("User", Parcels.wrap(post.getUser()));
                    context.startActivity(i);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Intent i = new Intent(context, OpenPost.class);
                        i.putExtra("Post", Parcels.wrap(post));
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