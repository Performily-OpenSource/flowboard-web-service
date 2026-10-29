package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.RenamePositionCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.RenamePositionResource;

/**
 * RenamePosition Command From Resource Assembler
 * @summary
 * Assembler to convert a RenamePositionResource to a RenamePositionCommand.
 *
 * @since 1.0.0
 */
public class RenamePositionCommandFromResourceAssembler {
    /**
     * Converts a {@link RenamePositionResource} to a {@link RenamePositionCommand}.
     *
     * @param positionId the position id
     * @param resource the {@link RenamePositionResource} instance
     * @return the {@link RenamePositionCommand}
     */
    public static RenamePositionCommand toCommandFromResource(Long positionId, RenamePositionResource resource) {
        return new RenamePositionCommand(positionId, resource.title());
    }
}
