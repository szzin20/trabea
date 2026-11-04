package com.exacc.trabea.workschedule.dto;

import java.time.LocalDate;

public record SchedulePageItem(Integer partTimerId, String fullName, LocalDate date, Integer shiftId) {}
