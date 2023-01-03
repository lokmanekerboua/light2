package com.example.light;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.light.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private ArrayList<modelcat> categorylist;

    private static final String Tag = "ADD_PAF_TAG";
    private static final int pick_code = 1000;
    private Uri pdfuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        Loadpdfcat();


        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.categoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorypickdialog();
            }
        });

        binding.uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });
    }

    private String title = "", description="", category= "";
    private void validatedata() {
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        category = binding.categoryTV.getText().toString().trim();
        if(TextUtils.isEmpty(title)){
            binding.titleEt.setError("title cannot be empty");
        }
        else if(TextUtils.isEmpty(description)){
            binding.descriptionEt.setError("please add book description");
        }
        else if(TextUtils.isEmpty(category)){
            binding.categoryTV.setError("please pick category");
        }
        else if(pdfuri == null){
            Toast.makeText(this, "please pick pdf file...", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadfile();
        }
    }

    private void uploadfile(){
        Log.d(Tag, "uploadPdfToStorage: uploading to storage");

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "Books/"+timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(Tag, "onSuccess: PDF uploaded to storage...");
                        Log.d(Tag, "onSuccess: getting pdf url");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();

                        uploadpdfinfotodb(uploadedPdfUrl,timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag, "onFailure: PDF upload failed due to"+ e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "PDF upload failure due to "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadpdfinfotodb(String uploadedpdfUrl, long timestamp) {
        Log.d(Tag, "uploadPdfinfoToStorage: uploading pdf info to storage");

        String uid = firebaseAuth.getUid();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("category", ""+category);
        hashMap.put("url", ""+uploadedpdfUrl);
        hashMap.put("timestamp", ""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(Tag, "onSuccess: Successfully uploaded...");
                        binding.titleEt.setText("");
                        binding.descriptionEt.setText("");
                        binding.categoryTV.setText("");
                        Toast.makeText(PdfAddActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag,"OnFailure: faild to uplod to db due to "+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "Failed to upload to db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
     }

    private void Loadpdfcat() {
        Log.d(Tag, "loadPdfCategories: Loading pdf categories");
        categorylist =new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categorylist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    modelcat model = ds.getValue(modelcat.class);

                    categorylist.add(model);
                    Log.d(Tag, "onDataChange"+ model.getCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void categorypickdialog(){
        Log.d(Tag, "categoryPickDialog: showing category pick dialog");
        String[] cattegorylist = new String[categorylist.size()];
        for(int i=0; i<categorylist.size();i++){
            cattegorylist[i]=categorylist.get(i).getCategory();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pick Category").setItems(cattegorylist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String category = cattegorylist[i];
                binding.categoryTV.setText(category);

                Log.d(Tag, "onClick: Selected Category");
            }
        })
                .show();
    }

    private void pdfPickIntent(){
        Log.d(Tag, "pdfPickIntent: start pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"),pick_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == pick_code){
                Log.d(Tag,"onActivityResult: PDF Picked");
                pdfuri = data.getData();
                Log.d(Tag, "onActivityResult: URI:"+pdfuri);
            }
        }
        else{
            Log.d(Tag, "onActivityResult: cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }
}