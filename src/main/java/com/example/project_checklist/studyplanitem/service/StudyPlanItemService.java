package com.example.project_checklist.studyplanitem.service;

import com.example.project_checklist.global.exception.CustomException;
import com.example.project_checklist.global.exception.ErrorCode;
import com.example.project_checklist.studyplanitem.dto.DayCompletionResponse;
import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.dto.TodayPlanResponse;
import com.example.project_checklist.studyplanitem.entity.StudyDayCompletion;
import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import com.example.project_checklist.studyplanitem.repository.StudyDayCompletionRepository;
import com.example.project_checklist.studyplanitem.repository.StudyPlanItemRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyPlanItemService {

    private static final Set<Integer> ALLOWED_PROGRESS_RATES = Set.of(0, 25, 50, 75, 100);

    private final StudyPlanItemRepository studyPlanItemRepository;
    private final StudyDayCompletionRepository studyDayCompletionRepository;

    /**
     * REQ-F-050~054: 오늘(또는 지정한 날짜)의 학습 계획 리스트 + 오늘 진행률 + 마무리 여부 조회.
     * 오늘 진행률은 그날 항목들의 progressRate 단순 평균을 반올림한 값입니다.
     */
    public TodayPlanResponse getTodayPlan(Long memberId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<StudyPlanItem> items = studyPlanItemRepository.findByMemberIdAndPlanDate(memberId, targetDate);

        StudyDayCompletion completion = items.isEmpty()
                ? null
                : studyDayCompletionRepository
                        .findByStudyPlanIdAndPlanDate(items.get(0).getStudyPlan().getId(), targetDate)
                        .orElse(null);

        return TodayPlanResponse.builder()
                .date(targetDate)
                .todayProgressRate(calculateAverageProgress(items))
                .dayCompleted(completion != null)
                .dayCompletedAt(completion != null ? completion.getCreatedAt() : null)
                .items(items.stream().map(StudyPlanItemResponse::from).toList())
                .build();
    }

    /**
     * REQ-F-050~054: 항목별 진행률 체크(0/25/50/75/100). 본인 소유 항목이 아니면 거부합니다.
     */
    @Transactional
    public StudyPlanItemResponse updateProgress(Long memberId, Long itemId, int progressRate) {
        if (!ALLOWED_PROGRESS_RATES.contains(progressRate)) {
            throw new CustomException(ErrorCode.INVALID_PROGRESS_RATE);
        }

        StudyPlanItem item = studyPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_PLAN_ITEM_NOT_FOUND));

        if (!item.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.STUDY_PLAN_ITEM_ACCESS_DENIED);
        }

        item.updateProgress(progressRate);
        return StudyPlanItemResponse.from(item);
    }

    /**
     * "오늘 학습 마무리하기": 그날 항목이 하나 이상 등록돼 있으면, 전부 100%가 아니어도
     * (부득이하게 일부만 했더라도) 완료 기록을 남길 수 있습니다.
     * 이미 완료 기록이 있으면 새로 만들지 않고 기존 기록을 그대로 반환합니다(하루 1회).
     */
    @Transactional
    public DayCompletionResponse completeToday(Long memberId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<StudyPlanItem> items = studyPlanItemRepository.findByMemberIdAndPlanDate(memberId, targetDate);

        if (items.isEmpty()) {
            throw new CustomException(ErrorCode.STUDY_PLAN_ITEMS_EMPTY_FOR_DATE);
        }

        Long studyPlanId = items.get(0).getStudyPlan().getId();
        StudyDayCompletion completion = studyDayCompletionRepository
                .findByStudyPlanIdAndPlanDate(studyPlanId, targetDate)
                .orElseGet(() -> studyDayCompletionRepository.save(
                        StudyDayCompletion.builder()
                                .studyPlan(items.get(0).getStudyPlan())
                                .planDate(targetDate)
                                .build()));

        return DayCompletionResponse.builder()
                .date(targetDate)
                .completed(true)
                .completedAt(completion.getCreatedAt())
                .build();
    }

    private int calculateAverageProgress(List<StudyPlanItem> items) {
        if (items.isEmpty()) {
            return 0;
        }
        double average = items.stream()
                .mapToInt(StudyPlanItem::getProgressRate)
                .average()
                .orElse(0);
        return (int) Math.round(average);
    }
}
