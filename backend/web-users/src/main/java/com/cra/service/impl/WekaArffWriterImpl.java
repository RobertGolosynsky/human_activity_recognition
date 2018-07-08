package com.cra.service.impl;

import com.cra.domain.entity.GyroData;
import com.cra.domain.entity.RecordType;
import com.cra.domain.entity.Recording;
import com.cra.service.interfaces.ModellingService;
import com.cra.service.interfaces.WekaArffWriter;
import com.cra.util.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WekaArffWriterImpl implements WekaArffWriter {
    @Value("${weka.relation}")
    private String relationTemplate;
    @Value("${weka.attribute}")
    private String numericAttributeTemplate;
    @Value("${weka.class}")
    private String classesTemplate;
    @Value("${weka.data}")
    private String dataHeader;

    private final ModellingService modellingService;

    @Override
    public void writeRawArff(List<Recording> recordings, OutputStream os) {
        Set<String> classes = recordings.stream()
                .map(Recording::getType)
                .map(String::valueOf)
                .collect(Collectors.toSet());
        List<String> numericAttributes = Arrays.asList("x","y","z","time");

        StringBuilder sb = new StringBuilder();

        sb.append(String.format(relationTemplate, "default")).append("\n");
        numericAttributes.forEach(attr -> sb.append(String.format(numericAttributeTemplate, attr)).append("\n"));
        sb.append(String.format(classesTemplate, String.join(",", classes))).append("\n");

        sb.append(dataHeader).append("\n");

        recordings.forEach(rec ->
                rec.getData().forEach(row -> {
                    List<String> stringRow = Stream.of(row.getX(), row.getY(), row.getZ(), row.getTime(), rec.getType())
                            .map(String::valueOf)
                            .collect(Collectors.toList());
                    sb.append(String.join(",", stringRow)).append("\n");
                })
        );
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        try {
            bw.append(sb);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void writeArffWithFeatures(List<Recording> recordings, ModelConfig modelConfig, OutputStream os) {
        Instances instances = modellingService.createDataset(modelConfig, recordings);
        ArffSaver saver = new ArffSaver();

        try {
            saver.setInstances(instances);
            saver.setDestination(os);
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
