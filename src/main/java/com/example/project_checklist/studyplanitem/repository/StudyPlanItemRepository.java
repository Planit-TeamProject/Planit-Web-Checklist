package com.example.project_checklist.studyplanitem.repository;

import com.example.project_checklist.studyplanitem.entity.StudyPlanItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

    /**
     * REQ-F-001, REQ-F-002: 특정 회원의 특정 날짜 학습 계획 리스트를
     * 하루 내 표시 순서(sort_order) 기준으로 조회합니다.
     */
    @Query("""
            select i from StudyPlanItem i
            where i.studyPlan.member.id = :memberId
              and i.planDate = :planDate
            order by i.sortOrder asc
            """)
    List<StudyPlanItem> findByMemberIdAndPlanDate(@Param("memberId") Long memberId,
                                                    @Param("planDate") LocalDate planDate);
}
