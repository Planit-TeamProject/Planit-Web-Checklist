package com.example.project_checklist.studyplanitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.example.project_checklist.global.exception.CustomException;
import com.example.project_checklist.member.entity.Member;
import com.example.project_checklist.studyplan.entity.StudyPlan;
import com.example.project_checklist.studyplanitem.dto.StudyPlanItemResponse;
import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import com.example.project_checklist.studyplanitem.repository.StudyPlanItemRepository;
import java.time.LocalDate;
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

    private StudyPlanItem itemOf(Long ownerId, Long id) {
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
        return item;
    }

    @Test
    void 오늘의_계획을_조회하면_회원과_날짜로_필터링된_리스트가_반환된다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        LocalDate today = LocalDate.now();
        given(studyPlanItemRepository.findByMemberIdAndPlanDate(1L, today))
                .willReturn(List.of(itemOf(1L, 100L)));

        List<StudyPlanItemResponse> result = studyPlanItemService.getPlanItems(1L, today);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
    }

    @Test
    void 미완료_항목을_체크하면_완료로_바뀌고_완료일시가_기록된다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        StudyPlanItem item = itemOf(1L, 100L);
        given(studyPlanItemRepository.findById(100L)).willReturn(Optional.of(item));

        StudyPlanItemResponse result = studyPlanItemService.toggleCheck(1L, 100L);

        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getCompletedAt()).isNotNull();
    }

    @Test
    void 본인_소유가_아닌_항목을_체크하면_예외가_발생한다() {
        studyPlanItemService = new StudyPlanItemService(studyPlanItemRepository);
        StudyPlanItem item = itemOf(2L, 100L); // 소유자는 memberId=2

        given(studyPlanItemRepository.findById(100L)).willReturn(Optional.of(item));

        assertThatThrownBy(() -> studyPlanItemService.toggleCheck(1L, 100L))
                .isInstanceOf(CustomException.class);
    }
}
