package org.cra.contextrecognition.network.service;

import org.cra.contextrecognition.network.domain.GyroRecord;
import org.cra.contextrecognition.network.domain.CRAAuthRequest;
import org.cra.contextrecognition.network.domain.CRAAuthResponse;
import org.cra.contextrecognition.network.domain.CRABasicResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CRAWebApi {
    @POST("auth")
    Call<CRAAuthResponse> auth(@Body CRAAuthRequest authRequest);
    @POST("saveData")
    Call<CRABasicResponse> saveGyroData(@Body List<GyroRecord> gyroRecord);

}