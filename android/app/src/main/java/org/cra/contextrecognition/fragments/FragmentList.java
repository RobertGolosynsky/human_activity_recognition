package org.cra.contextrecognition.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.services.FileService;
import org.cra.contextrecognition.services.ReadingsSaverService;

import java.util.List;

public class FragmentList extends SightFragment {

    private ListView listView;
    private BaseAdapter adapter;
    private List<String> items;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        items = new ReadingsSaverService().listReadings(getContext());
        listView = view.findViewById(R.id.list_view);


        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return items.size();
            }

            @Override
            public Object getItem(int position) {
                return items.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View row;
                row = inflater.inflate(R.layout.list_view_item, parent, false);
                TextView type, date, length;
                type = row.findViewById(R.id.type_text);
                date = row.findViewById(R.id.date_text);
                length = row.findViewById(R.id.length_text);

                String[] split = items.get(position).split("\\.");

                type.setText(getString(R.string.list_item_state, split[1]));
                length.setText(getString(R.string.list_item_length, split[2]));
                date.setText(getString(R.string.list_item_date, split[3]));
                return row;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    protected void onUserVisibleChanged(boolean visible) {
        super.onUserVisibleChanged(visible);
        if (visible){
            items = new ReadingsSaverService().listReadings(getContext());
            adapter.notifyDataSetChanged();
        }
    }
}
