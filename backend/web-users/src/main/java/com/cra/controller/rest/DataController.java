package com.cra.controller.rest;

import com.cra.domain.entity.RecordType;
import com.cra.domain.entity.Recording;
import com.cra.domain.entity.User;
import com.cra.model.json.response.CRAErrorResponse;
import com.cra.repository.RecordingRepository;
import com.cra.repository.UserRepository;
import com.cra.service.interfaces.WekaArffWriter;
import com.cra.util.ModelConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataController {

    private final RecordingRepository recordingRepository;
    private final UserRepository userRepository;
    private final WekaArffWriter arffWriter;

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
    public @ResponseBody
    List<RecordingDTO> getAllData(final Principal principal) {
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

    @PostMapping("/arff/raw")
    public Object getRawRecordingsAsArff(@RequestBody List<Long> recordingsIds, final Principal principal, HttpServletResponse response) {
        if (recordingsIds == null || recordingsIds.isEmpty()){
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("Array of recordings' ids expected."));
        }
        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        final List<Recording> recordingList = recordingsWithIds(user, recordingsIds);

        if (recordingList.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("No recordings founds with ids: "+recordingsIds));
        }

        sendFile(response, (fos->arffWriter.writeRawArff(recordingList, fos)));
        return null;
    }

    @PostMapping("/arff/processed")
    public Object getRecordingsAsArffWithFeatures(Principal principal, HttpServletResponse response, @RequestBody ModelConfig modelConfig) {
        if (modelConfig.getRecordingIds() == null || modelConfig.getRecordingIds().isEmpty()){
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("Array of recordings' ids expected."));
        }

        final User user = userRepository.findByLoginIgnoreCase(principal.getName());
        final List<Recording> recordingList = recordingsWithIds(user, modelConfig.getRecordingIds());
        if (recordingList.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CRAErrorResponse("No recordings founds with ids: "+modelConfig.getRecordingIds()));
        }

        sendFile(response, (fos->arffWriter.writeArffWithFeatures(recordingList, modelConfig, fos)));
        return null;
    }

    private List<Recording> recordingsWithIds(User user, List<Long> recordingsIds){
        final Map<Long, Recording> recordingsMap = user
                .getRecordings()
                .stream()
                .collect(Collectors.toMap(Recording::getId, Function.identity()));
        return recordingsIds
                .stream()
                .map(p -> recordingsMap.get(p))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private void sendFile(HttpServletResponse response, Consumer<OutputStream> outputStreamConsumer) {
        try {

            File tempFile = File.createTempFile("data", ".arff");
            FileOutputStream fos = new FileOutputStream(tempFile);
            OutputStream os = response.getOutputStream();
            outputStreamConsumer.accept(fos);
            fos.close();
            long fileSize = tempFile.length();
            response.setContentType("text/*");
            response.setContentLength((int) fileSize);
            IOUtils.copy(new FileInputStream(tempFile), os);
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=data.arff";
            response.setHeader(headerKey, headerValue);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
