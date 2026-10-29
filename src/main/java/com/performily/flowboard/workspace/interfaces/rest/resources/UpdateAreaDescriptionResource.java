package com.performily.flowboard.workspace.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Update Area Description Resource
 * @summary
 * Request payload for updating the description of an area
 *
 * @since 1.0.0
 */
@Schema(name = "UpdateAreaDescriptionRequest", description = "Request payload for updating the description of an area")
public record UpdateAreaDescriptionResource(
        @Size(max = 255)
        @Schema(description = "New area description. Blank clears it", example = "Gestión del talento y planillas", maxLength = 255)
        String description
) {
}
