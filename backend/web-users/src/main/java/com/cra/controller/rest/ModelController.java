package com.cra.controller.rest;

import com.cra.domain.entity.Model;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.ModellingService;
import com.cra.util.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
        return userRepository.findByLoginIgnoreCase(principal.getName()).getModels();
    }

    @PostMapping("/create")
    public ResponseEntity<?> getRecordingsByIds(Principal principal, @RequestBody ModelConfig modelConfig) {
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
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

        return ResponseEntity.ok(modellingService.trainModel(modelConfig, recordingList, user));
    }

    private static boolean hasAllNulls(Iterable<?> array) {
        return StreamSupport.stream(array.spliterator(), true).allMatch(o -> o == null);
    }



}