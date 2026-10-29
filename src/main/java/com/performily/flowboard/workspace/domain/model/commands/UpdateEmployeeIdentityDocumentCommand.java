package com.performily.flowboard.workspace.domain.model.commands;

import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;

/**
 * Update Employee Identity Document Command
 * @summary
 * Command to update the identity document of an employee.
 *
 * @since 1.0.0
 */
public record UpdateEmployeeIdentityDocumentCommand(
        Long employeeId,
        IdentityDocumentType identityDocumentType,
        String identityDocumentNumber) {
    /**
     * Compact constructor for UpdateEmployeeIdentityDocumentCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdateEmployeeIdentityDocumentCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (identityDocumentType == null) {
            throw new IllegalArgumentException("identityDocumentType cannot be null");
        }
        if (identityDocumentNumber == null || identityDocumentNumber.isBlank()) {
            throw new IllegalArgumentException("identityDocumentNumber cannot be null or blank");
        }
    }
}
