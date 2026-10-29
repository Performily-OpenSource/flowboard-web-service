package com.performily.flowboard.workspace.application.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.workspace.domain.model.commands.ActivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.ChangePositionAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.CreatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.RenamePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.UpdatePositionReferenceSalaryCommand;
import com.performily.flowboard.workspace.domain.model.entities.Position;

/**
 * Position Command Service
 * @summary
 * Application service contract for commands over the {@link Position} entity.
 *
 * @since 1.0.0
 */
public interface PositionCommandService {
    /**
     * Handles position creation.
     *
     * @param command command containing the position data
     * @return created position identifier or an application error
     */
    Result<Long, ApplicationError> handle(CreatePositionCommand command);

    /**
     * Handles the reference salary update.
     *
     * @param command command containing the position id and the new salary
     * @return updated position or an application error
     */
    Result<Position, ApplicationError> handle(UpdatePositionReferenceSalaryCommand command);

    /**
     * Handles position deactivation.
     *
     * @param command command containing the position id
     * @return updated position or an application error
     */
    Result<Position, ApplicationError> handle(DeactivatePositionCommand command);

    /**
     * Handles position activation.
     *
     * @param command command containing the position id
     * @return updated position or an application error
     */
    Result<Position, ApplicationError> handle(ActivatePositionCommand command);

    /**
     * Handles the position rename.
     *
     * @param command command containing the position id and the new title
     * @return updated position or an application error
     */
    Result<Position, ApplicationError> handle(RenamePositionCommand command);

    /**
     * Handles the move of a position to another area.
     *
     * @param command command containing the position id and the new area id
     * @return updated position or an application error
     */
    Result<Position, ApplicationError> handle(ChangePositionAreaCommand command);
}
