package com.performily.flowboard.payroll.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update Payslip Resource
 * @summary
 * Full state of a payslip sent by the client. The server applies the needed transitions
 * (replace file, publish, mark as paid or observed). Unknown fields are ignored.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "UpdatePayslipRequest", description = "Request payload with the full state of a payslip")
public record UpdatePayslipResource(
        @NotBlank(message = "{validation.not-blank}") @Size(max = 255)
        @Schema(description = "File name", example = "boleta-2026-08.pdf")
        String fileName,
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Content type. Only PDF is accepted", example = "application/pdf")
        String contentType,
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "File size in bytes, up to 5 MB", example = "245760")
        Long sizeBytes,
        @NotBlank(message = "{validation.not-blank}") @Size(max = 7_500_000)
        @Schema(description = "Storage URL or data URL (base64) of the PDF")
        String storageUrl,
        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Issue date", example = "2026-08-31")
        LocalDate issueDate,
        @NotNull(message = "{validation.not-null}") @PositiveOrZero
        @Schema(description = "Net amount", example = "3450.00")
        BigDecimal netAmount,
        @Schema(description = "Currency (ISO 4217). Default PEN", example = "PEN")
        String netCurrency,
        @NotNull(message = "{validation.not-null}")
        @Pattern(regexp = "UNDER_REVIEW|PUBLISHED", message = "{validation.invalid-value}")
        @Schema(description = "Publication status", example = "PUBLISHED", allowableValues = {"UNDER_REVIEW", "PUBLISHED"})
        String publicationStatus,
        @NotNull(message = "{validation.not-null}")
        @Pattern(regexp = "PENDING|PAID|OBSERVED", message = "{validation.invalid-value}")
        @Schema(description = "Payment status", example = "PAID", allowableValues = {"PENDING", "PAID", "OBSERVED"})
        String paymentStatus,
        @Schema(description = "Payment date, required when PAID", example = "2026-09-01")
        LocalDate paidOn,
        @Size(max = 500)
        @Schema(description = "Observation reason, required when OBSERVED", example = "Cuenta bancaria inválida")
        String observationReason
) {
}
