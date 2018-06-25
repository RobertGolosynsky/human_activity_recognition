package com.cra.repository;

import com.cra.domain.entity.Recording;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;

public interface RecordingRepository extends JpaRepository<Recording, Long> {

    Recording getRecordingByDate(Calendar c);

}
