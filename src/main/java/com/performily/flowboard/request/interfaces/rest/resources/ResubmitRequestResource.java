package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Resubmit Request Resource
 * @summary
 * Resource for sending again a request that was returned for review. The period,
 * values and attachments replace the previous ones.
 *
 * @since 1.0.0
 */
@Schema(name = "ResubmitRequestRequest", description = "Request payload for resubmitting a request")
public record ResubmitRequestResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Requester that resubmits the request", example = "3")
        Long actorId,

        @Schema(description = "Corrected start date", example = "2026-10-12")
        LocalDate startDate,

        @Schema(description = "Corrected end date", example = "2026-10-16")
        LocalDate endDate,

        @Schema(description = "Corrected start time, only for permissions of hours", example = "09:00", type = "string")
        LocalTime startTime,

        @Schema(description = "Corrected end time, only for permissions of hours", example = "11:00", type = "string")
        LocalTime endTime,

        @Valid
        @Schema(description = "Values of the dynamic form")
        List<FieldValueResource> fieldValues,

        @Valid
        @Schema(description = "Attachments")
        List<RequestAttachmentResource> attachments,

        @Size(max = 500)
        @Schema(description = "Optional comment for the approver", example = "Adjunté el documento solicitado", maxLength = 500)
        String comment
) {
}
