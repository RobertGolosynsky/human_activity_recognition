package org.cra.contextrecognition.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.cra.contextrecognition.R;
import org.cra.contextrecognition.network.domain.CRAErrorResponse;
import org.cra.contextrecognition.network.domain.CRAModelsListResponse;
import org.cra.contextrecognition.network.domain.Model;
import org.cra.contextrecognition.network.service.CRACallback;
import org.cra.contextrecognition.network.service.CRAWebApi;
import org.cra.contextrecognition.network.service.RetrofitService;
import org.cra.contextrecognition.services.ReadingsSaverService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FragmentModels extends SightFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private BaseAdapter adapter;
    private List<Model> models = Collections.emptyList();
    private CRAWebApi craWebApi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_models, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        craWebApi = RetrofitService.getInstance().create(CRAWebApi.class);
        listView = view.findViewById(R.id.list_view);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return models.size();
            }

            @Override
            public Object getItem(int position) {
                return models.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View row;
                row = inflater.inflate(R.layout.model_list_item, parent, false);
                TextView modelName, modelCreationDate, modelScore;
                modelName = row.findViewById(R.id.model_name_text);
                modelCreationDate = row.findViewById(R.id.date_text);
                modelScore = row.findViewById(R.id.score_text);

                Model model = models.get(position);

                modelName.setText(model.getName());

                modelScore.setText(getString(R.string.model_list_item_score, model.getScore()));
                modelCreationDate.setText(SimpleDateFormat.getDateTimeInstance().format(model.getDate()));
                return row;
            }
        };
        listView.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {

        refreshLayout.setRefreshing(true);
        craWebApi.getModels().enqueue(new CRACallback<List<Model>>(getActivity()) {
            @Override
            public void onSuccess(Call<List<Model>> call, Response<List<Model>> response) {
                models = response.body();
            }

            @Override
            public void finaly() {
                refreshLayout.setRefreshing(false);
            }

        });
    }

    @Override
    protected void onUserFirstSight() {
        super.onUserFirstSight();
        onRefresh();
    }
}
