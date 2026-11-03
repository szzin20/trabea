package com.exacc.trabea.parttime.service;

import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.parttime.mapper.PartTimeEmployeeMapper;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PartTimeEmployeeServiceTest {
    private final PartTimeEmployeeRepository employees = mock(PartTimeEmployeeRepository.class);
    private final PartTimeEmployeeService service = new PartTimeEmployeeService(
            employees, null, null, null, null, mock(PartTimeEmployeeMapper.class));

    @Test
    void defaultsToFirstPageTenAndStableServerSort() {
        when(employees.search(eq(""), any())).thenReturn(new PageImpl<>(List.of()));

        service.findAll(0, 10, null);

        verify(employees).search(eq(""), eq(PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "id"))));
    }

    @Test
    void trimsNameBeforeSearching() {
        when(employees.search(anyString(), any())).thenReturn(new PageImpl<>(List.of()));

        service.findAll(0, 10, "  Anna Danis  ");

        verify(employees).search(eq("Anna Danis"), any());
    }

    @Test
    void rejectsInvalidPageAndSize() {
        assertEquals("Page harus minimal 0", assertThrows(BusinessRuleException.class,
                () -> service.findAll(-1, 10, null)).getMessage());
        assertEquals("Size harus antara 1 dan 100", assertThrows(BusinessRuleException.class,
                () -> service.findAll(0, 0, null)).getMessage());
        assertEquals("Size harus antara 1 dan 100", assertThrows(BusinessRuleException.class,
                () -> service.findAll(0, 101, null)).getMessage());
        verifyNoInteractions(employees);
    }

}
