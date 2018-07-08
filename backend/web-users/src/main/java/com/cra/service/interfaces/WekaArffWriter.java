package com.cra.service.interfaces;

import com.cra.domain.entity.Recording;
import com.cra.util.ModelConfig;

import java.io.OutputStream;
import java.util.List;

public interface WekaArffWriter{

    void writeRawArff(List<Recording> recordings, OutputStream os);

    void writeArffWithFeatures(List<Recording> recordings, ModelConfig modelConfig, OutputStream os);
}
