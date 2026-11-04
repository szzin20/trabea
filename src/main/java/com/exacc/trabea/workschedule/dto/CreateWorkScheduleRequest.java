package com.exacc.trabea.workschedule.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateWorkScheduleRequest(
        @NotNull LocalDate workDate,
        @NotNull Integer workShiftId) {}
