package com.example.project_checklist.studyplan.entity;

import com.example.project_checklist.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * 임시 최소 버전입니다.
 *
 * 학습 계획 입력 도메인(시험일/과목/우선순위/가용시간 담당)의 실제 StudyPlan 엔티티로
 * 교체될 예정입니다. study_plan_item(REQ-F-001~005, 유시우 담당)이 study_plan_id FK로
 * 참조해야 해서, id 와 member 연관관계만 있는 최소 버전으로 우선 만들어 둡니다.
 * 실제 StudyPlan 엔티티가 머지되면 이 파일은 삭제하고 import 경로만 그쪽으로 맞추면 됩니다.
 */
@Entity
@Table(name = "study_plan")
@Getter
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public Long getMemberId() {
        return member.getId();
    }
}
