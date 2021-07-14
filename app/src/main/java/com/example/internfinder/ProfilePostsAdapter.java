package com.example.internfinder;

import android.content.Context;
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

import java.util.List;


public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;


    public ProfilePostsAdapter(Context context, List<Post> allPosts) {
        this.context = context;
        this.posts = allPosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        // Log.i("ProfilePostsAdapter", "reached");
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

        private ImageView ivProfilePictureItem;
        private TextView tvUsernameItem;
        private TextView tvFirstnameItem;
        private TextView tvLastnameItem;
        private TextView tvDescriptionItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePictureItem = itemView.findViewById(R.id.ivProfilePictureItem);
            tvUsernameItem = itemView.findViewById(R.id.tvUsernameItem);
            tvFirstnameItem = itemView.findViewById(R.id.tvFirstnameItem);
            tvLastnameItem = itemView.findViewById(R.id.tvLastnameItem);
            tvDescriptionItem = itemView.findViewById(R.id.tvDescriptionItem);

        }

        public void bind(Post post) {


            // Bind the post data to the view elements
            tvDescriptionItem.setText(post.getDescription());

            tvUsernameItem.setText("@" + post.getUser().getUsername());

            tvFirstnameItem.setText(post.getUser().getString("firstname"));

            tvLastnameItem.setText(post.getUser().getString("lastname"));

            //Date createdAt = post.getCreatedAt();
            //String timeAgo = Post.calculateTimeAgo(createdAt);
            // tvCreatedAt.setText(timeAgo);


            ParseFile profilePicItem = post.getUser().getParseFile("profilePicture");
            if (profilePicItem != null) {
                Glide.with(context).load(profilePicItem.getUrl()).into(ivProfilePictureItem);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Intent i = new Intent(context, PostDetails.class);
                    //go to postdetails
                    //  i.putExtra("Post", Parcels.wrap(post));
                    // context.startActivity(i);
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