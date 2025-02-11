package com.archisemtle.semtlewebserverspring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @Column(name = "applicant_id", nullable = false)
    private Integer applicantId; // 지원자 ID

    @ManyToOne // ManyToOne 관계 설정
    @JoinColumn(name = "applicant_id", referencedColumnName = "applicantId", insertable = false, updatable = false) // 외래 키 설정
    private Applicants applicant; // 지원자와의 관계

    @Column(nullable = false)
    private String projectTitle; // 프로젝트 제목

    @Column(nullable = false)
    private Integer boardId; // 게시판 ID

    @Column(nullable = false)
    private LocalDateTime applyDate; // 지원 날짜

    private String status; // 지원 상태

    private String projectType; // 프로젝트 유형

    private String relateField; // 관련 분야

    @Builder
    public Application(String projectTitle, Integer boardId, LocalDateTime applyDate, String status,
        String projectType, String relateField) {
        this.projectTitle = projectTitle;
        this.boardId = boardId;
        this.applyDate = applyDate;
        this.status = status;
        this.projectType = projectType;
        this.relateField = relateField;
    }
}
