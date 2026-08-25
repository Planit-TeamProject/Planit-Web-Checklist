package com.example.project_checklist.studyplanitem.service;

import com.example.project_checklist.global.exception.CustomException;
import com.example.project_checklist.global.exception.ErrorCode;
import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import com.example.project_checklist.studyplanitem.repository.StudyPlanItemRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyPlanItemService {

    private final StudyPlanItemRepository studyPlanItemRepository;

    /**
     * REQ-F-001: 오늘의 학습 계획 리스트 조회.
     * REQ-F-002: date 를 넘기면 다른 날짜도 같은 방식으로 조회.
     */
    public List<StudyPlanItemResponse> getPlanItems(Long memberId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return studyPlanItemRepository.findByMemberIdAndPlanDate(memberId, targetDate).stream()
                .map(StudyPlanItemResponse::from)
                .toList();
    }

    /**
     * REQ-F-003, REQ-F-004: 완료/미완료 체크. 본인 소유 항목이 아니면 거부합니다.
     */
    @Transactional
    public StudyPlanItemResponse toggleCheck(Long memberId, Long itemId) {
        StudyPlanItem item = studyPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_PLAN_ITEM_NOT_FOUND));

        if (!item.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.STUDY_PLAN_ITEM_ACCESS_DENIED);
        }

        item.toggleCompleted();
        return StudyPlanItemResponse.from(item);
    }
}
