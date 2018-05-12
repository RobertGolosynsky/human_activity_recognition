package org.cra.contextrecognition.model;

import android.support.annotation.Nullable;

public enum  State {
    WALK(0), STAND(1), SIT(2);

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
