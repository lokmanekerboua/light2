package com.example.light.filters;

import android.widget.Filter;

import com.example.light.adapters.adaptercategories;
import com.example.light.models.modelcat;

import java.util.ArrayList;

public class Filtercategory extends Filter {

    ArrayList<modelcat> filterlist;
    adaptercategories adaptercate;

    public Filtercategory(ArrayList<modelcat> filterlist, adaptercategories adaptercate) {
        this.filterlist = filterlist;
        this.adaptercate = adaptercate;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<modelcat> filtermodels = new ArrayList<>();

            for (int i = 0; i < filterlist.size(); i++) {
                if (filterlist.get(i).getCategory().toUpperCase().contains(constraint)) {
                    filtermodels.add(filterlist.get(i));
                }
            }
            results.count = filtermodels.size();
            results.values = filtermodels;
        } else {
            results.count = filterlist.size();
            results.values = filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adaptercate.modelcatArrayList = (ArrayList<modelcat>) results.values;
        adaptercate.notifyDataSetChanged();
    }
}
