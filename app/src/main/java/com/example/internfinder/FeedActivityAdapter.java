package com.example.internfinder;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.internfinder.Post;
import com.example.internfinder.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class FeedActivityAdapter extends RecyclerView.Adapter<FeedActivityAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;


    public FeedActivityAdapter(Context context, List<Post> thePosts) {
        this.context = context;
        this.posts = thePosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_feed, parent, false);
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsernamePD);
            tvDescription = itemView.findViewById(R.id.tvDescriptionPD);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAtPD);
            ivProfilePic = itemView.findViewById(R.id.profilePic);

        }

        public void bind(Post post) {
            // Bind the post data to the view elements

            tvDescription.setText(post.getDescription());
            tvUsername.setText("@" + post.getUser().getUsername());
            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvCreatedAt.setText(timeAgo);


            ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePicture");
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).into(ivProfilePic);


            }
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("Post", Parcels.wrap(post));
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