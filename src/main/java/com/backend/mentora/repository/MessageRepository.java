package com.backend.mentora.repository;

import com.backend.mentora.entity.Appointment;
import com.backend.mentora.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	@Query("""
			SELECT m FROM Message m
			WHERE (m.sender.id = :user1 AND m.recipient.id = :user2)
			  OR (m.sender.id = :user2 AND m.recipient.id = :user1)
			ORDER BY m.sentAt DESC
""")
	List<Message> findConversation(
					@Param("user1") Long user1,
					@Param("user2") Long user2,
					Pageable pageable
	);

	@Query("""
			SELECT COUNT(m) FROM Message m
			WHERE m.recipient.id = :userId AND m.readAt IS NULL
""")
	Long countUnreadMessage(@Param("userId")  Long userId);


}
