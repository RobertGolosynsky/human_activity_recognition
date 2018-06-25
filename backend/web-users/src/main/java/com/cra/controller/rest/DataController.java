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
            userRepository.save(user);
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/list")
    public @ResponseBody List<Calendar> getAllData(Principal principal) {
        final User user = userRepository.findByEmail(principal.getName());

        return user.getRecordings().stream().map(p -> p.getDate()).collect(Collectors.toList());
    }

}
