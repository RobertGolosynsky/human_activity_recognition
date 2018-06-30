package com.cra.service.impl;

import com.cra.domain.entity.*;
import com.cra.domain.entity.Model;
import com.cra.repository.ModelRepository;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.ModellingService;
import com.cra.util.Coordinate;
import com.cra.util.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.sql.Blob;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ModellingServiceImpl implements ModellingService {

    private final ModelRepository modelRepository;
    private final UserRepository userRepository;

    private static List<List<List<GyroData>>> getPartitions(final ModelConfig modelConfig, final List<Recording> recordingList) {
        int windowSize = modelConfig.getWindowSize();
        int receivedOffset = modelConfig.getOffset();
        int offset = receivedOffset == 0 ? 1 : receivedOffset;

        return recordingList.stream()
                .map(p -> {
                    final List<List<GyroData>> partitions = new ArrayList<>();
                    final List<GyroData> data = p.getData();
                    long measureTime = p.getDuration();
                    int windowShiftCount = (int) (measureTime < windowSize ? 0 : (measureTime - windowSize) / offset + 1);

                    for (int i = 0; i < windowShiftCount; i++) {
                        final int tempCount = i;
                        final GyroData gyroDataObj = data
                                .stream()
                                .filter(s -> s.getTime() >= offset * tempCount)
                                .findFirst()
                                .get();
                        int startIndex = data.indexOf(gyroDataObj);
                        long currentWindowEnd = gyroDataObj.getTime() + windowSize;

                        final List<GyroData> temp = new ArrayList<>();
                        GyroData current = data.get(startIndex);

                        while (current.getTime() < currentWindowEnd) {
                            temp.add(data.get(startIndex));
                            current = data.get(++startIndex);
                        }
                        partitions.add(temp);
                    }
                    return partitions;
                })
                .collect(Collectors.toList());
    }

    private static List<Map<Coordinate, Double>> extractAverage(final List<List<GyroData>> partitions) {
        final List<Map<Coordinate, Double>> average = new ArrayList<>(partitions.size());

        partitions.stream()
                .forEach(p -> {
                    final Map<Coordinate, Double> temp = new HashMap<>();

                    temp.put(Coordinate.X, p.stream().mapToDouble(s -> s.getX()).average().getAsDouble());
                    temp.put(Coordinate.Y, p.stream().mapToDouble(s -> s.getY()).average().getAsDouble());
                    temp.put(Coordinate.Z, p.stream().mapToDouble(s -> s.getZ()).average().getAsDouble());

                    average.add(temp);
                });

        return average;
    }

    private static List<Map<Coordinate, Double>> extractVariance(final List<List<GyroData>> partitions,
                                                                 final List<Map<Coordinate, Double>> average) {
        final List<Map<Coordinate, Double>> variance = new ArrayList<>(partitions.size());

        partitions.stream()
                .forEach(p -> {
                    final Map<Coordinate, Double> temp = new HashMap<>();
                    final Map<Coordinate, Double> val = average.get(partitions.indexOf(p));

                    temp.put(Coordinate.X, p.stream()
                            .mapToDouble(s ->
                                    Math.pow(s.getX() - val.get(Coordinate.X), 2)).average().getAsDouble());
                    temp.put(Coordinate.Y, p.stream()
                            .mapToDouble(s ->
                                    Math.pow(s.getY() - val.get(Coordinate.Y), 2)).average().getAsDouble());
                    temp.put(Coordinate.Z, p.stream()
                            .mapToDouble(s ->
                                    Math.pow(s.getZ() - val.get(Coordinate.Z), 2)).average().getAsDouble());

                    variance.add(temp);
                });

        return variance;
    }

    private static List<Map<Coordinate, Double>> extractDeviation(final List<List<GyroData>> partitions,
                                                           final List<Map<Coordinate, Double>> variance) {
        final List<Map<Coordinate, Double>> deviation = new ArrayList<>(variance.size());

        partitions
                .stream()
                .forEach(p -> {
                    final Map<Coordinate, Double> temp = new HashMap<>();
                    final Map<Coordinate, Double> val = variance.get(partitions.indexOf(p));

                    temp.put(Coordinate.X, Math.pow(val.get(Coordinate.X), 0.5));
                    temp.put(Coordinate.Y, Math.pow(val.get(Coordinate.Y), 0.5));
                    temp.put(Coordinate.Z, Math.pow(val.get(Coordinate.Z), 0.5));

                    deviation.add(temp);
                });

        return deviation;
    }

    private static List<List<Double>> extractMagnitude(final List<List<GyroData>> partitions) {
        final List<List<Double>> magnitude = new ArrayList<>(partitions.size());

        partitions.stream()
                .forEach(p -> {
                    final List<Double> temp = new ArrayList<>();

                    p.forEach(s ->
                            temp.add(Math.pow(Math.pow(s.getX(), 2) + Math.pow(s.getY(), 2) + Math.pow(s.getZ(), 2), 0.5)));

                    magnitude.add(temp);
                });

        return magnitude;
    }

    private static List<Double> extractMagnitudeVariance(List<List<Double>> magnitude) {
        final List<Double> average = magnitude.stream()
                .map(p -> p.stream()
                        .mapToDouble(s -> s.doubleValue()).average().getAsDouble())
                .collect(Collectors.toList());

        return magnitude.stream()
                .map(p -> { int index = magnitude.indexOf(p);
                    return p.stream()
                            .mapToDouble(k -> Math.pow(k - average.get(index), 2))
                            .average().getAsDouble();
                }).collect(Collectors.toList());
    }

    public Model trainModel(ModelConfig modelConfig, List<Recording> recordingList, User user) {
        final Attribute xAverage = new Attribute("xAverage");
        final Attribute yAverage = new Attribute("yAverage");
        final Attribute zAverage = new Attribute("zAverage");

        final Attribute xVar = new Attribute("xVar");
        final Attribute yVar = new Attribute("yVar");
        final Attribute zVar = new Attribute("zVar");

        final Attribute magnitVar = new Attribute("magnitVar");

        final List<String> types = Arrays.asList(RecordType.values()).stream()
                .map(RecordType::toString)
                .collect(Collectors.toList());
        final Attribute cls = new Attribute("cls", types);

        final ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(xAverage, yAverage, zAverage, xVar,
                yVar, zVar, magnitVar, cls));

        final List<List<List<GyroData>>> partitions = getPartitions(modelConfig, recordingList);

        int size = (int) partitions.stream()
                .mapToLong(p -> p.stream().count()).sum();

        final Instances instances = new Instances("Rel", attributes, size);
        instances.setClass(cls);

        partitions.stream()
                .forEach(p -> {
                    final List<Map<Coordinate, Double>> average = extractAverage(p);
                    final List<Map<Coordinate, Double>> variance = extractVariance(p, average);
                    final List<Double> magnitudeVar = extractMagnitudeVariance(extractMagnitude(p));

                    final Instance instance = new DenseInstance(8);
                    int index = partitions.indexOf(p);

                    instance.setValue(xAverage, average.get(index).get(Coordinate.X));
                    instance.setValue(yAverage, average.get(index).get(Coordinate.Y));
                    instance.setValue(zAverage, average.get(index).get(Coordinate.Z));
                    instance.setValue(xVar, variance.get(index).get(Coordinate.X));
                    instance.setValue(yVar, variance.get(index).get(Coordinate.Y));
                    instance.setValue(zVar, variance.get(index).get(Coordinate.Z));
                    instance.setValue(magnitVar, magnitudeVar.get(index));
                    instance.setValue(cls, recordingList.get(index).getType().toString());

                    instances.add(instance);
                });

        try {
            final Classifier cModel = new NaiveBayes();
            final Evaluation eTest = new Evaluation(instances);

            cModel.buildClassifier(instances);
            eTest.evaluateModel(cModel, instances);

            final String strSummary = eTest.toSummaryString();
            final Blob classifierBlob = BlobProxy.generateProxy(
                    SerializationUtils.serialize((Serializable) cModel));

            final Model model = new Model(eTest.weightedFMeasure(),
                    cModel.getClass().getSimpleName(),
                    Calendar.getInstance(), classifierBlob);

            final Model savedModel = modelRepository.save(model);

            user.getModels().add(savedModel);
            userRepository.save(user);

            return savedModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
