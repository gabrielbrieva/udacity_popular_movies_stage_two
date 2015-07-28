package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.tuxan.udacity.popularmovies.model.SortValuePair;

import java.util.ArrayList;
import java.util.List;

public class SpinnerSortAdapter extends ArrayAdapter<SortValuePair> {

    public SpinnerSortAdapter(Context context, int layout) {
        super(context, layout);

        List<SortValuePair> items = new ArrayList<>();
        items.add(new SortValuePair(context.getString(R.string.pref_sort_value_popularity), context.getString(R.string.pref_sort_label_popularity)));
        items.add(new SortValuePair(context.getString(R.string.pref_sort_value_rate), context.getString(R.string.pref_sort_label_rate)));

        this.addAll(items);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        SortValuePair item = getItem(position);

        /*if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.spinner_item_dropdown, parent, false);
        }*/

        TextView textView = (TextView) view.findViewById(R.id.tv_spinner_item_dropdown_text);
        textView.setText(item.getValue());

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        SortValuePair item = getItem(position);

        /*if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.sort_spinner, parent, false);
        }*/

        TextView textView = (TextView) view.findViewById(R.id.tv_spinner_item_dropdown_text);
        textView.setText(item.getValue());

        return view;
    }
}
