package com.example.bandup.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bandup.R;
import com.example.bandup.match.NewMatchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ImageView newMessage;
    private List<MessageModel> messageList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.message_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), messageList);
        recyclerView.setAdapter(messageAdapter);
//        newMessage = view.findViewById(R.id.new_message);
//        newMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent moveToEditActivity = new Intent(getActivity(), NewMatchActivity.class);
//                moveToEditActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(moveToEditActivity);
//            }
//        });
        readMessages();
        return view;
    }

    private void readMessages() {
        String uid = FirebaseAuth.getInstance().getUid();
        assert uid != null;
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("messages").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = new MessageModel();
                    message.setMessageId(snapshot.getKey());
                    message.setSender(snapshot.child("sender").getValue(String.class));
                    message.setReceiver(snapshot.child("receiver").getValue(String.class));
                    message.setText(snapshot.child("text").getValue(String.class));
                    message.setTimestamp(snapshot.child("timestamp").getValue(String.class));
                    message.setType(snapshot.child("type").getValue(Integer.class));
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
