package com.example.bandup.post;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private static final int PICK_AUDIO = 200;

    ImageView postClose;
    TextView postMusic;
    ImageView musicFile;
    private EditText musicTitle;
    private EditText musicDescription;
    private ProgressDialog progressDialog;
    String title;
    String description;
    private Uri audio;
    private String userId;
    private PostModel post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postClose = findViewById(R.id.post_close);
        postMusic = findViewById(R.id.post_bar_text);
        musicFile = findViewById(R.id.post_music_file);
        musicTitle = findViewById(R.id.post_text_title);
        musicDescription = findViewById(R.id.post_text_description);
        progressDialog = new ProgressDialog(this);
        userId = getUserId();
        post = new PostModel();
        audio = null;
        postClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        postMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPost();
            }
        });
        musicFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioSelection();
            }
        });

        //Pide permisos al usuario
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(PostActivity.this, "No se disponen de los permisos necesarios para realizar esta acción", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_AUDIO);
        }
    }

    private void close() {
        //Si se hace de esta manera hay mejor interaccion entre activities

        //Intent back = new Intent(PostActivity.this, NavigationActivity.class);
        finish();
        //startActivity(back);
    }

    private void audioSelection() {
        Intent audio = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(audio, PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_AUDIO) {
            audio = data.getData();
            // musicFile.setImageResource(R.drawable.ok_icon);
        }
    }

    private void audioPost() {
        title = musicTitle.getText().toString();
        description = musicDescription.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(PostActivity.this, "Porfavor, complete todos los campos antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        if (audio == null) {
            Toast.makeText(PostActivity.this, "Porfavor, seleccione una canción antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        post.setPostFile(audio);
        post.setDescription(description);
        post.setTitle(title);
        post.setPublisher(userId);
        post.setPostId(userId + title.trim());
        savePost();
    }

    //Usado para actualizar el texto del nombre de publicacion
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    private String getFileExtension(Uri url) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(url));
    }

    private void savePost() {
        progressDialog.setTitle("Guardando");
        progressDialog.setMessage("Porfavor espere mientras se guarda su publicación");
        progressDialog.show();
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts").child(post.getPostId());
        final StorageReference mChildStorage = FirebaseStorage.getInstance().getReference().child("posts").child(post.getPostId());
        mChildStorage.putFile(post.getPostFile()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postRef.child("uid").setValue(post.getPublisher());
                        postRef.child("Title").setValue(post.getTitle());
                        postRef.child("Description").setValue(post.getDescription());
                        postRef.child("url").setValue(uri.toString());
                        postRef.child("Extension").setValue(getFileExtension(uri));
                        postRef.child("timestamp").setValue(Long.toString(System.currentTimeMillis()));
                        progressDialog.dismiss();
                        close();
                    }
                });
            }
        });
    }
}
