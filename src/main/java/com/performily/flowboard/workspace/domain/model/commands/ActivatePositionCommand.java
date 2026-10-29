package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Activate Position Command
 * @summary
 * Command to activate an inactive position.
 *
 * @since 1.0.0
 */
public record ActivatePositionCommand(Long positionId) {
    /**
     * Compact constructor for ActivatePositionCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public ActivatePositionCommand {
        if (positionId == null || positionId <= 0) {
            throw new IllegalArgumentException("positionId cannot be null or less than 1");
        }
    }
}
