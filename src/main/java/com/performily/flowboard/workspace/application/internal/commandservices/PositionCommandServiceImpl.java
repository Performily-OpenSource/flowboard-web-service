package com.performily.flowboard.workspace.application.internal.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.Money;
import com.performily.flowboard.workspace.application.commandservices.PositionCommandService;
import com.performily.flowboard.workspace.domain.model.commands.ActivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.ChangePositionAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.CreatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.RenamePositionCommand;
import com.performily.flowboard.workspace.domain.model.commands.UpdatePositionReferenceSalaryCommand;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import com.performily.flowboard.workspace.domain.repositories.EmployeeRepository;
import com.performily.flowboard.workspace.domain.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Position Command Service Impl
 * @summary
 * Application service that executes position commands.
 *
 * @since 1.0.0
 */
@Service
public class PositionCommandServiceImpl implements PositionCommandService {
    private final PositionRepository positionRepository;
    private final AreaRepository areaRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor.
     *
     * @param positionRepository the {@link PositionRepository} instance
     * @param areaRepository the {@link AreaRepository} instance
     * @param employeeRepository the {@link EmployeeRepository} instance, used to check the use of a position
     */
    public PositionCommandServiceImpl(PositionRepository positionRepository, AreaRepository areaRepository,
                                      EmployeeRepository employeeRepository) {
        this.positionRepository = positionRepository;
        this.areaRepository = areaRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePositionCommand command) {
        var area = areaRepository.findById(command.areaId());
        if (area.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        if (!area.get().isActive())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "position-creation", "Positions can only be created in active areas"));
        if (positionRepository.existsByAreaIdAndTitle(command.areaId(), command.title().trim()))
            return Result.failure(ApplicationError.conflict("Position",
                    "Title '%s' already exists in the area".formatted(command.title().trim())));
        try {
            var referenceSalary = toMoney(command.referenceSalaryAmount(), command.referenceSalaryCurrency());
            var position = positionRepository.save(new Position(command.title(), area.get(), referenceSalary));
            return Result.success(position.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("position", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-position", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(UpdatePositionReferenceSalaryCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        try {
            position.updateReferenceSalary(toMoney(command.referenceSalaryAmount(), command.referenceSalaryCurrency()));
            return Result.success(positionRepository.save(position));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("position", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-position-reference-salary", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(RenamePositionCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        var title = command.title().trim();
        if (!title.equals(position.getTitle())
                && positionRepository.existsByAreaIdAndTitle(position.getArea().getId(), title))
            return Result.failure(ApplicationError.conflict("Position",
                    "Title '%s' already exists in the area".formatted(title)));
        try {
            position.rename(title);
            return Result.success(positionRepository.save(position));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("position", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("rename-position", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(ChangePositionAreaCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        if (command.areaId().equals(position.getArea().getId()))
            return Result.success(position);
        var area = areaRepository.findById(command.areaId());
        if (area.isEmpty())
            return Result.failure(ApplicationError.notFound("Area", command.areaId().toString()));
        if (!area.get().isActive())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "position-area-change", "A position can only be moved to an active area"));
        if (employeeRepository.existsByJobAssignmentPositionId(command.positionId()))
            return Result.failure(ApplicationError.businessRuleViolation(
                    "position-area-change", "A position held by employees cannot be moved to another area"));
        if (positionRepository.existsByAreaIdAndTitle(command.areaId(), position.getTitle()))
            return Result.failure(ApplicationError.conflict("Position",
                    "Title '%s' already exists in the area".formatted(position.getTitle())));
        try {
            position.changeArea(area.get());
            return Result.success(positionRepository.save(position));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("change-position-area", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(ActivatePositionCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        if (!position.getArea().isActive())
            return Result.failure(ApplicationError.businessRuleViolation(
                    "position-activation", "A position of an inactive area cannot be activated"));
        try {
            position.activate();
            return Result.success(positionRepository.save(position));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("position-activation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("activate-position", e.getMessage()));
        }
    }

    @Override
    public Result<Position, ApplicationError> handle(DeactivatePositionCommand command) {
        var result = positionRepository.findById(command.positionId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Position", command.positionId().toString()));
        var position = result.get();
        try {
            position.deactivate();
            return Result.success(positionRepository.save(position));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("position-deactivation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("deactivate-position", e.getMessage()));
        }
    }

    private static Money toMoney(BigDecimal amount, String currencyCode) {
        var currency = (currencyCode == null || currencyCode.isBlank())
                ? Money.DEFAULT_CURRENCY
                : Currency.getInstance(currencyCode.trim().toUpperCase(Locale.ROOT));
        return new Money(amount, currency);
    }
}