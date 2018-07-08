package com.cra.service.interfaces;

import com.cra.domain.entity.Model;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.util.ModelConfig;
import weka.core.Instances;

import java.util.List;

public interface ModellingService {

    Instances createDataset(ModelConfig modelConfig, List<Recording> recordingList);

    Model trainModel(ModelConfig modelConfig, List<Recording> recordingList, User user);
}
