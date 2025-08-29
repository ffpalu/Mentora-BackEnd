package com.backend.mentora.dto.response;

import com.backend.mentora.entity.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClientRequestResponse {
	private Long id;
	private Long clientId;
	private String clientName;
	private Integer clientAge;
	private String clientMessage;
	private RequestStatus status;
	private LocalDateTime requestedAt;
	private String clientPriority;

}
