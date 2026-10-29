package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Reactivate Employee Command
 * @summary
 * Command to reactivate a SUSPENDED employee.
 *
 * @since 1.0.0
 */
public record ReactivateEmployeeCommand(Long employeeId) {
    /**
     * Compact constructor for ReactivateEmployeeCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ReactivateEmployeeCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
    }
}
