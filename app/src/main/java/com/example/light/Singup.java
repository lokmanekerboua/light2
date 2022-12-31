package com.example.light;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.ProgressDialog;
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
    Spinner spin;
    TextView login;
    EditText emailfield, passwordfield , fullname;
    Button create;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//---------------------------------spinner -----------------------------------------------------------------
        String[] gender = {"Male", "female"};
        spin = findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
//----------------------------------------------------------------------------------------------------------
        emailfield = findViewById(R.id.editTextTextPersonName2);
        passwordfield = findViewById(R.id.editTextTextPassword);
        fullname = findViewById(R.id.editTextTextPersonName);

        create = findViewById(R.id.button2);

        mAuth = FirebaseAuth.getInstance();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail = emailfield.getText().toString().trim();
                String userpass = passwordfield.getText().toString().trim();

                if (TextUtils.isEmpty(useremail)) {
                    emailfield.setError("Email cannot be empty");
                    emailfield.requestFocus();
                } else if (TextUtils.isEmpty(userpass)) {
                    passwordfield.setError("password cannot be empty");
                    passwordfield.requestFocus();
                } else {
                    mAuth.createUserWithEmailAndPassword(useremail, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "");
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                            } else {
                                Log.w(TAG, "", task.getException());
                                Toast.makeText(Singup.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        login = findViewById(R.id.textView8);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
    }

}