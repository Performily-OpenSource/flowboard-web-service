package com.performily.flowboard.payroll.domain.model.commands;

import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update Payslip Command
 * @summary
 * Command that brings a payslip to the state sent by the client: it replaces the file when the file,
 * issue date or net amount changed, publishes it when asked, and records the payment status.
 *
 * @since 1.0.0
 */
public record UpdatePayslipCommand(Long payslipId, String fileName, String contentType, Long sizeInBytes,
                                   String storageUrl, LocalDate issueDate, BigDecimal netAmount, String currency,
                                   PublicationStatus publicationStatus, PaymentStatus paymentStatus,
                                   LocalDate paidOn, String observationReason) {
    public UpdatePayslipCommand {
        if (payslipId == null || payslipId <= 0) {
            throw new IllegalArgumentException("payslipId cannot be null or less than 1");
        }
        if (sizeInBytes == null || issueDate == null || netAmount == null
                || publicationStatus == null || paymentStatus == null) {
            throw new IllegalArgumentException(
                    "sizeInBytes, issueDate, netAmount, publicationStatus and paymentStatus are required");
        }
    }
}
