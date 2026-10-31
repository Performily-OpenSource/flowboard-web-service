package com.performily.flowboard.iam.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "user_accounts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_iam_employee", columnNames = "employee_id"),
        @UniqueConstraint(name = "uk_iam_username", columnNames = "username")
    }
)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "employee_id", nullable = false)
    public Long employeeId;

    @Column(nullable = false, length = 255)
    public String username;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role = Role.EMPLOYEE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.ACTIVE;

    @Column(name = "must_change_password", nullable = false)
    public boolean mustChangePassword = true;

    @Column(name = "last_sign_in_at")
    public Instant lastSignInAt;

    public enum Role {
        HR_STAFF, EMPLOYEE
    }

    public enum Status {
        ACTIVE, DISABLED
    }
}