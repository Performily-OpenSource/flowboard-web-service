package com.performily.flowboard.request.domain.model.entities;

import com.performily.flowboard.request.domain.model.valueobjects.BalanceDeduction;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Request Type Entity
 * @summary
 * Type of request defined by the organization (vacations, medical leave,
 * permission, certificate...). It defines the fields of the form, whether an
 * attachment is required and whether it deducts a balance. Adding a new type
 * does not need changes in the backend.
 *
 * Rules: the name is unique (checked by the application service). A type that
 * already has requests is deactivated, never deleted, and its fields cannot be
 * removed nor change their data type.
 *
 * @since 1.0.0
 */
public class RequestType {
    private static final int NAME_MAX_LENGTH = 80;
    private static final int DESCRIPTION_MAX_LENGTH = 255;

    private Long id;
    private String name;
    private String description;
    private boolean requiresAttachment;
    private BalanceDeduction balanceDeduction;
    private boolean active;
    private final List<RequestField> fields;

    /**
     * Creates a new active request type without fields.
     *
     * @param name               the name
     * @param description        the description, optional
     * @param requiresAttachment whether an attachment is required
     * @param balanceDeduction   the balance it deducts
     */
    public RequestType(String name, String description, boolean requiresAttachment, BalanceDeduction balanceDeduction) {
        this(null, name, description, requiresAttachment, balanceDeduction, true, List.of());
    }

    /**
     * Rebuilds an existing request type. Used by the persistence assemblers.
     */
    public RequestType(Long id, String name, String description, boolean requiresAttachment,
                       BalanceDeduction balanceDeduction, boolean active, List<RequestField> fields) {
        this.id = id;
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.requiresAttachment = requiresAttachment;
        this.balanceDeduction = balanceDeduction == null ? BalanceDeduction.NONE : balanceDeduction;
        this.active = active;
        this.fields = new ArrayList<>(fields == null ? List.of() : fields);
    }

    /**
     * Updates the general data of the type. The fields are managed with
     * {@link #addField(RequestField)} and {@link #removeField(Long)}.
     */
    public void update(String name, String description, boolean requiresAttachment, BalanceDeduction balanceDeduction) {
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.requiresAttachment = requiresAttachment;
        this.balanceDeduction = balanceDeduction == null ? BalanceDeduction.NONE : balanceDeduction;
    }

    /**
     * Adds a field to the form. The key cannot repeat inside the type.
     *
     * @param field the field
     */
    public void addField(RequestField field) {
        Objects.requireNonNull(field, "Field cannot be null");
        boolean repeated = fields.stream().anyMatch(existing -> existing.getKey().equals(field.getKey()));
        if (repeated) {
            throw new IllegalArgumentException("Field key '%s' already exists in this request type".formatted(field.getKey().value()));
        }
        fields.add(field);
    }

    /**
     * Removes a field from the form.
     *
     * @param fieldId the field id
     */
    public void removeField(Long fieldId) {
        boolean removed = fields.removeIf(field -> field.getId() != null && field.getId().equals(fieldId));
        if (!removed) {
            throw new IllegalArgumentException("Field %d does not belong to this request type".formatted(fieldId));
        }
    }

    /**
     * Replaces the fields of the form with a new list, matching them by key: the
     * fields whose key already exists are updated (label, data type, required and
     * order) and keep their identifier, the new keys are added and the missing
     * keys are removed.
     *
     * When the type already has requests, its fields cannot be removed nor change
     * their data type, because the values saved in those requests depend on them.
     *
     * @param newFields the complete list of fields
     * @param inUse     whether the type already has requests
     * @throws IllegalArgumentException if a key is repeated
     * @throws IllegalStateException    if the type is in use and a field is removed or changes its data type
     */
    public void replaceFields(List<RequestField> newFields, boolean inUse) {
        var incoming = newFields == null ? List.<RequestField>of() : newFields;

        var incomingKeys = new HashSet<FieldKey>();
        for (var field : incoming) {
            Objects.requireNonNull(field, "Field cannot be null");
            if (!incomingKeys.add(field.getKey())) {
                throw new IllegalArgumentException("Field key '%s' is repeated".formatted(field.getKey().value()));
            }
        }

        if (inUse) {
            var removedKeys = fields.stream()
                    .map(RequestField::getKey)
                    .filter(key -> !incomingKeys.contains(key))
                    .map(FieldKey::value)
                    .toList();
            if (!removedKeys.isEmpty()) {
                throw new IllegalStateException("A request type with requests cannot lose fields: "
                        + String.join(", ", removedKeys));
            }
        }

        var result = new ArrayList<RequestField>();
        for (var field : incoming) {
            var existing = fields.stream().filter(current -> current.getKey().equals(field.getKey())).findFirst();
            if (existing.isEmpty()) {
                result.add(field);
                continue;
            }
            if (inUse && existing.get().getDataType() != field.getDataType()) {
                throw new IllegalStateException("Field '%s' cannot change its data type because the request type has requests"
                        .formatted(field.getKey().value()));
            }
            existing.get().update(field.getLabel(), field.getDataType(), field.isRequired(), field.getDisplayOrder());
            result.add(existing.get());
        }
        fields.clear();
        fields.addAll(result);
    }

    /**
     * Validates the values and attachments of a request against this type:
     * every required field has a value, there are no unknown keys, each value
     * matches the data type of its field, and the attachment exists when it is required.
     *
     * @param fieldValues the values entered by the requester
     * @param attachments the attachments
     * @throws IllegalArgumentException with the list of problems found
     */
    public void validate(List<FieldValue> fieldValues, List<FileReference> attachments) {
        var values = fieldValues == null ? List.<FieldValue>of() : fieldValues;
        var problems = new ArrayList<String>();

        var keys = new HashSet<String>();
        for (var value : values) {
            if (!keys.add(value.key().value())) {
                problems.add("field '%s' is repeated".formatted(value.key().value()));
            }
        }

        var missing = fields.stream()
                .filter(RequestField::isRequired)
                .filter(field -> values.stream().noneMatch(v -> v.key().equals(field.getKey()) && !v.isEmpty()))
                .map(field -> field.getKey().value())
                .toList();
        if (!missing.isEmpty()) {
            problems.add("missing required fields: " + String.join(", ", missing));
        }

        for (var value : values) {
            var field = fields.stream().filter(f -> f.getKey().equals(value.key())).findFirst();
            if (field.isEmpty()) {
                problems.add("field '%s' does not belong to this request type".formatted(value.key().value()));
            } else if (!value.isEmpty() && !field.get().accepts(value.value())) {
                problems.add("field '%s' must be of type %s".formatted(value.key().value(), field.get().getDataType()));
            }
        }

        if (requiresAttachment && (attachments == null || attachments.isEmpty())) {
            problems.add("this request type requires an attachment");
        }

        if (!problems.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", problems));
        }
    }

    /**
     * Checks whether the type deducts a balance.
     *
     * @return true when balanceDeduction is not NONE
     */
    public boolean deductsBalance() {
        return balanceDeduction != BalanceDeduction.NONE;
    }

    /**
     * Checks whether the requests of this type must have a period of whole days.
     * Only the types that deduct vacation days need it.
     *
     * @return true when the type deducts VACATION_DAYS
     */
    public boolean requiresWholeDaysPeriod() {
        return balanceDeduction.requiresWholeDaysPeriod();
    }

    /**
     * Deactivates the type. Inactive types cannot receive new requests.
     */
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("Request type is already inactive");
        }
        this.active = false;
    }

    /**
     * Activates the type.
     */
    public void activate() {
        if (active) {
            throw new IllegalStateException("Request type is already active");
        }
        this.active = true;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Request type name cannot be null or blank");
        }
        var trimmed = name.trim();
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Request type name cannot exceed %d characters".formatted(NAME_MAX_LENGTH));
        }
        return trimmed;
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        var trimmed = description.trim();
        if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Request type description cannot exceed %d characters".formatted(DESCRIPTION_MAX_LENGTH));
        }
        return trimmed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name; 
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiresAttachment() {
        return requiresAttachment;
    }

    public BalanceDeduction getBalanceDeduction() {
        return balanceDeduction;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Gets the fields ordered by display order.
     *
     * @return the fields
     */
    public List<RequestField> getFields() {
        return fields.stream().sorted(Comparator.comparingInt(RequestField::getDisplayOrder)).toList();
    }
}
