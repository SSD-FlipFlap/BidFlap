package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Notification;
import com.ssd.bidflap.domain.enums.NotificationType;
import com.ssd.bidflap.domain.enums.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus);
    List<Notification> findByMemberIdAndReadStatusAndNotificationType(Long memberId, ReadStatus readStatus, NotificationType type);

}
