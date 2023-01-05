package com.example.light;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.light.adapters.AdapterPdfadmin;
import com.example.light.databinding.ActivityPdfListAdminBinding;
import com.example.light.models.modelPdf;
import com.google.api.LogDescriptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Pdf_listAdmin extends AppCompatActivity {
    private ActivityPdfListAdminBinding binding;
    private ArrayList<modelPdf> pdfArraylist;
    private AdapterPdfadmin adapterPdfadmin;
    private String categoryId, categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        binding.subtitle.setText(categoryTitle);
        loadpdflist();

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterPdfadmin.getFilter().filter(s);
                }catch (Exception ee){
                    Log.d(TAG, "onTextChanged: "+ ee.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadpdflist() {
        pdfArraylist = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArraylist.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            modelPdf model = ds.getValue(modelPdf.class);

                            pdfArraylist.add(model);
                            Log.d(TAG, "onDataChange: "+ model.getId()+" "+model.getTitle());
                        }
                        adapterPdfadmin = new AdapterPdfadmin(Pdf_listAdmin.this,pdfArraylist);
                        binding.bookview.setAdapter(adapterPdfadmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}