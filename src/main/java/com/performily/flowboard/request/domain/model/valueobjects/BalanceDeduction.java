package com.performily.flowboard.request.domain.model.valueobjects;

/**
 * Balance Deduction
 * @summary
 * Indicates which balance a request type deducts when it is approved.
 * VACATION_DAYS deducts calendar days from the vacation balance, so it needs a
 * period of whole days. BENEFIT_BALANCE deducts an amount (for example a salary
 * advance) and does not need a period.
 * @since 1.0.0
 */
public enum BalanceDeduction {
    NONE,
    VACATION_DAYS,
    BENEFIT_BALANCE;

    /**
     * Checks whether the requests of this type must have a period of whole days.
     *
     * @return true only for VACATION_DAYS
     */
    public boolean requiresWholeDaysPeriod() {
        return this == VACATION_DAYS;
    }
}
