package MachTeacher.MachTeacher.dto;

import MachTeacher.MachTeacher.model.UrgentHelpRequest;

public class UrgentHelpRequestMapper {

    public static SosResponseDto toDto(UrgentHelpRequest sos) {

        SosUserDto studentDto = null;
        if (sos.getStudent() != null) {
            studentDto = new SosUserDto(
                    sos.getStudent().getId(),
                    sos.getStudent().getFullName());
        }

        SosUserDto acceptedByDto = null;
        if (sos.getAcceptedBy() != null) {
            acceptedByDto = new SosUserDto(
                    sos.getAcceptedBy().getId(),
                    sos.getAcceptedBy().getFullName());
        }

        return new SosResponseDto(
                sos.getId(),
                studentDto,
                acceptedByDto,
                sos.getSubject(),
                sos.getMessage(),
                sos.getStatus().name(),
                sos.getCreatedAt() != null ? sos.getCreatedAt().toString() : null,
                sos.getAcceptedAt() != null ? sos.getAcceptedAt().toString() : null,
                sos.getSessionId(),
                sos.getConversationId());
    }
}
