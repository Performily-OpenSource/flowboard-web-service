package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * Position Id Value Object
 * @summary
 * Shared Kernel identifier for the Position concept, owned by Workspace.
 *
 * Other bounded contexts use it to refer to a position without a direct
 * dependency on the Workspace model.
 *
 * @param value the identifier value. It cannot be null or less than 1.
 * @since 1.0.0
 */
public record PositionId(Long value) {
    /**
     * Compact constructor for PositionId.
     *
     * @throws IllegalArgumentException if the value is null or less than 1
     */
    public PositionId {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Position id cannot be null or less than 1");
        }
    }
}
