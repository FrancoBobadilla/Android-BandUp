package com.example.bandup.message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.NavigationActivity;
import com.example.bandup.R;
import com.example.bandup.userprofile.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private Context mContext;
    private List<MessageModel> mMessage;

    public MessageAdapter(Context mContext, List<MessageModel> mMessage) {
        this.mContext = mContext;
        this.mMessage = mMessage;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int i) {
        final MessageModel message = mMessage.get(i);
        switch (message.getType()) {
            case 0://app notificación
                setNotificationElement(viewHolder, message);
                break;
            case 1://nuevo postulante
                setNewPostulantElement(viewHolder, message);
                break;
            case 2://chats
                setChatElement(viewHolder, message);
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    private void setNotificationElement(final MessageViewHolder viewHolder, MessageModel message) {
        publisherInfo(viewHolder, message.getSender());
        viewHolder.messageText.setText(message.getText());
        viewHolder.messageAcceptPostulant.setVisibility(View.GONE);
        viewHolder.messageCancelPostulant.setVisibility(View.GONE);
    }

    private void setNewPostulantElement(final MessageViewHolder viewHolder, MessageModel message) {
        publisherInfo(viewHolder, message.getSender());

        viewHolder.messageAcceptPostulantFragment.setMessage(message);
        viewHolder.messageCancelPostulantFragment.setMessage(message);
        viewHolder.messageText.setText("Este músico se postuló para tocar contigo!");
        viewHolder.messageAcceptPostulant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.messageAcceptPostulantFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "aceptar postulante");
            }
        });
        viewHolder.messageCancelPostulant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.messageCancelPostulantFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "cancelar postulante");
            }
        });
    }

    private void setChatElement(final MessageViewHolder viewHolder, MessageModel message) {
        publisherInfo(viewHolder, message.getSender());
    }

    private void publisherInfo(final MessageViewHolder viewHolder, final String userid) {
        if(!userid.equals("bandup")){
            viewHolder.messageUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserID", userid);
                    ProfileFragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            });
        }
        FirebaseDatabase.getInstance().getReference("users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(Objects.requireNonNull(dataSnapshot.child("imageUrl").getValue()).toString()).into(viewHolder.messageUserProfileImage);
                viewHolder.messageUsername.setText(Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
