package com.cra.controller.rest;

import com.cra.domain.entity.GyroData;
import com.cra.domain.entity.Model;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ModelController {

    private final UserRepository userRepository;

    @GetMapping("/list")
    public @ResponseBody List<Model> getModels(Principal principal) {
        return userRepository.findByEmail(principal.getName()).getModels();
    }

    @PostMapping("/create")
    public ResponseEntity<?> getRecordingsByIds(Principal principal, @RequestBody ModelConfig modelConfig) {
        final User user = userRepository.findByEmail(principal.getName());
        final Map<Long, Recording> recordingsMap = user.getRecordings()
                .stream()
                .collect(Collectors.toMap(Recording::getId, Function.identity()));
        final List<Recording> recordingList = modelConfig.getRecordingIds()
                .stream()
                .map(p -> recordingsMap.get(p))
                .collect(Collectors.toList());

        if (hasAllNulls(recordingList)) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("No recordings found! Cannot create a model."));
        }

        final ArrayList<ArrayList<GyroData>> partitions = getPartitions(modelConfig, recordingList);
        final ArrayList<Map<Coordinate, Double>> average = extractAverage(partitions);
        final ArrayList<Map<Coordinate, Double>> variance = extractVariance(partitions, average);
        final ArrayList<Map<Coordinate, Double>> deviation = extractDeviation(partitions, variance);
        final ArrayList<ArrayList<Double>> magnitude = extractMagnitude(partitions);
        final ArrayList<Object> result = new ArrayList<>();

        result.add(average);
        result.add(variance);
        result.add(deviation);
        result.add(magnitude);

        return ResponseEntity.ok(result);
    }

    private ArrayList<ArrayList<GyroData>> getPartitions(final ModelConfig modelConfig, final List<Recording> recordingList) {
        int windowSize = modelConfig.getWindowSize();
        int receivedOffset = modelConfig.getOffset();
        int offset = receivedOffset == 0 ? 1 : receivedOffset;

        final ArrayList<ArrayList<GyroData>> partitions = new ArrayList<>();

        recordingList.stream()
                .forEach(p -> {
                    final List<GyroData> data = p.getData();
                    long measureTime = p.getDuration();
                    int windowShiftCount = (int) (measureTime < windowSize ? 0 : (measureTime - windowSize) / offset + 1);

                    for (int i = 0; i < windowShiftCount; i++) {
                        final int tempCount = i;
                        final GyroData gyroDataObj = data.stream()
                                .filter(s -> s.getTime() >= offset * tempCount)
                                .findFirst()
                                .get();
                        int startIndex = data.indexOf(gyroDataObj);
                        long currentWindowEnd = gyroDataObj.getTime() + windowSize;

                        final ArrayList<GyroData> temp = new ArrayList<>();
                        GyroData current = data.get(startIndex);

                        while (current.getTime() < currentWindowEnd) {
                            temp.add(data.get(startIndex));
                            current = data.get(++startIndex);
                        }
                        partitions.add(temp);
                    }
                });

        return partitions;
    }

    private ArrayList<Map<Coordinate, Double>> extractAverage(final ArrayList<ArrayList<GyroData>> partitions) {
        final ArrayList<Map<Coordinate, Double>> average = new ArrayList<>(partitions.size());

        partitions.stream()
                .forEach(p -> {
                    Map<Coordinate, Double> temp = new HashMap<>();

                    temp.put(Coordinate.X, p.stream().mapToDouble(s -> s.getX()).average().getAsDouble());
                    temp.put(Coordinate.Y, p.stream().mapToDouble(s -> s.getY()).average().getAsDouble());
                    temp.put(Coordinate.Z, p.stream().mapToDouble(s -> s.getZ()).average().getAsDouble());

                    average.add(temp);
                });

        return average;
    }

    private ArrayList<Map<Coordinate, Double>> extractVariance(final ArrayList<ArrayList<GyroData>> partitions, final ArrayList<Map<Coordinate, Double>> average) {
        final ArrayList<Map<Coordinate, Double>> variance = new ArrayList<>(average.size());

        partitions.stream()
                .forEach(p -> {
                    Map<Coordinate, Double> temp = new HashMap<>();
                    int index = partitions.indexOf(p);
                    int size = p.size();

                    temp.put(Coordinate.X, p.stream()
                            .mapToDouble(s -> Math.pow(s.getX() - average.get(index).get(Coordinate.X), 2)).sum() / size);
                    temp.put(Coordinate.Y, p.stream()
                            .mapToDouble(s -> Math.pow(s.getY() - average.get(index).get(Coordinate.Y), 2)).sum() / size);
                    temp.put(Coordinate.Z, p.stream()
                            .mapToDouble(s -> Math.pow(s.getZ() - average.get(index).get(Coordinate.Z), 2)).sum() / size);

                    variance.add(temp);
                });

        return variance;
    }

    private ArrayList<Map<Coordinate, Double>> extractDeviation(final ArrayList<ArrayList<GyroData>> partitions, final ArrayList<Map<Coordinate, Double>> variance) {
        final ArrayList<Map<Coordinate, Double>> deviation = new ArrayList<>(variance.size());

        partitions.stream()
                .forEach(p -> {
                    Map<Coordinate, Double> temp = new HashMap<>();
                    int index = partitions.indexOf(p);

                    temp.put(Coordinate.X, Math.pow(variance.get(index).get(Coordinate.X), 0.5));
                    temp.put(Coordinate.Y, Math.pow(variance.get(index).get(Coordinate.Y), 0.5));
                    temp.put(Coordinate.Z, Math.pow(variance.get(index).get(Coordinate.Z), 0.5));

                    deviation.add(temp);
                });

        return deviation;
    }

    private ArrayList<ArrayList<Double>> extractMagnitude(final ArrayList<ArrayList<GyroData>> partitions) {
        final ArrayList<ArrayList<Double>> magnitude = new ArrayList<>(partitions.size());

        partitions.stream()
                .forEach(p -> {
                    ArrayList<Double> temp = new ArrayList<>();

                    p.forEach(s ->
                            temp.add(Math.pow(Math.pow(s.getX(), 2) + Math.pow(s.getY(), 2) + Math.pow(s.getZ(), 2), 0.5)));

                    magnitude.add(temp);
                });

        return magnitude;
    }

    private static boolean hasAllNulls(Iterable<?> array) {
        return StreamSupport.stream(array.spliterator(), true).allMatch(o -> o == null);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ModelConfig {
        private List<Long> recordingIds;
        private int windowSize;
        private int offset;
    }

    private enum Coordinate{
        X, Y, Z
    }

}