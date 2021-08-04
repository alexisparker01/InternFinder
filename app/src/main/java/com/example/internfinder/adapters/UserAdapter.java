package com.example.internfinder.adapters;

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
import com.example.internfinder.R;
import com.example.internfinder.activities.MainActivity;
import com.example.internfinder.activities.ProfileActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<ParseUser> users;


    public UserAdapter(Context context, List<ParseUser> usersList) {
        this.context = context;
        this.users = usersList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePictureGrid;
        private TextView tvUsernameGrid;
        private TextView tvFirstnameGrid;
        private TextView tvLastnameGrid;
        private TextView tvIndustryGrid;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePictureGrid = itemView.findViewById(R.id.ivProfilePictureGrid);
            tvUsernameGrid = itemView.findViewById(R.id.tvUsernameGrid);
            tvFirstnameGrid = itemView.findViewById(R.id.tvFirstNameGrid);
            tvLastnameGrid = itemView.findViewById(R.id.tvLastNameGrid);
            tvIndustryGrid = itemView.findViewById(R.id.tvIndustryGrid);


        }

        public void bind(ParseUser user) {


            try {
                tvUsernameGrid.setText("@" + user.fetchIfNeeded().getString("username"));

                tvFirstnameGrid.setText(user.fetchIfNeeded().getString("firstname"));

                tvLastnameGrid.setText(user.fetchIfNeeded().getString("lastname"));

                tvIndustryGrid.setText(user.fetchIfNeeded().getString("industry"));

                ParseFile profilePic = user.fetchIfNeeded().getParseFile("profilePic");
                if (profilePic != null) {
                    Glide.with(context).load(profilePic.getUrl()).into(ivProfilePictureGrid);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.i("USERADAPTER", "tvUsername " + tvUsernameGrid.getText() + " parse usr : @" + ParseUser.getCurrentUser().getUsername());
                    if (tvUsernameGrid.getText().toString().equals("@" + ParseUser.getCurrentUser().getUsername())) {

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("openProfileFragment", true);
                        //overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        //finish();
                        context.startActivity(intent);
                    } else {
                        Intent i = new Intent(context, ProfileActivity.class);
                        i.putExtra("User", Parcels.wrap(user));
                        context.startActivity(i);

                    }
                }

            });
        }
    }


    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseUser> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }


}