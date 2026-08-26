package com.example.project_checklist.studyplanitem.dto;

import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyPlanItemResponse {

    private Long id;
    private String subject;
    private String content;
    private LocalDate planDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private int sortOrder;
    private int progressRate;
    private boolean completed;
    private LocalDateTime completedAt;

    public static StudyPlanItemResponse from(StudyPlanItem item) {
        return StudyPlanItemResponse.builder()
                .id(item.getId())
                .subject(item.getSubject())
                .content(item.getContent())
                .planDate(item.getPlanDate())
                .startTime(item.getStartTime())
                .endTime(item.getEndTime())
                .durationMinutes(item.getDurationMinutes())
                .sortOrder(item.getSortOrder())
                .progressRate(item.getProgressRate())
                .completed(item.isCompleted())
                .completedAt(item.getCompletedAt())
                .build();
    }
}
