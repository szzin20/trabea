package com.exacc.trabea.shift.repository;

import com.exacc.trabea.shift.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findAllByOrderByIdAsc();
}
