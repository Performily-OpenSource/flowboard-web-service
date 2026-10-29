package com.performily.flowboard.payroll.domain.model.queries;

import com.performily.flowboard.payroll.domain.model.valueobjects.PaymentStatus;

/**
 * Search Payslips Query
 * @summary
 * Query to list payslips (HR view). Both filters are optional: without them every payslip is returned.
 *
 * @param payrollPeriodId optional payroll period filter
 * @param paymentStatus   optional payment status filter
 * @since 1.0.0
 */
public record SearchPayslipsQuery(Long payrollPeriodId, PaymentStatus paymentStatus) {
    public SearchPayslipsQuery {
        if (payrollPeriodId != null && payrollPeriodId <= 0) {
            throw new IllegalArgumentException("payrollPeriodId cannot be less than 1");
        }
    }
}
