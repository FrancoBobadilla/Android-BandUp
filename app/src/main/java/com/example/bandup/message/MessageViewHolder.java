package com.example.bandup.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bandup.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public ImageView messageUserProfileImage;
    public TextView messageUsername;
    public TextView messageText;
    public TextView messageAge;
    public ImageView messageAcceptPostulant;
    public ImageView messageCancelPostulant;
    public MessageAcceptPostulantFragment messageAcceptPostulantFragment;
    public MessageCancelPostulantFragment messageCancelPostulantFragment;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        messageUserProfileImage = itemView.findViewById(R.id.messageUserProfileImage);
        messageUsername = itemView.findViewById(R.id.messageUsername);
        messageText = itemView.findViewById(R.id.messageText);
        messageAge = itemView.findViewById(R.id.messageAge);
        messageAcceptPostulant = itemView.findViewById(R.id.messageAcceptPostulant);
        messageCancelPostulant = itemView.findViewById(R.id.messageCancelPostulant);

        messageAcceptPostulantFragment = new MessageAcceptPostulantFragment();
        messageCancelPostulantFragment = new MessageCancelPostulantFragment();

    }
}
