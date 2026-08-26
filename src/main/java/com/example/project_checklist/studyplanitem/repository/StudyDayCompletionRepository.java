package com.example.project_checklist.studyplanitem.repository;

import com.example.project_checklist.studyplanitem.entity.StudyDayCompletion;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyDayCompletionRepository extends JpaRepository<StudyDayCompletion, Long> {

    Optional<StudyDayCompletion> findByStudyPlanIdAndPlanDate(Long studyPlanId, LocalDate planDate);
}
