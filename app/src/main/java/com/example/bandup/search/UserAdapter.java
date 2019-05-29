package com.example.bandup.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bandup.R;
import com.example.bandup.userprofile.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<UserModel> mUsers;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<UserModel> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final UserModel user = mUsers.get(i);

        viewHolder.btnFollow.setVisibility(View.VISIBLE);

        viewHolder.username.setText(user.getUserName());
        String userFullName = user.getFirstName() + " " + user.getLastName();
        viewHolder.fullname.setText(userFullName);
        Picasso.get().load(user.getImageUrl()).into(viewHolder.imageProfile);
        isFollowing(user.getUid(), viewHolder.btnFollow);

        if(user.getUid().equals(firebaseUser.getUid())){
            viewHolder.btnFollow.setVisibility(View.GONE);
        }

        /*      ESTO SERIA PARA REDIRECCIONAR AL PERFIL DEL USUARIO
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.btnFollow.getText().toString().equals("Seguir")){
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getUid())
                            .setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getUid()).child("followers").child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getUid()).child("followers").child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView imageProfile;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.item_username);
            fullname = itemView.findViewById(R.id.item_fullname);
            imageProfile = itemView.findViewById(R.id.item_image_profile);
            btnFollow = itemView.findViewById(R.id.item_btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists()){
                    button.setText("Siguiendo");
                } else{
                    button.setText("Seguir");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
