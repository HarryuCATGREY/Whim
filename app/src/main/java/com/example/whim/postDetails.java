package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class postDetails extends AppCompatActivity {

    private TextView postTitle, postcontent, numberlikes;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    String postImgUri, postImgName;
    Button postLocationText;
    TextView postTextDateTime;
    ImageView postImage, likenote;
    int numuserliked = 0;
    ArrayList<String> likedUserList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        getSupportActionBar().hide();

        postTitle = findViewById(R.id.posttitle);
        postcontent = findViewById(R.id.postexist);

        postTextDateTime = findViewById(R.id.postDateTime);
        postImage = findViewById(R.id.postimage);
        postLocationText = findViewById(R.id.locationpost);
        likenote = findViewById(R.id.likenote);
        numberlikes = findViewById(R.id.numberlikes);

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
        String uID = data.getStringExtra("uid");

        // String numlikes = data.getStringExtra("numlikes");

        //int numberlikes = Integer.parseInt(numlikes);

        postTitle.setText(data.getStringExtra("title"));
        postcontent.setText(data.getStringExtra("content"));
        postTextDateTime.setText(data.getStringExtra("time"));
        postLocationText.setText(data.getStringExtra("location"));
        SimpleDateFormat formatterTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a");


        if (data.getStringExtra("image") != null) {
            StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(postImage);
                }
            });
        }

        DocumentReference likeRef = firebaseFirestore.collection("posts").document(postID);

        likeRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        for (String user : (ArrayList<String>) document.get("likedusers")){
                            likedUserList.add(user);
                        }
                           // Log.v("vettore", String.valueOf(likedUserList));
//                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


        likeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                numuserliked = document.getLong("numlikes").intValue();
//                Log.v("Number of liked users", String.valueOf(numuserliked));
                numberlikes.setText(String.valueOf(numuserliked));
//                Log.v("in textview", numberlikes.getText().toString());


            }

        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });;

        //numberlikes.setText(String.valueOf(numuserliked));


        ImageView backpost = findViewById(R.id.backpost);
        backpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference likeRef = firebaseFirestore.collection("posts").document(postID);
                //DocumentReference documentReference = firebaseFirestore.collection("posts").document();

                Map<String, Object> post = new HashMap<>();
                try {
                    Date realStamp = formatterTime.parse(timepost);
                    post.put("timestamp", realStamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //ArrayList<String> likedusers = new ArrayList<String>();
                post.put("uid", uID);
                post.put("title", titlepost);
                post.put("content", contentpost);
                post.put("image", imgpost);
                post.put("time", timepost);
                post.put("location", locationpost);
                post.put("imagename", imageNamepost);
                post.put("numlikes", numuserliked);
                post.put("likedusers", likedUserList);

                likeRef.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        Toast.makeText(getApplicationContext(), "You liked this whim :)", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(postDetails.this, postDetails.this));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Failed to like whim, please try again later :(", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(postDetails.this, PostActivity.class));
            }
        });


//        deletepost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DocumentReference documentReference = firebaseFirestore.collection("posts").document(data.getStringExtra("postId"));
//                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(view.getContext(), "Your post is deleted.", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(postDetails.this, PostActivity.class));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(view.getContext(), "Your post failed to be deleted.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

        likenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(likedUserList.contains(firebaseUser.getUid())){
                    likedUserList.remove(firebaseUser.getUid());
                    numuserliked -= 1;
                    numberlikes.setText(String.valueOf(numuserliked));

//                    Log.v("current liked users", String.valueOf(likedUserList));
//                    Log.v("current likes", String.valueOf(numuserliked));
                    Toast.makeText(getApplicationContext(), "You removed your like to this whim :)", Toast.LENGTH_SHORT).show();

                }else{
                    likedUserList.add(firebaseUser.getUid());
                    numuserliked += 1;
                    numberlikes.setText(String.valueOf(numuserliked));
                    //numberlikes.setText(String.valueOf(numuserliked));
//                    Log.v("current liked users", String.valueOf(likedUserList));
//                    Log.v("current likes", String.valueOf(numuserliked));
                    Toast.makeText(getApplicationContext(), "You liked this whim :)", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void onBackPressed() {

    }
}
