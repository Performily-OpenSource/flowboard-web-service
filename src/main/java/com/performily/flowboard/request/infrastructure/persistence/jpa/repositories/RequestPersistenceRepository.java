package com.performily.flowboard.request.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Request Persistence Repository
 * @summary
 * Spring Data repository for request persistence entities.
 *
 * The list queries bring the request type in the same query (join fetch), and the
 * collections of each request (values, attachments, history) are loaded in batches
 * thanks to the BatchSize of the entity.
 *
 * @since 1.0.0
 */
@Repository
public interface RequestPersistenceRepository extends JpaRepository<RequestPersistenceEntity, Long> {
    /**
     * Finds the requests that match the optional filters, newest first.
     *
     * @param requesterId   the requester id, or null for every employee
     * @param status        the status, or null for all
     * @param requestTypeId the request type id, or null for all
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where (:requesterId is null or r.requesterId = :requesterId)
              and (:status is null or r.status = :status)
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            order by r.submittedAt desc, r.id desc
            """)
    List<RequestPersistenceEntity> findAllByFilters(@Param("requesterId") Long requesterId,
                                                    @Param("status") RequestStatus status,
                                                    @Param("requestTypeId") Long requestTypeId);

    /**
     * Finds the requests of an employee with one of the given statuses.
     *
     * @param requesterId the requester id
     * @param statuses    the statuses
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.requesterId = :requesterId
              and r.status in :statuses
            """)
    List<RequestPersistenceEntity> findAllByRequesterIdAndStatusIn(@Param("requesterId") Long requesterId,
                                                                   @Param("statuses") Collection<RequestStatus> statuses);

    /**
     * Finds the requests assigned to a direct manager with one of the given statuses.
     *
     * @param approverEmployeeId the approver employee id
     * @param statuses           the statuses
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.approverType = :approverType
              and r.approverEmployeeId = :approverEmployeeId
              and r.status in :statuses
            """)
    List<RequestPersistenceEntity> findAllByApproverEmployeeIdAndStatusIn(@Param("approverType") ApproverType approverType,
                                                                          @Param("approverEmployeeId") Long approverEmployeeId,
                                                                          @Param("statuses") Collection<RequestStatus> statuses);

    /**
     * Finds the requests with the given approver and status, oldest first.
     * The approver employee id and the request type are optional filters.
     *
     * @param approverType       the approver type
     * @param approverEmployeeId the approver employee id, or null
     * @param status             the status
     * @param requestTypeId      the request type id, or null
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.approverType = :approverType
              and (:approverEmployeeId is null or r.approverEmployeeId = :approverEmployeeId)
              and r.status = :status
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            order by r.submittedAt asc, r.id asc
            """)
    List<RequestPersistenceEntity> findAllByApprover(@Param("approverType") ApproverType approverType,
                                                     @Param("approverEmployeeId") Long approverEmployeeId,
                                                     @Param("status") RequestStatus status,
                                                     @Param("requestTypeId") Long requestTypeId);

    /**
     * Counts the requests of an employee with one of the given statuses whose
     * period shares at least one day with the given dates. One request can be left
     * out of the count (the one being resubmitted).
     *
     * @param requesterId       the requester id
     * @param statuses          the statuses to consider
     * @param startDate         the start date
     * @param endDate           the end date
     * @param excludedRequestId the request left out, or null
     * @return the number of overlapping requests
     */
    @Query("""
            select count(r) from RequestPersistenceEntity r
            where r.requesterId = :requesterId
              and r.status in :statuses
              and r.startDate is not null
              and r.startDate <= :endDate
              and r.endDate >= :startDate
              and (:excludedRequestId is null or r.id <> :excludedRequestId)
            """)
    long countOverlapping(@Param("requesterId") Long requesterId,
                          @Param("statuses") Collection<RequestStatus> statuses,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          @Param("excludedRequestId") Long excludedRequestId);

    /**
     * Checks whether a request type already has requests. Spring Data stops at the
     * first row it finds.
     *
     * @param requestTypeId the request type id
     * @return true if it has
     */
    boolean existsByRequestTypeId(Long requestTypeId);
}
