package com.example.bandup.home;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.R;
import com.example.bandup.message.MessageViewHolder;
import com.example.bandup.post.PostModel;
import com.example.bandup.post.PostViewHolder;
import com.example.bandup.userprofile.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HomeAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private Context mContext;
    private List<PostModel> mPost;
    private MediaPlayer mediaPlayer;
    private int activeAudio;


    public HomeAdapter(Context mContext, List<PostModel> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.mediaPlayer = new MediaPlayer();
        this.activeAudio = -1;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder viewHolder, int i) {
        final PostModel post = mPost.get(i);
        final int newActiveAudio = i;
        Toast.makeText(mContext, post.getTimestamp(), Toast.LENGTH_SHORT).show();
        if (post.getDescription().equals("")) {
            viewHolder.postTextDescription.setVisibility(View.GONE);
        } else {
            setTime(viewHolder, post.getTimestamp());
            viewHolder.deletePostDialogFragment.setPostId(post.getPostId());
            viewHolder.postTextDescription.setVisibility(View.VISIBLE);
            viewHolder.postTextDescription.setText(post.getDescription());
            viewHolder.postTextTitle.setText(post.getTitle());
            viewHolder.postPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.reset();
                    if (activeAudio == newActiveAudio) {
                        viewHolder.postPlayButton.setImageResource(R.drawable.ic_play);
                        activeAudio = -1;
                    } else {
                        viewHolder.postPlayButton.setImageResource(R.drawable.ic_pause);
                        activeAudio = newActiveAudio;
                        playFile(post.getPostFile().toString());
                    }
                }
            });
            viewHolder.postUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserID", post.getPublisher());

                    ProfileFragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            });
            viewHolder.postDelete.setVisibility(View.GONE);
        }
        publisherInfo(viewHolder.postUserProfileImage, viewHolder.postTextUsername, post.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    private void publisherInfo(final ImageView postUserProfileImage, final TextView postTextUsername, String userid) {
        FirebaseDatabase.getInstance().getReference("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(Objects.requireNonNull(dataSnapshot.child("imageUrl").getValue()).toString()).into(postUserProfileImage);
                postTextUsername.setText(Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void playFile(String dataSource) {
        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setTime(final PostViewHolder viewHolder, final String time) {
        long ts = Long.parseLong(time);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts);
        long diff = TimeUnit.MINUTES.convert(System.currentTimeMillis() - ts, TimeUnit.MILLISECONDS);
        if (diff == 0) {
            viewHolder.postTime.setText("Ahora");
            return;
        }
        if (diff < 1440) {
            viewHolder.postTime.setText(DateFormat.format("hh:mm", cal).toString());
            return;
        }
        if (diff < 2880) {
            viewHolder.postTime.setText("Ayer");
            return;
        }
        viewHolder.postTime.setText(DateFormat.format("dd/MM/yyyy", cal).toString());
    }
}
