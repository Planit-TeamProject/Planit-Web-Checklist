package com.example.project_checklist.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BaseTimeEntity 의 @CreatedDate / @LastModifiedDate 자동 처리를 위한 설정.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
