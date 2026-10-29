package com.performily.flowboard.workspace.domain.model.commands;

/**
 * Remove Employee Document Command
 * @summary
 * Command to remove a document from an employee record.
 *
 * @since 1.0.0
 */
public record RemoveEmployeeDocumentCommand(
        Long employeeId,
        Long documentId) {
    /**
     * Compact constructor for RemoveEmployeeDocumentCommand.
     *
     * @throws IllegalArgumentException if a required value is missing
     */
    public RemoveEmployeeDocumentCommand {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId cannot be null or less than 1");
        }
        if (documentId == null || documentId <= 0) {
            throw new IllegalArgumentException("documentId cannot be null or less than 1");
        }
    }
}
