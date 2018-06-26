package org.cra.contextrecognition.network.service;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;

import org.cra.contextrecognition.network.domain.CRAErrorResponse;

import java.io.IOException;

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
        System.out.println("CRACallback.onResponse");
        if (response.isSuccessful()) {
            onSuccess(call, response);
            finaly();
        } else {
            Gson gson = new Gson();
            CRAErrorResponse message=gson.fromJson(response.errorBody().charStream(),CRAErrorResponse.class);
            onFailure(call, response, message);
            finaly();
        }
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    public void onFailure(Call<T> call, Response<T> response, CRAErrorResponse errorResponse){
        System.out.println("CRACallback.onFailure");
        System.out.println(response.raw());

        if (activity != null) {
            Toast.makeText(activity, "Code: "+response.code()+", text: "+ errorResponse.getText()+".", Toast.LENGTH_SHORT).show();
        }
        System.out.println("Activity: "+activity);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        System.out.println("CRACallback.onFailure");
        if (activity != null) {
            Toast.makeText(activity, "Network or conversion error.", Toast.LENGTH_SHORT).show();
        }
        System.out.println("Activity: "+activity);
        finaly();
    }

    public void finaly(){

    }
}
