package com.exacc.trabea.parttime.service;

import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.common.DuplicateResourceException;
import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.parttime.dto.PartTimeEmployeeRequest;
import com.exacc.trabea.parttime.dto.PartTimeEmployeeResponse;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import com.exacc.trabea.parttime.mapper.PartTimeEmployeeMapper;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.role.repository.RoleRepository;
import com.exacc.trabea.security.jwt.RoleAuthority;
import com.exacc.trabea.user.entity.User;
import com.exacc.trabea.user.entity.UserRole;
import com.exacc.trabea.user.entity.UserRoleId;
import com.exacc.trabea.user.repository.UserRepository;
import com.exacc.trabea.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PartTimeEmployeeService {
    private static final String DEFAULT_PASSWORD = "Trabea123";
    private static final String EMAIL_DOMAIN = "@trabea.co.id";
    private final PartTimeEmployeeRepository employees;
    private final UserRepository users;
    private final RoleRepository roles;
    private final UserRoleRepository userRoles;
    private final PasswordEncoder passwordEncoder;
    private final PartTimeEmployeeMapper mapper;


    @Transactional
    public PartTimeEmployeeResponse create(PartTimeEmployeeRequest request) {
        ensurePersonalContactsAvailable(request.personalEmail(), request.personalPhoneNumber(), null);
        String workEmail = slug(request.firstName()) + "." + slug(request.lastName()) + EMAIL_DOMAIN;
        if (users.existsByWorkEmail(workEmail))
            throw new DuplicateResourceException("Work email sudah digunakan: " + workEmail);
        var role = roles.findByName(RoleAuthority.canonical("PART_TIME"))
                .orElseThrow(() -> new BusinessRuleException("Role PART_TIME belum terdaftar"));
        User user = users.save(User.builder().workEmail(workEmail)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD)).build());
        userRoles.save(UserRole.builder().id(new UserRoleId(workEmail, role.getId()))
                .user(user).role(role).build());
        PartTimeEmployee employee = PartTimeEmployee.builder().firstName(request.firstName().trim())
                .lastName(request.lastName().trim()).personalEmail(request.personalEmail().trim())
                .personalPhoneNumber(request.personalPhoneNumber().trim()).lastEducation(request.lastEducation())
                .onGoingEducation(request.onGoingEducation()).user(user).build();
        return mapper.toResponse(employees.save(employee));
    }

    @Transactional(readOnly = true)
    public Page<PartTimeEmployeeResponse> findAll(int page, int size, String name) {
        if (page < 0) throw new BusinessRuleException("Page harus minimal 0");
        if (size < 1 || size > 100)
            throw new BusinessRuleException("Size harus antara 1 dan 100");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        String keyword = (name == null) ? "" : name.trim();
        return employees.search(keyword, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PartTimeEmployeeResponse findById(Integer id) { return mapper.toResponse(getEmployee(id)); }

    @Transactional
    public PartTimeEmployeeResponse update(Integer id, PartTimeEmployeeRequest request) {
        PartTimeEmployee employee = getEmployee(id);
        ensurePersonalContactsAvailable(request.personalEmail(), request.personalPhoneNumber(), id);
        employee.setFirstName(request.firstName().trim()); employee.setLastName(request.lastName().trim());
        employee.setPersonalEmail(request.personalEmail().trim());
        employee.setPersonalPhoneNumber(request.personalPhoneNumber().trim());
        employee.setLastEducation(request.lastEducation()); employee.setOnGoingEducation(request.onGoingEducation());
        return mapper.toResponse(employees.save(employee));
    }

    @Transactional
    public PartTimeEmployeeResponse resign(Integer id) {
        PartTimeEmployee employee = getEmployee(id);
        if (employee.getResignDate() == null) employee.setResignDate(java.time.LocalDateTime.now());
        return mapper.toResponse(employees.save(employee));
    }

    private PartTimeEmployee getEmployee(Integer id) {
        return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "PartTimer dengan id " + id + " tidak ditemukan"));
    }
    private void ensurePersonalContactsAvailable(String email, String phone, Integer id) {
        boolean emailUsed = id == null ? employees.existsByPersonalEmail(email.trim()) : employees.existsByPersonalEmailAndIdNot(email.trim(), id);
        boolean phoneUsed = id == null ? employees.existsByPersonalPhoneNumber(phone.trim()) : employees.existsByPersonalPhoneNumberAndIdNot(phone.trim(), id);
        if (emailUsed) throw new DuplicateResourceException("Personal email sudah digunakan");
        if (phoneUsed) throw new DuplicateResourceException("Nomor telepon pribadi sudah digunakan");
    }
    private String slug(String value) {
        return value.trim().toLowerCase(java.util.Locale.ROOT).replaceAll("\\s+", ".");
    }
}
