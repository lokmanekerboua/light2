package com.example.light;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.light.databinding.ActivitySingupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class Singup extends AppCompatActivity {
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private ActivitySingupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait...");

//---------------------------------spinner -----------------------------------------------------------------
        String[] gender = {"Male", "female"};
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(aa);
//----------------------------------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               tryconnection();
            }
        });
//------------------------------------------------go to login page------------------------------------------------------------
        binding.textView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void tryconnection(){
        if(InternetCheck.isInternetAvailable(Singup.this)){
            validatedata();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(Singup.this);
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

    String fullname="", email="", password="",gender="";

    private void validatedata(){
       fullname = binding.editTextTextPersonName.getText().toString().trim().toLowerCase();
       email = binding.editTextTextPersonName2.getText().toString().trim().toLowerCase();
       password = binding.editTextTextPassword.getText().toString().trim();
       gender = binding.spinner.getSelectedItem().toString().trim().toLowerCase();

       if(TextUtils.isEmpty(fullname)){
           binding.editTextTextPersonName.setError("name cannot be empty");
           binding.editTextTextPersonName.requestFocus();
       }
       else if(TextUtils.isEmpty(email)){
            binding.editTextTextPersonName2.setError("email cannot be empty");
            binding.editTextTextPersonName2.requestFocus();
       }
       else if(TextUtils.isEmpty(password)){
           binding.editTextTextPassword.setError("password cannot be empty");
           binding.editTextTextPassword.requestFocus();
       }
       else if(password.length()<8){
           binding.editTextTextPassword.setError("Password must be at least 8 characters");
           binding.editTextTextPassword.requestFocus();
       }
       else{
           createuser();
       }
    }

    private void createuser(){
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // progressDialog.dismiss();
                        progressDialog.cancel();
                        Toast.makeText(Singup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo(){
       // progressDialog.setTitle("Saving user info...");

        long timestamp = System.currentTimeMillis();

        String uid = mAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("fullname", fullname);
        hashMap.put("gender", gender);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      //  progressDialog.dismiss();
                        Toast.makeText(Singup.this, "Account created...", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        startActivity(new Intent(Singup.this,DashUser.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(Singup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}