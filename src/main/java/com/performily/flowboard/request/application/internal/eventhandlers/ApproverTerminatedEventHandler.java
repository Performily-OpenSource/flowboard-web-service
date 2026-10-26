package com.performily.flowboard.request.application.internal.eventhandlers;

import com.performily.flowboard.request.domain.model.valueobjects.Approver;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.workspace.domain.model.events.EmployeeTerminatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Approver Terminated Event Handler
 * @summary
 * Listens to the EmployeeTerminatedEvent of Workspace and moves to the HR staff the
 * requests that the terminated employee had to approve as direct manager and are not
 * resolved yet. Without this, those requests would stay with someone who can no
 * longer resolve them.
 *
 * Each request is handled on its own: if one cannot be moved, the problem is logged
 * and the termination in Workspace is not undone.
 *
 * @since 1.0.0
 */
@Component
public class ApproverTerminatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApproverTerminatedEventHandler.class);

    private final RequestRepository requestRepository;

    /**
     * Constructor.
     *
     * @param requestRepository the {@link RequestRepository} instance
     */
    public ApproverTerminatedEventHandler(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Moves the pending approvals of the terminated employee to the HR staff.
     *
     * @param event the {@link EmployeeTerminatedEvent} instance
     */
    @EventListener
    public void on(EmployeeTerminatedEvent event) {
        requestRepository.findAllUnresolvedByApproverEmployeeId(event.employeeId()).forEach(request -> {
            try {
                request.reassignApprover(Approver.hrStaff());
                requestRepository.save(request);
            } catch (RuntimeException e) {
                LOGGER.warn("Request {} of terminated approver {} was not moved to the HR staff: {}",
                        request.getId(), event.employeeId(), e.getMessage());
            }
        });
    }
}
