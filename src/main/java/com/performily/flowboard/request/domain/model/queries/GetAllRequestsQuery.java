package com.performily.flowboard.request.domain.model.queries;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;

/**
 * Get All Requests Query
 * @summary
 * Query to get the requests of every employee, newest first. Used by the HR staff
 * inbox, which shows pending, under review and resolved requests. All the filters
 * are optional: requesterId, status and requestTypeId.
 *
 * @since 1.0.0
 */
public record GetAllRequestsQuery(Long requesterId, RequestStatus status, Long requestTypeId) {
}
