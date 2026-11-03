package com.exacc.trabea.shift.dto;

import java.time.LocalTime;

public record ShiftResponse(Integer id, LocalTime startTime, LocalTime endTime) {}
