package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.SosResponseDto;
import MachTeacher.MachTeacher.dto.UrgentHelpRequestMapper;
import MachTeacher.MachTeacher.model.UrgentHelpRequest;
import MachTeacher.MachTeacher.service.UrgentHelpRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class UrgentHelpRequestController {

    private final UrgentHelpRequestService sosService;

    // ───────────────── 1) Crear SOS (ALUMNO) ─────────────────
    @PostMapping
    public ResponseEntity<SosResponseDto> createSos(@RequestBody CreateSosRequest request) {
        UrgentHelpRequest sos = sosService.createSos(
                request.studentId,
                request.subject,
                request.message);

        if (sos == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(UrgentHelpRequestMapper.toDto(sos));
    }

    // ───────────────── 2) Listar SOS activos (MENTOR) ─────────────────
    @GetMapping("/active")
    public ResponseEntity<List<SosResponseDto>> getActiveSos() {
        List<SosResponseDto> list = sosService.getActiveSos()
                .stream()
                .map(UrgentHelpRequestMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ───────────────── 3) Aceptar SOS (MENTOR) ─────────────────
    @PostMapping("/{id}/accept")
    public ResponseEntity<SosResponseDto> acceptSos(
            @PathVariable("id") Long sosId,
            @RequestBody AcceptSosRequest request) {
        UrgentHelpRequest sos = sosService.acceptSos(sosId, request.mentorId);

        if (sos == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(UrgentHelpRequestMapper.toDto(sos));
    }

    // ───────────────── 4) SOS creados por un alumno ─────────────────
    @GetMapping("/my")
    public ResponseEntity<List<SosResponseDto>> getMySos(@RequestParam("studentId") Long studentId) {
        List<SosResponseDto> list = sosService.getSosByStudent(studentId)
                .stream()
                .map(UrgentHelpRequestMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ───────────────── 5) SOS aceptados por un mentor ─────────────────
    @GetMapping("/mentor")
    public ResponseEntity<List<SosResponseDto>> getSosForMentor(@RequestParam("mentorId") Long mentorId) {
        List<SosResponseDto> list = sosService.getSosByMentor(mentorId)
                .stream()
                .map(UrgentHelpRequestMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ───────────────── DTOs simples para request ─────────────────
    public static class CreateSosRequest {
        public Long studentId;
        public String subject;
        public String message;
    }

    public static class AcceptSosRequest {
        public Long mentorId;
    }
}
