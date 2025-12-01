package MachTeacher.MachTeacher.dto;

public record SosResponseDto(
                Long id,
                SosUserDto student,
                SosUserDto acceptedBy,
                String subject,
                String message,
                String status,
                String createdAt,
                String acceptedAt,
                Long sessionId,
                Long conversationId) {
}

