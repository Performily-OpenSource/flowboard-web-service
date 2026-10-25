package com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestFieldPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestTypePersistenceEntity;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Request Type Persistence Assembler
 * @summary
 * Static assembler between request type domain and persistence representations,
 * including the fields of the form.
 *
 * @since 1.0.0
 */
public final class RequestTypePersistenceAssembler {
    private RequestTypePersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link RequestTypePersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static RequestType toDomainFromPersistence(RequestTypePersistenceEntity entity) {
        if (entity == null) return null;
        return new RequestType(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isRequiresAttachment(),
                entity.getBalanceDeduction(),
                entity.isActive(),
                entity.getFields().stream().map(RequestTypePersistenceAssembler::toDomainFromPersistence).toList());
    }

    /**
     * Converts a new domain object to its persistence representation.
     *
     * @param requestType the {@link RequestType} instance, without id
     * @return the persistence entity, or null when the domain object is null
     */
    public static RequestTypePersistenceEntity toPersistenceFromDomain(RequestType requestType) {
        if (requestType == null) return null;
        var entity = new RequestTypePersistenceEntity();
        copyGeneralData(requestType, entity);
        requestType.getFields().forEach(field -> entity.getFields().add(toPersistenceFromDomain(field, entity)));
        return entity;
    }

    /**
     * Copies the state of a domain object over its persistence entity. The fields
     * are matched by id: the existing ones are updated, the ones that are no longer
     * in the domain are removed and the new ones (without id) are added. This way
     * Hibernate does not delete and insert again every field on each save.
     *
     * @param requestType the {@link RequestType} instance
     * @param entity      the {@link RequestTypePersistenceEntity} to update
     */
    public static void updatePersistenceFromDomain(RequestType requestType, RequestTypePersistenceEntity entity) {
        copyGeneralData(requestType, entity);

        var fields = requestType.getFields();
        var keptIds = fields.stream().map(RequestField::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        entity.getFields().removeIf(current -> !keptIds.contains(current.getId()));
        for (var field : fields) {
            if (field.getId() == null) {
                entity.getFields().add(toPersistenceFromDomain(field, entity));
                continue;
            }
            entity.getFields().stream()
                    .filter(current -> field.getId().equals(current.getId()))
                    .findFirst()
                    .ifPresent(current -> copyField(field, current));
        }
    }

    private static RequestField toDomainFromPersistence(RequestFieldPersistenceEntity entity) {
        return new RequestField(
                entity.getId(),
                new FieldKey(entity.getFieldKey()),
                entity.getLabel(),
                entity.getDataType(),
                entity.isRequired(),
                entity.getDisplayOrder());
    }

    private static RequestFieldPersistenceEntity toPersistenceFromDomain(RequestField field,
                                                                         RequestTypePersistenceEntity requestType) {
        var entity = new RequestFieldPersistenceEntity();
        entity.setRequestType(requestType);
        entity.setFieldKey(field.getKey().value());
        copyField(field, entity);
        return entity;
    }

    private static void copyGeneralData(RequestType requestType, RequestTypePersistenceEntity entity) {
        entity.setName(requestType.getName());
        entity.setDescription(requestType.getDescription());
        entity.setRequiresAttachment(requestType.isRequiresAttachment());
        entity.setBalanceDeduction(requestType.getBalanceDeduction());
        entity.setActive(requestType.isActive());
    }

    private static void copyField(RequestField field, RequestFieldPersistenceEntity entity) {
        entity.setLabel(field.getLabel());
        entity.setDataType(field.getDataType());
        entity.setRequired(field.isRequired());
        entity.setDisplayOrder(field.getDisplayOrder());
    }
}
