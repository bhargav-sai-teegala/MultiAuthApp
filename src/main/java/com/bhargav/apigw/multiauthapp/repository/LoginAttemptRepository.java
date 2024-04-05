package com.bhargav.apigw.multiauthapp.repository;

import com.bhargav.apigw.multiauthapp.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
}

