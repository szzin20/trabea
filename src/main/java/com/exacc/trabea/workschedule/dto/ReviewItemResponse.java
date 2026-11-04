package com.exacc.trabea.workschedule.dto;

import java.time.LocalDate;

public record ReviewItemResponse(Integer workScheduleId, Integer partTimerId, String fullName,
                                 LocalDate date, Integer shiftId, String status) {}
