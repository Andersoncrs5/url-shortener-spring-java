package com.notify.notify.modules.user.repository;

import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.utils.base.repository.CustomCdcRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long>, CustomCdcRepository<UserEntity> {
    @Modifying
    @Query("DELETE FROM UserEntity s WHERE s.id = :id")
    int deleteAndCount(@Param("id") Long id);
}
