package com.schooltrack.repository;

import com.schooltrack.model.BoardingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardingRecordRepository extends JpaRepository<BoardingRecord, Long> {
    List<BoardingRecord> findByTripIdOrderByTimestampAsc(Long tripId);
    List<BoardingRecord> findByStudentIdOrderByTimestampDesc(Long studentId);
    long countByTripIdAndType(Long tripId, BoardingRecord.BoardingType type);
}
