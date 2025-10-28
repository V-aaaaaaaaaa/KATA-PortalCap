package com.empresa.portal.repository;

import com.empresa.portal.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserId(Long userId);

    Optional<UserBadge> findByUserIdAndBadgeId(Long userId, Long badgeId);

    // Query para obtener las insignias con detalles
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.userId = :userId")
    List<UserBadge> findByUserIdWithBadge(@Param("userId") Long userId);
}