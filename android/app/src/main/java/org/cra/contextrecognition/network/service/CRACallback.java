package org.cra.contextrecognition.network.service;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;

import org.cra.contextrecognition.network.domain.CRAErrorResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CRACallback<T> implements Callback<T> {

    private Activity activity;

    public CRACallback() {
    }

    public CRACallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(call, response);
        } else {
            Gson gson = new Gson();
            CRAErrorResponse message=gson.fromJson(response.errorBody().charStream(),CRAErrorResponse.class);
            onFailure(call, response, message);
        }
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    public abstract void onFailure(Call<T> call, Response<T> response, CRAErrorResponse errorResponse);

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (activity != null) {
            Toast.makeText(activity, "Network error. Check internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
}
