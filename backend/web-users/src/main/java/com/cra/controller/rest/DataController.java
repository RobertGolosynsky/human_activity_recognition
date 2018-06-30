package com.cra.controller.rest;

import com.cra.domain.entity.RecordType;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.RecordingRepository;
import com.cra.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataController {

    private final RecordingRepository recordingRepository;
    private final UserRepository userRepository;

    @PostMapping("/save")
    public ResponseEntity<?> saveData(@RequestBody final Recording recording, final Principal principal) {
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        final List<Recording> recordingList = user.getRecordings();

        if (recordingList
                .stream()
                .anyMatch(p -> p.getDate().equals(recording.getDate()))) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("Recording already exists!"));
        } else {
            final Recording saved = recordingRepository.save(recording);
            user.getRecordings().add(saved);
            userRepository.save(user);

            final RecordingDTO result = new RecordingDTO(saved.getId(), saved.getDate(), saved.getType(),
                    saved.getDuration());

            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveData(@RequestBody final List<Recording> recordings, final Principal principal) {
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        final List<Recording> currentRecordings = user.getRecordings();

        if (currentRecordings
                .stream()
                .anyMatch(p -> recordings
                        .stream()
                        .anyMatch(s -> s.getDate().equals(p.getDate())))) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("One of recordings already exists!"));
        } else {
            final List<Recording> savedRecordings = recordingRepository.save(recordings);
            user.getRecordings().addAll(savedRecordings);
            userRepository.save(user);

            final List<RecordingDTO> result = savedRecordings
                    .stream()
                    .map(p -> new RecordingDTO(p.getId(), p.getDate(), p.getType(), p.getDuration()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/list")
    public @ResponseBody List<RecordingDTO> getAllData(final Principal principal) {
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        final List<RecordingDTO> result = user
                .getRecordings()
                .stream()
                .map(p -> new RecordingDTO(p.getId(), p.getDate(), p.getType(), p.getDuration()))
                .collect(Collectors.toList());

        return result;
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecordingById(final Principal principal, @PathVariable final Long id) {
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        user.getRecordings()
                .removeIf(p -> p.getId().equals(id));
        userRepository.save(user);

        return ResponseEntity.ok(null);
    }

    @Getter
    @RequiredArgsConstructor
    private static class RecordingDTO {
        private final Long id;
        private final Calendar date;
        private final RecordType type;
        private final Long duration;
    }

}
