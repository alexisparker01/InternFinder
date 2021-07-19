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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;



public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;


    public CommentAdapter(Context context, List<Comment> theComments) {
        this.context = context;
        this.comments = theComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsernameComment;
        private TextView tvTextComment;
        private TextView tvCreatedAtComment;
        private ImageView ivProfilePicComment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsernameComment = itemView.findViewById(R.id.tvUsernameComment);
            tvTextComment = itemView.findViewById(R.id.tvTextComment);
            tvCreatedAtComment = itemView.findViewById(R.id.tvCreatedAtComment);
            ivProfilePicComment = itemView.findViewById(R.id.ivProfilePicComment);

        }

        public void bind(Comment comment) {
            // Bind the comment data to the view elements

            try {
                Log.i("CommentAdapter", comment.getParseUser("user").fetchIfNeeded().getString("username"));
                tvUsernameComment.setText(comment.getUser().getUsername());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvTextComment.setText(comment.getText());
            Date createdAt = comment.getCreatedAt();
            String timeAgo = Comment.calculateTimeAgo(createdAt);
            tvCreatedAtComment.setText(timeAgo);

            try {
            ParseFile profilePic = comment.getUser().getParseFile("profilePicture");
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).into(ivProfilePicComment);

            }
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }
    }



    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

}