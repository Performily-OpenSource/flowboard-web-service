package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Update Area Description Command
 * @summary
 * Command to update the description of an area. A blank description clears it.
 *
 * @since 1.0.0
 */
public record UpdateAreaDescriptionCommand(
        Long areaId,
        String description) {
    /**
     * Compact constructor for UpdateAreaDescriptionCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public UpdateAreaDescriptionCommand {
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}
