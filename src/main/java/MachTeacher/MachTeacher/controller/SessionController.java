package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.SessionCreateRequest;
import MachTeacher.MachTeacher.dto.SessionDTO;
import MachTeacher.MachTeacher.model.SessionStatus;
import MachTeacher.MachTeacher.repository.UserRepository;
import MachTeacher.MachTeacher.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    // ---- Listados generales ----
    @GetMapping
    public List<SessionDTO> list() {
        return sessionService.listAll();
    }

    @GetMapping("/{id}")
    public SessionDTO get(@PathVariable Long id) {
        return sessionService.getById(id);
    }

    // ---- Listado por estudiante----
    @GetMapping("/student/{studentId}")
    public List<SessionDTO> byStudent(@PathVariable Long studentId) {
        return sessionService.listByStudent(studentId);
    }

    // ---- Listado por mentor----
    @GetMapping("/mentor/{mentorId}")
    public List<SessionDTO> byMentor(@PathVariable Long mentorId) {
        return sessionService.listByMentor(mentorId);
    }

    // ======================================================
    // listar sesiones por usuario
    // ======================================================
    @GetMapping("/user/{userId}")
    public List<SessionDTO> listByUser(@PathVariable Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        
        switch (user.getRole()) {
            case STUDENT:
                return sessionService.listByStudent(userId);

            case MENTOR:
                return sessionService.listByMentor(userId);

            default:
                throw new IllegalArgumentException("Unsupported role: " + user.getRole());
        }
    }

    
    @PostMapping
    public ResponseEntity<SessionDTO> create(
            @RequestBody @Valid SessionCreateRequest req,
            Authentication auth) {

        Long studentId;

        
        if (auth != null) {
            String username = auth.getName();
            studentId = userRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + username))
                    .getId();
        } else {
            
            studentId = req.getStudentId();
        }

        req.setStudentId(studentId);

        SessionDTO created = sessionService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public SessionDTO updateStatus(
            @PathVariable Long id,
            @RequestParam SessionStatus value) {
        return sessionService.updateStatus(id, value);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sessionService.delete(id);
    }
}
