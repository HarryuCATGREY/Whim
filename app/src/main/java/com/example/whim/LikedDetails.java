package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LikedDetails extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    Button postLocationText;
    TextView postTextDateTime;
    ImageView postImage, dislikenote, backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_details);
        getSupportActionBar().hide();

//        initialise button and view
//        title and content for liked post
        TextView likeTitle = findViewById(R.id.liketitle);
        TextView likecontent = findViewById(R.id.likecontent);

//        details for post
        postTextDateTime = findViewById(R.id.postDateTimelike);
        postImage = findViewById(R.id.likeimage);
        postLocationText = findViewById(R.id.locationlike);

//        button for dislike, like, back
        dislikenote = findViewById(R.id.dislike);
        backbutton = findViewById(R.id.backlike);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent data = getIntent();

        String titlepost = data.getStringExtra("title");
        String contentpost = data.getStringExtra("content");
        String locationpost = data.getStringExtra("location");
        String timepost = data.getStringExtra("time");
        String imgpost = data.getStringExtra("image");
        String imageNamepost = data.getStringExtra("imagename");
        String postID = data.getStringExtra("postId");
        String uid = data.getStringExtra("uid");

        ArrayList<String> curlikedusers = data.getStringArrayListExtra("likedusers");
        final Integer[] numlikes = {data.getExtras().getInt("numlikes")};


        likeTitle.setText(data.getStringExtra("title"));
        likecontent.setText(data.getStringExtra("content"));
        postTextDateTime.setText(data.getStringExtra("time"));
        postLocationText.setText(data.getStringExtra("location"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatterTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a");


        if (data.getStringExtra("image") != null) {
            StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(postImage);
                }
            });
        }

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LikedDetails.this, WhatYouLikedActivity.class));
            }
        });

        dislikenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curlikedusers.remove(firebaseUser.getUid());
                numlikes[0] -= 1;
                DocumentReference likedpostRef = firebaseFirestore.collection("posts").document(postID);
                Map<String, Object> post = new HashMap<>();
                try {
                    Date realStamp = formatterTime.parse(timepost);
                    post.put("timestamp", realStamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                post.put("uid", firebaseUser.getUid());
                post.put("title", titlepost);
                post.put("content", contentpost);
                post.put("image", imgpost);
                post.put("time", timepost);
                post.put("location", locationpost);
                post.put("imagename", imageNamepost);
                post.put("numlikes", numlikes[0]);
                post.put("likedusers", curlikedusers);

                likedpostRef.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "The whim is removed from your Like list :)", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to remove whim, please try again later :(", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(LikedDetails.this, WhatYouLikedActivity.class));
                ;
            }

        });

    }

    public void onBackPressed() {
        startActivity(new Intent(LikedDetails.this, WhatYouLikedActivity.class));
    }

}