package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Update Employee Identity Document Resource
 * @summary
 * Request payload for updating the identity document of an employee
 *
 * @since 1.0.0
 */
@Schema(name = "UpdateEmployeeIdentityDocumentRequest", description = "Request payload for updating the identity document of an employee")
public record UpdateEmployeeIdentityDocumentResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Identity document type", example = "CE", allowableValues = {"DNI", "CE", "PASSPORT"})
        String identityDocumentType,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Identity document number", example = "001234567")
        String identityDocumentNumber
) {
}
