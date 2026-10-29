package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Change Position Area Resource
 * @summary
 * Request payload for moving a position to another area
 *
 * @since 1.0.0
 */
@Schema(name = "ChangePositionAreaRequest", description = "Request payload for moving a position to another area")
public record ChangePositionAreaResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "New area identifier", example = "2")
        Long areaId
) {
}
