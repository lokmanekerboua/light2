package com.example.light.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.light.Pdf_listAdmin;
import com.example.light.databinding.RowCatBinding;
import com.example.light.models.modelcat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class adaptercategories extends RecyclerView.Adapter<adaptercategories.holderCategories> {
    private Context context;
    public ArrayList<modelcat> modelcatArrayList;

    private RowCatBinding binding;

    public adaptercategories(Context context, ArrayList<modelcat> modelcatArrayList) {
        this.context = context;
        this.modelcatArrayList = modelcatArrayList;
        this.binding = binding;
    }

    @NonNull
    @Override
    public holderCategories onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCatBinding.inflate(LayoutInflater.from(context),parent,false);
        return new holderCategories(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderCategories holder, int position) {

        modelcat model = modelcatArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        holder.cattitle.setText(category);

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+category, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                deletecategory(model,holder);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Pdf_listAdmin.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);
            }
        });

    }

    private void deletecategory(modelcat model , holderCategories holder) {
        String id = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Successfully Delete...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelcatArrayList.size();
    }

    class holderCategories extends RecyclerView.ViewHolder{
        TextView cattitle;
        ImageButton deletebtn;
        public holderCategories(@NonNull View itemView) {
            super(itemView);
            cattitle = binding.cattitle;
            deletebtn = binding.deletebtn;
        }
    }
}
