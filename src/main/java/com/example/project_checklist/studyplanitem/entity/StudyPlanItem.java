package com.example.project_checklist.studyplanitem.entity;

import com.example.project_checklist.global.entity.BaseTimeEntity;
import com.example.project_checklist.studyplan.entity.StudyPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 04_ERD_테이블정의서 - study_plan_item(학습 계획 항목) 매핑.
 * 담당 요구사항: REQ-F-001~005 (오늘의 학습 계획 조회/체크), REQ-NF-001~004.
 *
 * 회원 정보는 study_plan 을 통해서만 참조하고 이 테이블에는 따로 저장하지 않습니다
 * (공통 설계 규칙 - 계산/중복 저장 금지 원칙).
 */
@Entity
@Table(
        name = "study_plan_item",
        indexes = {
                @Index(name = "idx_study_plan_id", columnList = "study_plan_id"),
                @Index(name = "idx_plan_date", columnList = "plan_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPlanItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_plan_id", nullable = false)
    private StudyPlan studyPlan;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "subject", length = 50, nullable = false)
    private String subject;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public StudyPlanItem(StudyPlan studyPlan, LocalDate planDate, String subject, String content, int sortOrder) {
        this.studyPlan = studyPlan;
        this.planDate = planDate;
        this.subject = subject;
        this.content = content;
        this.sortOrder = sortOrder;
        this.completed = false;
    }

    /**
     * REQ-F-003, REQ-F-004: 완료/미완료 체크. 완료로 바뀔 때만 완료 일시를 기록하고,
     * 다시 미완료로 되돌리면 완료 일시를 비웁니다.
     */
    public void toggleCompleted() {
        if (this.completed) {
            this.completed = false;
            this.completedAt = null;
        } else {
            this.completed = true;
            this.completedAt = LocalDateTime.now();
        }
    }

    /** 본인 소유 확인(REQ-NF-003 관련)용 — study_plan 을 거쳐 회원 id 를 얻습니다. */
    public Long getMemberId() {
        return this.studyPlan.getMemberId();
    }
}
