package com.example.light.filters;


import android.widget.Filter;

import com.example.light.adapters.AdapterPdfadmin;
import com.example.light.models.modelPdf;

import java.util.ArrayList;

public class FilterpdfAdmin extends Filter {

    ArrayList<modelPdf> filterlist;
    AdapterPdfadmin adapterPdfadmin;

    public FilterpdfAdmin(ArrayList<modelPdf> filterlist, AdapterPdfadmin adapterPdfadmin) {
        this.filterlist = filterlist;
        this.adapterPdfadmin = adapterPdfadmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<modelPdf> filtermodels = new ArrayList<>();

            for (int i = 0; i < filterlist.size(); i++) {
                if(filterlist.get(i).getTitle().toUpperCase().contains(constraint)){
                    filtermodels.add(filterlist.get(i));
                }
            }
            results.count = filtermodels.size();
            results.values = filtermodels;
        }
        else{
            results.count = filterlist.size();
            results.values = filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfadmin.pdfArraylist = (ArrayList<modelPdf>)results.values;
        adapterPdfadmin.notifyDataSetChanged();
    }
}
