package com.example.project_checklist.studyplanitem.entity;

import com.example.project_checklist.global.entity.BaseTimeEntity;
import com.example.project_checklist.studyplan.entity.StudyPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 04_ERD_테이블정의서 - study_day_completion(학습 일일 완료 기록) 매핑.
 *
 * "오늘 학습 마무리하기" 버튼을 눌러서 그 날짜의 학습을 모두 완료 처리했다는 사실을
 * 기록하는 테이블입니다. study_plan_id + plan_date 조합은 유일하며(하루 1회만 기록),
 * 이미 기록이 있으면 새로 만들지 않고 기존 기록을 그대로 반환합니다
 * (StudyPlanItemService#completeToday 참고).
 *
 * completed_at 은 별도 컬럼을 두지 않고, 상속받는 BaseTimeEntity 의 createdAt
 * (이 행이 생성된 시각 = 완료 처리한 시각)을 그대로 사용합니다.
 */
@Entity
@Table(
        name = "study_day_completion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_study_plan_plan_date",
                columnNames = {"study_plan_id", "plan_date"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyDayCompletion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_plan_id", nullable = false)
    private StudyPlan studyPlan;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Builder
    public StudyDayCompletion(StudyPlan studyPlan, LocalDate planDate) {
        this.studyPlan = studyPlan;
        this.planDate = planDate;
    }
}
