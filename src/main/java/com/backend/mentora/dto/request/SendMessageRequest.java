package com.backend.mentora.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

	@NotNull
	private Long recipientId;

	@NotBlank
	private String content;

}
