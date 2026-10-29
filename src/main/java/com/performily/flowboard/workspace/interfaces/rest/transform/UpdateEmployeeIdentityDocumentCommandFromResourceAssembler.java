package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.UpdateEmployeeIdentityDocumentCommand;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdateEmployeeIdentityDocumentResource;

import java.util.Locale;

/**
 * UpdateEmployeeIdentityDocument Command From Resource Assembler
 * @summary
 * Assembler to convert a UpdateEmployeeIdentityDocumentResource to a UpdateEmployeeIdentityDocumentCommand.
 *
 * @since 1.0.0
 */
public class UpdateEmployeeIdentityDocumentCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdateEmployeeIdentityDocumentResource} to a {@link UpdateEmployeeIdentityDocumentCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link UpdateEmployeeIdentityDocumentResource} instance
     * @return the {@link UpdateEmployeeIdentityDocumentCommand}
     */
    public static UpdateEmployeeIdentityDocumentCommand toCommandFromResource(Long employeeId, UpdateEmployeeIdentityDocumentResource resource) {
        return new UpdateEmployeeIdentityDocumentCommand(
                employeeId,
                IdentityDocumentType.valueOf(resource.identityDocumentType().trim().toUpperCase(Locale.ROOT)),
                resource.identityDocumentNumber());
    }
}
