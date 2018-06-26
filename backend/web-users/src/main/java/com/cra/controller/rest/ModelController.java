package com.cra.controller.rest;

import com.cra.domain.entity.GyroData;
import com.cra.domain.entity.Model;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        final ArrayList<ArrayList<GyroData>> partitions = getPartitions(modelConfig, recordingList);

        return ResponseEntity.ok(getPartitions(modelConfig, recordingList));
    }

    private ArrayList<ArrayList<GyroData>> getPartitions(final ModelConfig modelConfig, final List<Recording> recordingList) {
        int windowSize = modelConfig.getWindowSize();
        int receivedOffset = modelConfig.getOffset();
        int offset = receivedOffset == 0 ? 1 : receivedOffset;

        final ArrayList<ArrayList<GyroData>> partitions = new ArrayList<>();

        recordingList.stream()
                .forEach(p -> {
                    int listSize = p.getData().size();
                    int windowShiftCount = listSize < windowSize ? 0 : (listSize - windowSize) / offset + 1;

                    for (int i = 0; i < windowShiftCount; i++) {
                        final ArrayList<GyroData> temp = new ArrayList<>();

                        for (int j = i * offset; j < i * offset + windowSize; j++) {
                            temp.add(p.getData().get(j));
                        }
                        partitions.add(temp);
                    }
                });

        return partitions;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ModelConfig {
        private List<Long> recordingIds;
        private int windowSize;
        private int offset;
    }

}
