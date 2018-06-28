package com.cra.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModelConfig {
    private List<Long> recordingIds;
    private int windowSize;
    private int offset;
}