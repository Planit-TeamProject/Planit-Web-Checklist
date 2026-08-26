package com.example.project_checklist.studyplanitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.example.project_checklist.global.exception.CustomException;
import com.example.project_checklist.member.entity.Member;
import com.example.project_checklist.studyplan.entity.StudyPlan;
import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.dto.TodayPlanResponse;
import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import com.example.project_checklist.studyplanitem.repository.StudyPlanItemRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StudyPlanItemServiceTest {

    @Mock
    private StudyPlanItemRepository studyPlanItemRepository;

    private StudyPlanItemService studyPlanItemService;

    private StudyPlanItem itemOf(Long ownerId, Long id, int progressRate) {
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", ownerId);

        StudyPlan studyPlan = new StudyPlan();
        ReflectionTestUtils.setField(studyPlan, "member", member);

        StudyPlanItem item = StudyPlanItem.builder()
                .studyPlan(studyPlan)
                .planDate(LocalDate.now())
                .subject("자료구조")
                .content("3단원 정리")
                .sortOrder(1)
                .build();
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "progressRate", progressRate);
        return item;
    }

    @Test
    void 오늘의_계획을_조회하면_항목_리스트와_함께_진행률_평균이_반환된다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        LocalDate today = LocalDate.now();
        given(studyPlanItemRepository.findByMemberIdAndPlanDate(1L, today))
                .willReturn(List.of(
                        itemOf(1L, 100L, 100),
                        itemOf(1L, 101L, 50),
                        itemOf(1L, 102L, 0),
                        itemOf(1L, 103L, 75)));

        TodayPlanResponse result = studyPlanItemService.getTodayPlan(1L, today);

        assertThat(result.getItems()).hasSize(4);
        assertThat(result.getTodayProgressRate()).isEqualTo(56); // (100+50+0+75)/4 = 56.25 -> 반올림 56
    }

    @Test
    void 항목이_없으면_오늘_진행률은_0이다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        LocalDate today = LocalDate.now();
        given(studyPlanItemRepository.findByMemberIdAndPlanDate(1L, today)).willReturn(List.of());

        TodayPlanResponse result = studyPlanItemService.getTodayPlan(1L, today);

        assertThat(result.getTodayProgressRate()).isEqualTo(0);
    }

    @Test
    void 진행률을_100으로_체크하면_완료일시가_기록된다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        StudyPlanItem item = itemOf(1L, 100L, 0);
        given(studyPlanItemRepository.findById(100L)).willReturn(Optional.of(item));

        StudyPlanItemResponse result = studyPlanItemService.updateProgress(1L, 100L, 100);

        assertThat(result.getProgressRate()).isEqualTo(100);
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getCompletedAt()).isNotNull();
    }

    @Test
    void 완료된_항목의_진행률을_다시_낮추면_완료일시가_비워진다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        StudyPlanItem item = itemOf(1L, 100L, 100);
        ReflectionTestUtils.setField(item, "completedAt", LocalDateTime.now());
        given(studyPlanItemRepository.findById(100L)).willReturn(Optional.of(item));

        StudyPlanItemResponse result = studyPlanItemService.updateProgress(1L, 100L, 50);

        assertThat(result.getProgressRate()).isEqualTo(50);
        assertThat(result.getCompletedAt()).isNull();
    }

    @Test
    void 허용되지_않는_진행률_값이면_예외가_발생한다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);

        assertThatThrownBy(() -> studyPlanItemService.updateProgress(1L, 100L, 30))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void 본인_소유가_아닌_항목을_체크하면_예외가_발생한다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        StudyPlanItem item = itemOf(2L, 100L, 0); // 소유자는 memberId=2

        given(studyPlanItemRepository.findById(100L)).willReturn(Optional.of(item));

        assertThatThrownBy(() -> studyPlanItemService.updateProgress(1L, 100L, 50))
                .isInstanceOf(CustomException.class);
    }
}
