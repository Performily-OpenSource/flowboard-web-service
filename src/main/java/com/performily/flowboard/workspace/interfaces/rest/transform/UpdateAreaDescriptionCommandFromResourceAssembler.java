package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.UpdateAreaDescriptionCommand;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdateAreaDescriptionResource;

/**
 * UpdateAreaDescription Command From Resource Assembler
 * @summary
 * Assembler to convert a UpdateAreaDescriptionResource to a UpdateAreaDescriptionCommand.
 *
 * @since 1.0.0
 */
public class UpdateAreaDescriptionCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdateAreaDescriptionResource} to a {@link UpdateAreaDescriptionCommand}.
     *
     * @param areaId the area id
     * @param resource the {@link UpdateAreaDescriptionResource} instance
     * @return the {@link UpdateAreaDescriptionCommand}
     */
    public static UpdateAreaDescriptionCommand toCommandFromResource(Long areaId, UpdateAreaDescriptionResource resource) {
        return new UpdateAreaDescriptionCommand(areaId, resource.description());
    }
}
