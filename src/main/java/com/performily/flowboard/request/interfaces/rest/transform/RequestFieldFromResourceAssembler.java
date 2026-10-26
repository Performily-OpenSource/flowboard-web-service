package com.performily.flowboard.request.interfaces.rest.transform;

import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.valueobjects.FieldDataType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.interfaces.rest.resources.CreateRequestFieldResource;

import java.util.List;

/**
 * Request Field From Resource Assembler
 * @summary
 * Assembler to convert the fields sent by the client to RequestField entities.
 * It is shared by the create and update request type assemblers.
 *
 * @since 1.0.0
 */
public final class RequestFieldFromResourceAssembler {
    private RequestFieldFromResourceAssembler() {
    }

    /**
     * Converts a {@link CreateRequestFieldResource} to a new {@link RequestField}.
     *
     * @param resource the {@link CreateRequestFieldResource} instance
     * @return the {@link RequestField}, without id
     */
    public static RequestField toEntityFromResource(CreateRequestFieldResource resource) {
        return new RequestField(
                new FieldKey(resource.key()),
                resource.label(),
                RequestEnumAssembler.toEnum(FieldDataType.class, resource.dataType(), null, "dataType"),
                Boolean.TRUE.equals(resource.required()),
                resource.displayOrder() == null ? 0 : resource.displayOrder());
    }

    /**
     * Converts a list of field resources.
     *
     * @param resources the resources, may be null
     * @return the fields, or null when the list is null
     */
    public static List<RequestField> toEntitiesFromResources(List<CreateRequestFieldResource> resources) {
        return resources == null
                ? null
                : resources.stream().map(RequestFieldFromResourceAssembler::toEntityFromResource).toList();
    }
}
