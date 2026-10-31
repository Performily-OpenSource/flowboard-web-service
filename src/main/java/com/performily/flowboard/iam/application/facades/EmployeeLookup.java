package com.performily.flowboard.iam.application.facades;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLookup {

    private final JdbcTemplate jdbc;

    public EmployeeLookup(JdbcTemplate j) {
        jdbc = j;
    }

    public boolean existsActive(Long id) {
        return Boolean.TRUE.equals(
            jdbc.queryForObject(
                "SELECT COUNT(*) > 0 FROM employees WHERE id = ? AND status = 'ACTIVE'",
                Boolean.class,
                id
            )
        );
    }
}