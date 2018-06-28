package com.cra.controller.rest;

import com.cra.domain.entity.*;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.ModellingService;
import com.cra.util.Coordinate;
import com.cra.util.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ModellingService modellingService;

    @GetMapping("/list")
    public @ResponseBody List<Model> getModels(Principal principal) {
        return userRepository.findByEmail(principal.getName()).getModels();
    }

    @PostMapping("/create")
    public ResponseEntity<?> getRecordingsByIds(Principal principal, @RequestBody ModelConfig modelConfig) {
        final User user = userRepository.findByEmail(principal.getName());
        final Map<Long, Recording> recordingsMap = user
                .getRecordings()
                .stream()
                .collect(Collectors.toMap(Recording::getId, Function.identity()));
        final List<Recording> recordingList = modelConfig
                .getRecordingIds()
                .stream()
                .map(p -> recordingsMap.get(p))
                .collect(Collectors.toList());

        if (hasAllNulls(recordingList)) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("No recordings found! Cannot create a model."));
        }

        return ResponseEntity.ok(doWeka(modelConfig, recordingList));
    }

    private static boolean hasAllNulls(Iterable<?> array) {
        return StreamSupport.stream(array.spliterator(), true).allMatch(o -> o == null);
    }

    public String doWeka(ModelConfig modelConfig, List<Recording> recordingList) {
        Attribute xAverage = new Attribute("xAverage");
        Attribute yAverage = new Attribute("yAverage");
        Attribute zAverage = new Attribute("zAverage");

        Attribute xVar = new Attribute("xVar");
        Attribute yVar = new Attribute("yVar");
        Attribute zVar = new Attribute("zVar");

        Attribute magnitVar = new Attribute("magnitVar");

        List<String> types = Arrays.asList(RecordType.values()).stream()
                .map(RecordType::toString)
                .collect(Collectors.toList());
        Attribute cls = new Attribute("cls", types);

        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(xAverage, yAverage, zAverage, xVar, yVar, zVar, magnitVar, cls));

        final List<List<List<GyroData>>> partitions = modellingService.getPartitions(modelConfig, recordingList);

        int size = (int) partitions.stream()
                .mapToLong(p -> p.stream().count()).sum();

        Instances instances = new Instances("Rel", attributes, size);
        instances.setClass(cls);

        partitions.stream()
                .forEach(p -> {
                    final List<Map<Coordinate, Double>> average = modellingService.extractAverage(p);
                    final List<Map<Coordinate, Double>> variance = modellingService.extractVariance(p, average);
                    final List<Double> magnitudeVar = modellingService.extractMagnitudeVariance(modellingService.extractMagnitude(p));

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
            Classifier cModel = new NaiveBayes();
            Evaluation eTest = new Evaluation(instances);

            cModel.buildClassifier(instances);
            eTest.evaluateModel(cModel, instances);

            String strSummary = eTest.toSummaryString();
            System.out.println(strSummary);

            return strSummary;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}