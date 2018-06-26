package org.cra.contextrecognition.network.service;

import org.cra.contextrecognition.network.domain.CRAModelsListResponse;
import org.cra.contextrecognition.network.domain.CRAUploadRecordingResponse;
import org.cra.contextrecognition.network.domain.GyroRecord;
import org.cra.contextrecognition.network.domain.CRAAuthRequest;
import org.cra.contextrecognition.network.domain.CRAAuthResponse;
import org.cra.contextrecognition.network.domain.CRABasicResponse;
import org.cra.contextrecognition.network.domain.Model;
import org.cra.contextrecognition.network.domain.Recording;
import org.cra.contextrecognition.network.domain.RecordingDTO;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CRAWebApi {

    @POST("login")
    Call<CRAAuthResponse> auth(@Body CRAAuthRequest authRequest);

    @POST("recordings/save")
    Call<CRAUploadRecordingResponse> saveRecording(@Body Recording recording);

    @GET("recordings/list")
    Call<List<RecordingDTO>> getRecordings();

    @GET("models/list")
    Call<List<Model>> getModels();

    @POST("recordings/delete/{id}")
    Call<CRABasicResponse> removeRecording(@Path("id") int id);

}