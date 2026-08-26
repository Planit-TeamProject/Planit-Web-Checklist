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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 04_ERD_테이블정의서 - study_plan_item(학습 계획 항목) 매핑.
 * 담당 요구사항: REQ-F-050~054 (오늘의 학습 계획 조회/체크), REQ-NF-001~004.
 *
 * 회원 정보는 study_plan 을 통해서만 참조하고 이 테이블에는 따로 저장하지 않습니다
 * (공통 설계 규칙 - 계산/중복 저장 금지 원칙).
 *
 * 완료 여부는 boolean 이 아니라 0/25/50/75/100 다섯 단계의 진행률(progress_rate)로 관리합니다.
 * "오늘 진행률"(당일 전체 진행률)은 별도 컬럼으로 저장하지 않고, 조회 시점에 그날 항목들의
 * progress_rate 단순 평균으로 계산합니다 (StudyPlanItemService 참고).
 *
 * 화면 목업 기준 항목별 시간대 표시(예: "09:00 – 10:30", "저녁 · 30분")를 위해
 * startTime/endTime/durationMinutes 를 추가했습니다. 시간이 고정된 항목은
 * startTime/endTime 을 사용하고, 시간이 고정되지 않은 항목(자동 추가된 복습 등)은
 * durationMinutes 만 채우고 startTime/endTime 은 NULL 로 둡니다.
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

    /** 계획된 시작 시각. 시간대가 정해지지 않은 항목은 NULL. */
    @Column(name = "start_time")
    private LocalTime startTime;

    /** 계획된 종료 시각. startTime 과 함께 사용, NULL 가능. */
    @Column(name = "end_time")
    private LocalTime endTime;

    /** startTime/endTime 이 없을 때 화면에 보여줄 예상 소요 시간(분). */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 0, 25, 50, 75, 100 중 하나. 100이면 완료로 간주합니다. */
    @Column(name = "progress_rate", nullable = false)
    private int progressRate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public StudyPlanItem(StudyPlan studyPlan, LocalDate planDate, String subject, String content,
                          LocalTime startTime, LocalTime endTime, Integer durationMinutes, int sortOrder) {
        this.studyPlan = studyPlan;
        this.planDate = planDate;
        this.subject = subject;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.sortOrder = sortOrder;
        this.progressRate = 0;
    }

    /**
     * REQ-F-050~054: 학습 진행률 체크. progressRate 가 100이 되는 순간 완료 일시를 기록하고,
     * 100 미만으로 다시 바뀌면 완료 일시를 비웁니다.
     * 값 검증(0/25/50/75/100 인지)은 서비스 계층에서 먼저 확인한 뒤 호출합니다.
     */
    public void updateProgress(int progressRate) {
        this.progressRate = progressRate;
        this.completedAt = (progressRate == 100) ? LocalDateTime.now() : null;
    }

    public boolean isCompleted() {
        return this.progressRate == 100;
    }

    /** 본인 소유 확인(REQ-NF-003 관련)용 — study_plan 을 거쳐 회원 id 를 얻습니다. */
    public Long getMemberId() {
        return this.studyPlan.getMemberId();
    }
}
