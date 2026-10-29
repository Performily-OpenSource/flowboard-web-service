package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Rename Position Command
 * @summary
 * Command to rename a position.
 *
 * @since 1.0.0
 */
public record RenamePositionCommand(
        Long positionId,
        String title) {
    /**
     * Compact constructor for RenamePositionCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RenamePositionCommand {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
    }
}
