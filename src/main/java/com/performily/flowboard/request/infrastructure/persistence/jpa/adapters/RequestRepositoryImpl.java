package com.performily.flowboard.request.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers.RequestPersistenceAssembler;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.repositories.RequestPersistenceRepository;
import com.performily.flowboard.request.infrastructure.persistence.jpa.repositories.RequestTypePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Request Repository Impl
 * @summary
 * Repository adapter that bridges the request domain repository port with Spring Data JPA.
 *
 * Also acts as the event-publishing boundary: after the {@link Request} is persisted,
 * the domain events registered by the aggregate are dispatched via Spring's
 * {@link ApplicationEventPublisher}. For a new request, a RequestSubmittedEvent is
 * added once the JPA-assigned id is available.
 *
 * Methods run inside a transaction so the lazy collections (form values,
 * attachments, history and the fields of the type) can be read while assembling
 * the domain object. The read methods use read-only transactions, so Hibernate
 * does not track changes of the loaded entities.
 *
 * An existing request is saved by updating its managed entity instead of
 * rebuilding it, so only the data that changed is written.
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class RequestRepositoryImpl implements RequestRepository {
    private static final List<RequestStatus> UNRESOLVED_STATUSES =
            List.of(RequestStatus.IN_PROGRESS, RequestStatus.UNDER_REVIEW);
    private static final List<RequestStatus> OCCUPYING_STATUSES =
            List.of(RequestStatus.IN_PROGRESS, RequestStatus.UNDER_REVIEW, RequestStatus.APPROVED);

    private final RequestPersistenceRepository requestPersistenceRepository;
    private final RequestTypePersistenceRepository requestTypePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param requestPersistenceRepository the {@link RequestPersistenceRepository} instance
     * @param requestTypePersistenceRepository the {@link RequestTypePersistenceRepository} instance
     * @param eventPublisher the {@link ApplicationEventPublisher} instance
     */
    public RequestRepositoryImpl(RequestPersistenceRepository requestPersistenceRepository,
                                 RequestTypePersistenceRepository requestTypePersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.requestPersistenceRepository = requestPersistenceRepository;
        this.requestTypePersistenceRepository = requestTypePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findById(Long id) {
        return requestPersistenceRepository.findById(id).map(RequestPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAll(Long requesterId, RequestStatus status, Long requestTypeId) {
        return toDomain(requestPersistenceRepository.findAllByFilters(requesterId, status, requestTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllUnresolvedByRequesterId(Long requesterId) {
        return toDomain(requestPersistenceRepository.findAllByRequesterIdAndStatusIn(requesterId, UNRESOLVED_STATUSES));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllUnresolvedByApproverEmployeeId(Long approverId) {
        return toDomain(requestPersistenceRepository.findAllByApproverEmployeeIdAndStatusIn(
                ApproverType.DIRECT_MANAGER, approverId, UNRESOLVED_STATUSES));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllPendingByApproverEmployeeId(Long approverId, Long requestTypeId) {
        return toDomain(requestPersistenceRepository.findAllByApprover(
                ApproverType.DIRECT_MANAGER, approverId, RequestStatus.IN_PROGRESS, requestTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllPendingForHrStaff(Long requestTypeId) {
        return toDomain(requestPersistenceRepository.findAllByApprover(
                ApproverType.HR_STAFF, null, RequestStatus.IN_PROGRESS, requestTypeId));
    }

    @Override
    public Request save(Request request) {
        boolean isNew = request.getId() == null;
        var pendingEvents = new ArrayList<>(request.domainEvents());
        request.clearDomainEvents();

        var entity = isNew
                ? RequestPersistenceAssembler.toPersistenceFromDomain(request,
                        requestTypePersistenceRepository.getReferenceById(request.getRequestType().getId()))
                : updateManagedEntity(request);
        var savedEntity = requestPersistenceRepository.saveAndFlush(entity);
        var savedRequest = RequestPersistenceAssembler.toDomainFromPersistence(savedEntity);

        if (isNew) {
            savedRequest.onSubmitted();
            pendingEvents.addAll(savedRequest.domainEvents());
            savedRequest.clearDomainEvents();
        }
        pendingEvents.forEach(eventPublisher::publishEvent);
        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOverlappingRequest(Long requesterId, LocalDate startDate, LocalDate endDate, Long excludedRequestId) {
        return requestPersistenceRepository.countOverlapping(
                requesterId, OCCUPYING_STATUSES, startDate, endDate, excludedRequestId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRequestTypeId(Long requestTypeId) {
        return requestPersistenceRepository.existsByRequestTypeId(requestTypeId);
    }

    /**
     * Loads the managed entity of an existing request and copies the domain state over it.
     *
     * @param request the {@link Request} instance with id
     * @return the updated managed entity
     * @throws IllegalArgumentException if the request does not exist
     */
    private RequestPersistenceEntity updateManagedEntity(Request request) {
        var entity = requestPersistenceRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Request %d not found".formatted(request.getId())));
        RequestPersistenceAssembler.updatePersistenceFromDomain(request, entity);
        return entity;
    }

    private static List<Request> toDomain(List<RequestPersistenceEntity> entities) {
        return entities.stream().map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
    }
}
