package com.performily.flowboard.workspace.interfaces.rest.transform;

import com.performily.flowboard.workspace.domain.model.commands.UpdateEmployeeContractCommand;
import com.performily.flowboard.workspace.domain.model.valueobjects.ContractType;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdateEmployeeContractResource;

import java.util.Locale;

/**
 * UpdateEmployeeContract Command From Resource Assembler
 * @summary
 * Assembler to convert a UpdateEmployeeContractResource to a UpdateEmployeeContractCommand.
 *
 * @since 1.0.0
 */
public class UpdateEmployeeContractCommandFromResourceAssembler {
    /**
     * Converts a {@link UpdateEmployeeContractResource} to a {@link UpdateEmployeeContractCommand}.
     *
     * @param employeeId the employee id
     * @param resource the {@link UpdateEmployeeContractResource} instance
     * @return the {@link UpdateEmployeeContractCommand}
     */
    public static UpdateEmployeeContractCommand toCommandFromResource(Long employeeId, UpdateEmployeeContractResource resource) {
        return new UpdateEmployeeContractCommand(
                employeeId,
                ContractType.valueOf(resource.contractType().trim().toUpperCase(Locale.ROOT)),
                resource.contractEndDate());
    }
}
