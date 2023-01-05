package com.example.light.adapters;

import static com.example.light.CONSTANTS.MAX_BYTES_PDF;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.light.filters.FilterpdfAdmin;
import com.example.light.application;
import com.example.light.databinding.RowPdfAdminBinding;
import com.example.light.models.modelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import android.content.Context;

public class AdapterPdfadmin extends RecyclerView.Adapter<AdapterPdfadmin.HolderPdfAdmin> implements Filterable {
    private Context context;
    public ArrayList<modelPdf> pdfArraylist, filterlist;

    private RowPdfAdminBinding binding;

    private FilterpdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    public AdapterPdfadmin(Context context, ArrayList<modelPdf> pdfArraylist) {
        this.context = context;
        this.pdfArraylist = pdfArraylist;
        this.filterlist = pdfArraylist;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {

        modelPdf model = pdfArraylist.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        String timestamp = model.getTimestamp();

        String formattedDate = application.formatTimestamp(timestamp);

        holder.Titletv.setText(title);
        holder.descriptionEt.setText(description);
        holder.DateTV.setText(formattedDate);

        loadpdfcategory(model,holder);
        loadpdfFromurl(model,holder);
        loadpdfSize(model,holder);
    }

    private void loadpdfFromurl(modelPdf model, HolderPdfAdmin holder) {
        String pdfUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+ model.getTitle() +" successfully get the file ");


                        holder.pdfview.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG,"onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG,"onPageError: "+t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed getting file from url due to "+ e.getMessage());

                    }
                });
    }

    private void loadpdfSize(modelPdf model, HolderPdfAdmin holder) {
        String pdfUrl = model.getUrl();

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+ model.getTitle() +" "+bytes);
                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb>=1){
                            holder.SizeTV.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if(kb >=1){
                            holder.SizeTV.setText(String.format("%.2f", kb)+" KB");
                        }
                        else{
                            holder.SizeTV.setText(String.format("%.2f", bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadpdfcategory(modelPdf model, HolderPdfAdmin holder) {
        String categoryId = model.getCategoryId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category = ""+snapshot.child("category").getValue();
                        holder.categoryTV.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return pdfArraylist.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterpdfAdmin(filterlist, this);
        }
        return filter;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        PDFView pdfview;
        ProgressBar progressBar;
        TextView Titletv ,descriptionEt, categoryTV, SizeTV,DateTV ;
        ImageButton morebtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            pdfview = binding.pdfview;
            progressBar = binding.progress;
            Titletv = binding.titlepdf;
            descriptionEt = binding.descriptionEt;
            categoryTV = binding.categoryTV;
            SizeTV = binding.SizeTV;
            DateTV = binding.DateTV;
            morebtn = binding.morebtn;

        }
    }
}
