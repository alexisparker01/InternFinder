package com.example.internfinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.activities.MainActivity;
import com.example.internfinder.activities.OpenPostActivity;
import com.example.internfinder.activities.ProfileActivity;
import com.example.internfinder.models.Post;
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
        private TextView firstNamePost;
        private TextView lastNamePost;
        private TextView postType;
        private TextView tvUser2;
        private TextView tvPostLocation;
        private TextView tvLocationName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsernamePD);
            tvDescription = itemView.findViewById(R.id.tvDescriptionPD);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAtPD);
            ivProfilePic = itemView.findViewById(R.id.profilePic);
            ivImagePostFeed = itemView.findViewById(R.id.ivPostImageFeed);
            firstNamePost = itemView.findViewById(R.id.firstNamePost);
            lastNamePost = itemView.findViewById(R.id.lastNamePost);
            postType = itemView.findViewById(R.id.postType);
            tvUser2 = itemView.findViewById(R.id.tvUser2);
            tvPostLocation = itemView.findViewById(R.id.tvPostLocation);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);


        }

        public void bind(Post post) {
            // Bind the post data to the view elements


            if (post.getType().equals("event")) {
                tvPostLocation.setVisibility(View.VISIBLE);
                tvLocationName.setVisibility(View.VISIBLE);
                ivImagePostFeed.setVisibility(View.VISIBLE);
                tvPostLocation.setText(post.getLocation());
                tvLocationName.setText(post.getLocationName());
                tvUser2.setVisibility(View.GONE);
            } else if (post.getType().equals("photo")) {
                tvPostLocation.setVisibility(View.GONE);
                tvLocationName.setVisibility(View.GONE);
                ivImagePostFeed.setVisibility(View.VISIBLE);
                tvUser2.setVisibility(View.GONE);
            } else {
                tvUser2.setVisibility(View.VISIBLE);
                tvPostLocation.setVisibility(View.GONE);
                tvLocationName.setVisibility(View.GONE);
            }

            tvDescription.setText(post.getDescription());
            tvUsername.setText("@" + post.getUser().getUsername());

            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvCreatedAt.setText(timeAgo);

            if (post.getUser().getUsername().equals(ParseUser.getCurrentUser().getString("username"))) {
                firstNamePost.setText("You");
                lastNamePost.setVisibility(View.GONE);
            } else {
                firstNamePost.setText(post.getUser().getString("firstname"));
                lastNamePost.setText(post.getUser().getString("lastname"));
            }

            postType.setText("shared a " + post.getType() + " post");

            if (post.getType().equals("photo")) {
                tvUser2.setText(post.getUser().getUsername());
            } else {
                tvUser2.setText("");
            }


            ParseFile profilePic = post.getUser().getParseFile("profilePic");
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

                    if (tvUsername.getText().equals("@" + ParseUser.getCurrentUser().getUsername())) {

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("openProfileFragment", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                    } else {
                        Intent i = new Intent(context, ProfileActivity.class);
                        i.putExtra("Post", Parcels.wrap(post));
                        i.putExtra("User", Parcels.wrap(post.getUser()));
                        context.startActivity(i);

                    }
                }
            });


            itemView.setOnTouchListener(new View.OnTouchListener() {

                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Intent i = new Intent(context, OpenPostActivity.class);
                        i.putExtra("Post", Parcels.wrap(post));
                        context.startActivity(i);
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }

            });


        }


        public void clear() {
            posts.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Post> list) {
            posts.addAll(list);
            notifyDataSetChanged();
        }
    }
}