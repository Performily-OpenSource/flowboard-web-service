package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Update Employee Contract Resource
 * @summary
 * Request payload for updating the contract of an employee
 *
 * @since 1.0.0
 */
@Schema(name = "UpdateEmployeeContractRequest", description = "Request payload for updating the contract of an employee")
public record UpdateEmployeeContractResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Contract type", example = "FIXED_TERM",
                allowableValues = {"INDEFINITE", "FIXED_TERM", "PART_TIME", "INTERNSHIP"})
        String contractType,

        @Schema(description = "Contract end date. Required for FIXED_TERM", example = "2027-06-30")
        LocalDate contractEndDate
) {
}
