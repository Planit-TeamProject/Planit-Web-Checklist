package com.example.project_checklist.studyplanitem.controller;

import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.dto.TodayPlanResponse;
import com.example.project_checklist.studyplanitem.service.StudyPlanItemService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-plan-items")
@RequiredArgsConstructor
public class StudyPlanItemController {

    private final StudyPlanItemService studyPlanItemService;

    /**
     * REQ-F-050~054: GET /api/study-plan-items?memberId=1&date=2026-08-25
     * date 를 생략하면 오늘 날짜로 조회합니다. 응답에는 항목 리스트와 함께
     * 오늘 진행률(todayProgressRate, 0~100)이 같이 내려갑니다.
     *
     * TODO: memberId 를 지금은 파라미터로 받고 있습니다. 인증 도메인이 머지되면
     * @AuthenticationPrincipal 등으로 로그인한 회원 id 를 꺼내는 방식으로 교체하세요.
     */
    @GetMapping
    public ResponseEntity<TodayPlanResponse> getTodayPlan(
            @RequestParam Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(studyPlanItemService.getTodayPlan(memberId, date));
    }

    /**
     * REQ-F-050~054: PATCH /api/study-plan-items/{id}/progress?memberId=1&progressRate=75
     * progressRate 는 0/25/50/75/100 중 하나만 허용됩니다. 클릭 즉시 반영되고 결과를 바로 반환합니다.
     */
    @PatchMapping("/{id}/progress")
    public ResponseEntity<StudyPlanItemResponse> updateProgress(
            @RequestParam Long memberId,
            @PathVariable("id") Long itemId,
            @RequestParam int progressRate) {
        return ResponseEntity.ok(studyPlanItemService.updateProgress(memberId, itemId, progressRate));
    }
}
