package com.example.project_checklist.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * 임시 최소 버전입니다.
 *
 * 회원 도메인(회원가입/로그인 담당)의 실제 Member 엔티티로 교체될 예정입니다.
 * study_plan_item 조회/체크 기능을 독립적으로 개발·테스트하려면 study_plan -> member
 * 연관관계가 컴파일되어야 해서, id 만 있는 최소 버전으로 우선 만들어 둡니다.
 * 실제 Member 엔티티가 머지되면 이 파일은 삭제하고 import 경로만 그쪽으로 맞추면 됩니다.
 */
@Entity
@Table(name = "member")
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
