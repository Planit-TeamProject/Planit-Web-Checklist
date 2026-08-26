package com.example.project_checklist.studyplanitem.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * "오늘 할 일" 화면 응답. items 는 항목별 리스트, todayProgressRate 는 그날 항목들의
 * progressRate 단순 평균(반올림, 0~100)입니다. 항목이 하나도 없으면 0입니다.
 */
@Getter
@Builder
public class TodayPlanResponse {

    private LocalDate date;
    private int todayProgressRate;
    private List<StudyPlanItemResponse> items;
}
