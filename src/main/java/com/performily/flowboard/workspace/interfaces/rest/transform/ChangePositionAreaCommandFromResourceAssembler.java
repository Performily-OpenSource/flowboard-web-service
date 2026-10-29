package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.ChangePositionAreaCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.ChangePositionAreaResource;

/**
 * ChangePositionArea Command From Resource Assembler
 * @summary
 * Assembler to convert a ChangePositionAreaResource to a ChangePositionAreaCommand.
 *
 * @since 1.0.0
 */
public class ChangePositionAreaCommandFromResourceAssembler {
    /**
     * Converts a {@link ChangePositionAreaResource} to a {@link ChangePositionAreaCommand}.
     *
     * @param positionId the position id
     * @param resource the {@link ChangePositionAreaResource} instance
     * @return the {@link ChangePositionAreaCommand}
     */
    public static ChangePositionAreaCommand toCommandFromResource(Long positionId, ChangePositionAreaResource resource) {
        return new ChangePositionAreaCommand(positionId, resource.areaId());
    }
}
