package com.cra.service.interfaces;

import com.cra.domain.entity.GyroData;
import com.cra.domain.entity.Recording;
import com.cra.util.Coordinate;
import com.cra.util.ModelConfig;

import java.util.List;
import java.util.Map;

public interface ModellingService {

    List<List<List<GyroData>>> getPartitions(final ModelConfig modelConfig, final List<Recording> recordingList);
    List<Map<Coordinate, Double>> extractAverage(final List<List<GyroData>> partitions);
    List<Map<Coordinate, Double>> extractVariance(final List<List<GyroData>> partitions,
                                                  final List<Map<Coordinate, Double>> average);
    List<Map<Coordinate, Double>> extractDeviation(final List<List<GyroData>> partitions,
                                                   final List<Map<Coordinate, Double>> variance);
    List<List<Double>> extractMagnitude(final List<List<GyroData>> partitions);
    List<Double> extractMagnitudeVariance(List<List<Double>> magnitude);
}
