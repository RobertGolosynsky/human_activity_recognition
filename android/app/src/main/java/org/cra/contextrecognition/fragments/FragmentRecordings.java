package org.cra.contextrecognition.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.model.State;
import org.cra.contextrecognition.network.domain.CRABasicResponse;
import org.cra.contextrecognition.network.domain.CRAUploadRecordingResponse;
import org.cra.contextrecognition.network.domain.GyroRecord;
import org.cra.contextrecognition.network.domain.Recording;
import org.cra.contextrecognition.network.domain.RecordingDTO;
import org.cra.contextrecognition.network.service.CRACallback;
import org.cra.contextrecognition.network.service.CRAWebApi;
import org.cra.contextrecognition.network.service.RetrofitService;
import org.cra.contextrecognition.services.DateFormatService;
import org.cra.contextrecognition.services.ReadingsSaverService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FragmentRecordings extends SightFragment implements SwipeRefreshLayout.OnRefreshListener {



    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private BaseAdapter adapter;
    private List<ListRecordingItem> items = Collections.emptyList();
    private List<RecordingDTO> fetchedRecordings = Collections.emptyList();
    private CRAWebApi api = RetrofitService.getInstance().create(CRAWebApi.class);
    private ReadingsSaverService readingsSaverService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recordings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readingsSaverService = new ReadingsSaverService();
        readingsSaverService = new ReadingsSaverService();
        listView = view.findViewById(R.id.list_view);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
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
                row = inflater.inflate(R.layout.recording_list_item, parent, false);
                TextView type, date, length, checkMark;
                type = row.findViewById(R.id.type_text);
                date = row.findViewById(R.id.date_text);
                length = row.findViewById(R.id.length_text);
                checkMark = row.findViewById(R.id.check_mark);
                final ListRecordingItem recording = items.get(position);

                type.setText(getString(R.string.list_item_state, recording.state));
                length.setText(getString(R.string.list_item_length, String.valueOf(recording.length)));
                date.setText(getString(R.string.list_item_date, DateFormatService.format(recording.date)));
                checkMark.setVisibility(recording.id==-1 ? View.INVISIBLE:View.VISIBLE);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presentUploadRemoveDialog(recording);
                    }
                });

                return row;
            }
        };
        listView.setAdapter(adapter);
        onRefresh();
    }

    private void presentUploadRemoveDialog(final ListRecordingItem listRecordingItem) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        // set title
        alertDialogBuilder.setTitle("Actions");
        List<String> options = new ArrayList<>();
        if (listRecordingItem.id == -1){ // item is not uploaded
            options.add("Upload");
        }
        options.add("Remove");
        String[] items = new String[options.size()];
        items = options.toArray(items);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                uploadRecording(listRecordingItem);
                                break;
                            case 1:
                                removeRecording(listRecordingItem);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void removeRecording(final ListRecordingItem listRecordingItem) {
        if (listRecordingItem.id==-1) {
            removeRecordingLocally(listRecordingItem);
        }else {
            api.removeRecording(listRecordingItem.id).enqueue(new CRACallback<CRABasicResponse>(getActivity()) {
                @Override
                public void onSuccess(Call<CRABasicResponse> call, Response<CRABasicResponse> response) {
                    removeRecordingLocally(listRecordingItem);
                }
            });
        }
    }
    private void removeRecordingLocally(ListRecordingItem item){
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                items.remove(i);
                readingsSaverService.remove(getActivity(), item.filename);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void uploadRecording(final ListRecordingItem listRecordingItem) {
        List<GyroRecord> recordList = readingsSaverService.getReadings(getActivity(), listRecordingItem.filename);
        final Recording rec = new Recording();
        rec.setType(listRecordingItem.state);
        rec.setData(recordList);
        rec.setDate(listRecordingItem.date);
        api.saveRecording(rec).enqueue(new CRACallback<CRAUploadRecordingResponse>(getActivity()) {
            @Override
            public void onSuccess(Call<CRAUploadRecordingResponse> call, Response<CRAUploadRecordingResponse> response) {
                listRecordingItem.id = response.body().getId();
            }
        });
    }

    private List<ListRecordingItem> getRecordings(){
        List<String> fileNames = new ReadingsSaverService().listReadings(getContext());
        List<ListRecordingItem> listRecordingItems = new ArrayList<>();
        for (String fileName : fileNames) {
            String[] split = fileName.split("\\.");
            try {

                Date date = SimpleDateFormat.getDateTimeInstance().parse(split[3]);
                RecordingDTO dto = findByDate(date);
                listRecordingItems.add(
                        new ListRecordingItem(
                                dto == null ? -1 : dto.getId(),
                                fileName,
                                State.valueOf(split[1]),
                                date,
                                Integer.parseInt(split[2])
                        )
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return listRecordingItems;

    }

    private RecordingDTO findByDate(Date date){
        for (RecordingDTO dto:fetchedRecordings){
            if(dto.getDate().equals(date)){
                return dto;
            }
        }
        return null;
    }

    private void fetchSavedRecordings(){
        api.getRecordings().enqueue(new CRACallback<List<RecordingDTO>>(getActivity()) {
            @Override
            public void onSuccess(Call<List<RecordingDTO>> call, Response<List<RecordingDTO>> response) {
                fetchedRecordings = response.body();
                updateListView();
            }

            @Override
            public void finaly() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateListView(){
        items = getRecordings();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        fetchSavedRecordings();
    }



    @Override
    protected void onUserVisibleChanged(boolean visible) {
        super.onUserVisibleChanged(visible);
        if (visible) {
            updateListView();
        }
    }



    private class ListRecordingItem {
        int id;
        String filename;
        State state;
        Date date;
        int length;

        public ListRecordingItem(int id, String filename, State state, Date date, int length) {
            this.id = id;
            this.filename = filename;
            this.state = state;
            this.date = date;
            this.length = length;
        }
    }


}
