package com.performily.flowboard.workspace.domain.model.commands;

import com.performily.flowboard.workspace.domain.model.valueobjects.ContractType;

import java.time.LocalDate;

/**
 * Update Employee Contract Command
 * @summary
 * Command to update the contract type and the contract end date of an employee.
 *
 * @since 1.0.0
 */
public record UpdateEmployeeContractCommand(
        Long employeeId,
        ContractType contractType,
        LocalDate contractEndDate) {
    /**
     * Compact constructor for UpdateEmployeeContractCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdateEmployeeContractCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("contractType cannot be null");
        }
    }
}
