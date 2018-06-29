package com.cra.controller.rest;

import com.cra.domain.entity.RecordType;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataController {

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
            user.getRecordings().add(recording);
            final User savedUser = userRepository.save(user);

            final Recording saved = savedUser
                    .getRecordings()
                    .stream()
                    .filter(p -> p.getDate().equals(recording.getDate()))
                    .findFirst()
                    .get();
            final RecordingDTO result = new RecordingDTO(saved.getId(), saved.getDate(), saved.getType(),
                    saved.getData().size());

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
            user.getRecordings().addAll(recordings);

            final Set<Calendar> dates = recordings
                    .stream()
                    .map(p -> p.getDate())
                    .collect(Collectors.toSet());
            final User savedUser  = userRepository.save(user);

            final List<Recording> saved = savedUser
                    .getRecordings()
                    .stream()
                    .filter(p -> dates.contains(p.getDate()))
                    .collect(Collectors.toList());

            final List<RecordingDTO> result = saved
                    .stream()
                    .map(p -> new RecordingDTO(p.getId(), p.getDate(), p.getType(), p.getData().size()))
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
                .map(p -> new RecordingDTO(p.getId(), p.getDate(), p.getType(), p.getData().size()))
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
        private final int length;
    }

}
