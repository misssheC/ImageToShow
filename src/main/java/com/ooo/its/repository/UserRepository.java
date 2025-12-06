package com.ooo.its.repository;

import com.ooo.its.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByQqNumber(String qqnumber);
    Boolean existsByQqNumber(String qqnumber);

    @Modifying
    @Transactional
    void deleteByQqNumber(String qqNumber);
}
