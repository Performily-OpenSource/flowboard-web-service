package com.performily.flowboard.payroll.interfaces.rest.transform;

import com.performily.flowboard.payroll.domain.model.commands.UpdatePayslipCommand;
import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.interfaces.rest.resources.UpdatePayslipResource;

/**
 * Update Payslip Command From Resource Assembler
 * @summary
 * Converts an {@link UpdatePayslipResource} into an {@link UpdatePayslipCommand}.
 *
 * @since 1.0.0
 */
public class UpdatePayslipCommandFromResourceAssembler {
    public static UpdatePayslipCommand toCommandFromResource(Long payslipId, UpdatePayslipResource resource) {
        return new UpdatePayslipCommand(payslipId, resource.fileName(), resource.contentType(), resource.sizeBytes(),
                resource.storageUrl(), resource.issueDate(), resource.netAmount(), resource.netCurrency(),
                PublicationStatus.valueOf(resource.publicationStatus()),
                PaymentStatus.valueOf(resource.paymentStatus()),
                resource.paidOn(), resource.observationReason());
    }
}
