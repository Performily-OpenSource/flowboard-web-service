package com.performily.flowboard.request.infrastructure.seeding;

import com.performily.flowboard.request.application.commandservices.RequestTypeCommandService;
import com.performily.flowboard.request.application.queryservices.RequestTypeQueryService;
import com.performily.flowboard.request.domain.model.commands.CreateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.commands.DeactivateRequestTypeCommand;
import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.queries.GetAllRequestTypesQuery;
import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;
import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.performily.flowboard.request.domain.model.valueobjects.FieldDataType.*;

/**
 * Request Type Catalog Seeder
 * @summary
 * Creates the default catalog of request types when the request_types table is
 * empty, so the web and mobile applications have types to work with from the first
 * start. They are the same nine types of the fake API used by the frontend.
 *
 * The types are created through the {@link RequestTypeCommandService}, so they pass
 * the same validations as the ones created by the HR staff. If the table already has
 * types nothing is done, and any failure is logged: seeding must never stop the
 * application from starting.
 *
 * @since 1.0.0
 */
@Component
public class RequestTypeCatalogSeeder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTypeCatalogSeeder.class);

    private final RequestTypeCommandService requestTypeCommandService;
    private final RequestTypeQueryService requestTypeQueryService;

    /**
     * Constructor.
     *
     * @param requestTypeCommandService the {@link RequestTypeCommandService} instance
     * @param requestTypeQueryService the {@link RequestTypeQueryService} instance
     */
    public RequestTypeCatalogSeeder(RequestTypeCommandService requestTypeCommandService,
                                    RequestTypeQueryService requestTypeQueryService) {
        this.requestTypeCommandService = requestTypeCommandService;
        this.requestTypeQueryService = requestTypeQueryService;
    }

    /**
     * Creates the default request types if there are none.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultRequestTypes() {
        try {
            if (!requestTypeQueryService.handle(new GetAllRequestTypesQuery(false)).isEmpty()) {
                return;
            }
            int created = 0;
            for (var seed : defaultCatalog()) {
                var result = requestTypeCommandService.handle(seed.command());
                if (result instanceof Result.Success<Long, ?> success) {
                    created++;
                    if (!seed.active()) {
                        requestTypeCommandService.handle(new DeactivateRequestTypeCommand(success.value()));
                    }
                } else {
                    LOGGER.warn("Default request type '{}' was not created: {}", seed.command().name(), result);
                }
            }
            LOGGER.info("Default request type catalog seeded with {} types", created);
        } catch (RuntimeException exception) {
            LOGGER.warn("Default request types could not be seeded at startup: {}", exception.getMessage(), exception);
        }
    }

    private static List<SeedType> defaultCatalog() {
        return List.of(
                type("Vacations", "Paid vacation days, deducted from the vacation balance.",
                        false, BalanceDeduction.VACATION_DAYS, true,
                        field("startDate", "Start date", DATE, true, 1),
                        field("endDate", "End date", DATE, true, 2),
                        field("comment", "Comment", TEXT, false, 3)),
                type("Paid leave", "Leave with pay supported by a document (marriage, bereavement, paternity...).",
                        true, BalanceDeduction.NONE, true,
                        field("startDate", "Start date", DATE, true, 1),
                        field("endDate", "End date", DATE, true, 2),
                        field("reason", "Reason", TEXT, true, 3),
                        field("comment", "Comment", TEXT, false, 4)),
                type("Unpaid leave", "Leave without pay for personal reasons.",
                        true, BalanceDeduction.NONE, true,
                        field("startDate", "Start date", DATE, true, 1),
                        field("endDate", "End date", DATE, true, 2),
                        field("reason", "Reason", TEXT, true, 3),
                        field("comment", "Comment", TEXT, false, 4)),
                type("Permission by hours", "Absence of a few hours within the same day.",
                        false, BalanceDeduction.NONE, true,
                        field("startDate", "Date", DATE, true, 1),
                        field("startTime", "Start time", TIME, true, 2),
                        field("endTime", "End time", TIME, true, 3),
                        field("reason", "Reason", TEXT, true, 4)),
                type("Medical rest", "Rest days supported by a medical certificate.",
                        true, BalanceDeduction.NONE, true,
                        field("startDate", "Start date", DATE, true, 1),
                        field("endDate", "End date", DATE, true, 2),
                        field("comment", "Comment", TEXT, false, 3)),
                type("Absence justification", "Justifies a day already missed.",
                        true, BalanceDeduction.NONE, true,
                        field("startDate", "Absence date", DATE, true, 1),
                        field("reason", "Reason", TEXT, true, 2)),
                type("Salary advance", "Advance of part of the monthly salary, deducted from the advance benefit.",
                        false, BalanceDeduction.BENEFIT_BALANCE, true,
                        field("amount", "Amount (PEN)", MONEY, true, 1),
                        field("payrollMonth", "Payroll month", TEXT, true, 2)),
                type("Employment certificate", "Certificate that proves the current employment.",
                        false, BalanceDeduction.NONE, true,
                        field("purpose", "Purpose", TEXT, true, 1)),
                type("Payroll account change", "Change of the bank account where the salary is paid.",
                        true, BalanceDeduction.NONE, false,
                        field("bankName", "Bank", TEXT, true, 1),
                        field("accountNumber", "Account number", TEXT, true, 2)));
    }

    private static SeedType type(String name, String description, boolean requiresAttachment,
                                 BalanceDeduction balanceDeduction, boolean active, RequestField... fields) {
        return new SeedType(new CreateRequestTypeCommand(name, description, requiresAttachment, balanceDeduction,
                List.of(fields)), active);
    }

    private static RequestField field(String key, String label, FieldDataType dataType, boolean required, int displayOrder) {
        return new RequestField(new FieldKey(key), label, dataType, required, displayOrder);
    }

    /**
     * A default request type and whether it starts active.
     *
     * @param command the command that creates it
     * @param active  whether it stays active after being created
     */
    private record SeedType(CreateRequestTypeCommand command, boolean active) {
    }
}
