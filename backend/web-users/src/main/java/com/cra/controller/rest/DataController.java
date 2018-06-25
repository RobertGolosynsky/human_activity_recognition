package com.cra.controller.rest;

import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recordings")
public class DataController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/save")
    public ResponseEntity<?> saveData(@RequestBody Recording recording, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Recording> recordingList = user.getRecordings();

        if (recordingList.stream().anyMatch(p -> p.getDate().equals(recording.getDate()))) {
            return ResponseEntity.badRequest().body(new CRAErrorResponse("Recording already exists!"));
        } else {
            user.getRecordings().add(recording);
            User savedUser = userRepository.save(user);

            List<RecordingDTO> result = savedUser.getRecordings()
                    .stream()
                    .map(p -> new RecordingDTO(p.getId(), p.getDate()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/list")
    public @ResponseBody List<Calendar> getAllData(Principal principal) {
        final User user = userRepository.findByEmail(principal.getName());

        return user.getRecordings().stream().map(p -> p.getDate()).collect(Collectors.toList());
    }

    private static class RecordingDTO {
        Long id;
        Calendar date;

        public RecordingDTO(Long id, Calendar date) {
            this.id = id;
            this.date = date;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setDate(Calendar date) {
            this.date = date;
        }
    }

}
