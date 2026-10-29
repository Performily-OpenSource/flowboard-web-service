package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Change Position Area Command
 * @summary
 * Command to move a position to another area.
 *
 * @since 1.0.0
 */
public record ChangePositionAreaCommand(
        Long positionId,
        Long areaId) {
    /**
     * Compact constructor for ChangePositionAreaCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ChangePositionAreaCommand {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
        if (areaId == null || areaId <= 0) {
            throw new IllegalArgumentException("areaId cannot be null or less than 1");
        }
    }
}
