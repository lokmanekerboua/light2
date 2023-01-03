package com.example.light;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tryconnection();
    }

    private void tryconnection(){
        if(InternetCheck.isInternetAvailable(MainActivity.this)){
            firebaseAuth = FirebaseAuth.getInstance();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkuser();
                }
            },2000);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("SingOut")
                    .setMessage("No INTERNET connection")
                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tryconnection();
                        }
                    })
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        }
    }

    public void checkuser(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

       if(firebaseUser == null){
           startActivity(new Intent(MainActivity.this , Login.class));
           finish();
       }
       else{
           DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
           ref.child(firebaseUser.getUid())
                   .addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           String userType = ""+snapshot.child("userType").getValue();

                           if(userType.equals("admin")){
                               startActivity(new Intent(MainActivity.this,DashAdmin.class));
                               finish();
                           }
                           else {
                               startActivity(new Intent(MainActivity.this,DashUser.class));
                               finish();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
       }
    }
}