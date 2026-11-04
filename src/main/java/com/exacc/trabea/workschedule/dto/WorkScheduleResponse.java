package com.exacc.trabea.workschedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkScheduleResponse(Integer id, Integer partTimeEmployeeId, String firstName, String lastName,
                                   LocalDate workDate, Integer workShiftId, LocalTime startTime,
                                   LocalTime endTime, Boolean isApproved) {}
