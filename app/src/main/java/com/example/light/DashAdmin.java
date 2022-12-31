package com.example.light;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.light.databinding.ActivityCategoryBinding;
import com.example.light.databinding.ActivityDashAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashAdmin extends AppCompatActivity {

    private ActivityDashAdminBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<modelcat> modelcats;
    private adaptercategories adaptercategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();
        loadCategorys();



        binding.addcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashAdmin.this,Category.class));
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashAdmin.this,Login.class));
                finish();
            }
        });
    }

    private void checkuser(){

    }

    private void loadCategorys(){
        modelcats = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelcats.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    modelcat model = ds.getValue(modelcat.class);

                    modelcats.add(model);
                }
                adaptercategories = new adaptercategories(DashAdmin.this,modelcats);
                binding.categoriesrw.setAdapter(adaptercategories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}