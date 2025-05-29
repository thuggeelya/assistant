package com.thuggeelya.assistantclient.dao;

import com.thuggeelya.assistantclient.dao.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface ChatLogRepository extends JpaRepository<ChatLog, UUID> {
}
