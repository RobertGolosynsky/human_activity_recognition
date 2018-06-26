package org.cra.contextrecognition.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public enum  State {
    @SerializedName("0")
    WALK(0),
    @SerializedName("1")
    STAND(1),
    @SerializedName("2")
    SIT(2);

    private int code;
    State(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public static State fromCode(int i){
        for (State s: values()){
            if (s.code == i){
                return s;
            }
        }
        return null;
    }


}
