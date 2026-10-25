package com.performily.flowboard.request.domain.repositories;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Request Repository
 * @summary
 * Request repository port.
 *
 * @since 1.0.0
 */
public interface RequestRepository {
    /**
     * Finds the request with the given id, with its history.
     *
     * @param id the id
     * @return the request, if found
     */
    Optional<Request> findById(Long id);

    /**
     * Finds the requests that match the optional filters, newest first.
     *
     * @param requesterId   the requester filter, or null for every employee
     * @param status        the status filter, or null for all
     * @param requestTypeId the request type filter, or null for all
     * @return the list of requests
     */
    List<Request> findAll(Long requesterId, RequestStatus status, Long requestTypeId);

    /**
     * Finds the requests of an employee that are not resolved yet
     * (IN_PROGRESS or UNDER_REVIEW).
     *
     * @param requesterId the requester id
     * @return the list of requests
     */
    List<Request> findAllUnresolvedByRequesterId(Long requesterId);

    /**
     * Finds the requests assigned to a direct manager that are not resolved yet
     * (IN_PROGRESS or UNDER_REVIEW).
     *
     * @param approverId the approver id
     * @return the list of requests
     */
    List<Request> findAllUnresolvedByApproverEmployeeId(Long approverId);

    /**
     * Finds the IN_PROGRESS requests assigned to a direct manager, oldest first.
     *
     * @param approverId    the approver id
     * @param requestTypeId the request type filter, or null for all
     * @return the list of requests
     */
    List<Request> findAllPendingByApproverEmployeeId(Long approverId, Long requestTypeId);

    /**
     * Finds the IN_PROGRESS requests routed to the HR staff, oldest first.
     *
     * @param requestTypeId the request type filter, or null for all
     * @return the list of requests
     */
    List<Request> findAllPendingForHrStaff(Long requestTypeId);

    /**
     * Saves the request and publishes its domain events.
     *
     * @param request the {@link Request} instance
     * @return the saved request
     */
    Request save(Request request);

    /**
     * Checks whether the employee already has a request IN_PROGRESS, UNDER_REVIEW or
     * APPROVED whose period overlaps the given dates.
     *
     * @param requesterId       the requester id
     * @param startDate         the start date
     * @param endDate           the end date
     * @param excludedRequestId a request left out of the check (the one being resubmitted), or null
     * @return true if there is an overlapping request
     */
    boolean existsOverlappingRequest(Long requesterId, LocalDate startDate, LocalDate endDate, Long excludedRequestId);

    /**
     * Checks whether a request type already has requests.
     *
     * @param requestTypeId the request type id
     * @return true if it has
     */
    boolean existsByRequestTypeId(Long requestTypeId);
}
