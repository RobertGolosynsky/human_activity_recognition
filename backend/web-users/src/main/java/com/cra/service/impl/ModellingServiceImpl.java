package com.cra.service.impl;

import com.cra.domain.entity.GyroData;
import com.cra.domain.entity.Recording;
import com.cra.service.interfaces.ModellingService;
import com.cra.util.Coordinate;
import com.cra.util.ModelConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModellingServiceImpl implements ModellingService {

    public List<List<List<GyroData>>> getPartitions(final ModelConfig modelConfig, final List<Recording> recordingList) {
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

    public List<Map<Coordinate, Double>> extractAverage(final List<List<GyroData>> partitions) {
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

    public List<Map<Coordinate, Double>> extractVariance(final List<List<GyroData>> partitions,
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

    public List<Map<Coordinate, Double>> extractDeviation(final List<List<GyroData>> partitions,
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

    public List<List<Double>> extractMagnitude(final List<List<GyroData>> partitions) {
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

    public List<Double> extractMagnitudeVariance(List<List<Double>> magnitude) {
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

}
