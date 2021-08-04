package com.example.internfinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.activities.ProfileActivity;
import com.example.internfinder.models.Comment;
import com.parse.ParseException;
import com.parse.ParseFile;

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

            try {
                tvUsernameComment.setText("@" + comment.getUser().fetchIfNeeded().getUsername());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvTextComment.setText(comment.getText());
            Date createdAt = comment.getCreatedAt();
            String timeAgo = Comment.calculateTimeAgo(createdAt);
            tvCreatedAtComment.setText(timeAgo);

            try {
                ParseFile profilePic = comment.getUser().getParseFile("profilePic");
                if (profilePic != null) {
                    Glide.with(context).load(profilePic.getUrl()).into(ivProfilePicComment);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("User", Parcels.wrap(comment.getUser()));
                    context.startActivity(i);

                }
            });

        }


        public void clear() {
            comments.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Comment> list) {
            comments.addAll(list);
            notifyDataSetChanged();
        }

    }
}