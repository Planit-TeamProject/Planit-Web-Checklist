package com.example.project_checklist.studyplanitem.controller;

import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.service.StudyPlanItemService;
import java.time.LocalDate;
import java.util.List;
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
     * REQ-F-001, REQ-F-002: GET /api/study-plan-items?date=2026-08-25
     * date 를 생략하면 오늘 날짜로 조회합니다.
     *
     * TODO: memberId 를 지금은 파라미터로 받고 있습니다. 인증 도메인이 머지되면
     * @AuthenticationPrincipal 등으로 로그인한 회원 id 를 꺼내는 방식으로 교체하세요.
     */
    @GetMapping
    public ResponseEntity<List<StudyPlanItemResponse>> getPlanItems(
            @RequestParam Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(studyPlanItemService.getPlanItems(memberId, date));
    }

    /**
     * REQ-F-003, REQ-F-004: PATCH /api/study-plan-items/{id}/check
     * 클릭 즉시 완료/미완료를 토글하고 결과를 바로 반환합니다(별도 저장 버튼 없음).
     */
    @PatchMapping("/{id}/check")
    public ResponseEntity<StudyPlanItemResponse> toggleCheck(
            @RequestParam Long memberId,
            @PathVariable("id") Long itemId) {
        return ResponseEntity.ok(studyPlanItemService.toggleCheck(memberId, itemId));
    }
}
