package com.edulink.repository;

import com.edulink.model.Activity;
import com.edulink.model.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByActivityType(ActivityType activityType);
}
