package com.ooo.its.repository;

import com.ooo.its.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRep extends JpaRepository<Admin,Long> {

    Optional<Admin> findByName(String name);

}
