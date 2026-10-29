package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rename Position Resource
 * @summary
 * Request payload for renaming a position
 *
 * @since 1.0.0
 */
@Schema(name = "RenamePositionRequest", description = "Request payload for renaming a position")
public record RenamePositionResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 80)
        @Schema(description = "New position title", example = "Analista Senior de RR.HH.", maxLength = 80)
        String title
) {
}
