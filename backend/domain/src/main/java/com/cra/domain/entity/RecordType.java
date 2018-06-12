package com.cra.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecordType {
    WALK, STAND, SIT;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
