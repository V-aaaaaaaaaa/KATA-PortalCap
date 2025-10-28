package com.empresa.portal.controller;

import com.empresa.portal.model.Badge;
import com.empresa.portal.model.UserBadge;
import com.empresa.portal.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();

        List<Map<String, Object>> response = badges.stream().map(badge -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", badge.getId());
            map.put("moduleId", badge.getModuleId());
            map.put("name", badge.getName());
            map.put("description", badge.getDescription());
            map.put("icon", badge.getIcon());
            map.put("color", badge.getColor());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserHallOfFame(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        List<Badge> allBadges = badgeService.getAllBadges();

        List<UserBadge> userBadges = badgeService.getUserBadges(userId);

        Set<Long> earnedBadgeIds = userBadges.stream()
                .map(UserBadge::getBadgeId)
                .collect(Collectors.toSet());

        List<Map<String, Object>> earnedBadges = new ArrayList<>();
        List<Map<String, Object>> lockedBadges = new ArrayList<>();

        for (Badge badge : allBadges) {
            if (earnedBadgeIds.contains(badge.getId())) {
                UserBadge userBadge = userBadges.stream()
                        .filter(ub -> ub.getBadgeId().equals(badge.getId()))
                        .findFirst()
                        .orElse(null);

                if (userBadge != null) {
                    Map<String, Object> badgeMap = new HashMap<>();
                    badgeMap.put("id", badge.getId());
                    badgeMap.put("name", badge.getName());
                    badgeMap.put("icon", badge.getIcon());
                    badgeMap.put("color", badge.getColor());
                    badgeMap.put("courseName", "Curso ID: " + userBadge.getCourseId()); // Mejorar después
                    badgeMap.put("earnedAt", userBadge.getEarnedAt().toString());
                    badgeMap.put("isLocked", false);
                    earnedBadges.add(badgeMap);
                }
            } else {
                Map<String, Object> badgeMap = new HashMap<>();
                badgeMap.put("id", badge.getId());
                badgeMap.put("name", badge.getName());
                badgeMap.put("icon", badge.getIcon());
                badgeMap.put("color", badge.getColor());
                badgeMap.put("requirement", badge.getDescription());
                badgeMap.put("isLocked", true);
                lockedBadges.add(badgeMap);
            }
        }

        response.put("earnedBadges", earnedBadges);
        response.put("lockedBadges", lockedBadges);
        response.put("totalBadges", allBadges.size());
        response.put("earnedCount", earnedBadges.size());

        return ResponseEntity.ok(response);
    }
}