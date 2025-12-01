package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.model.Review;
import MachTeacher.MachTeacher.model.Role;
import MachTeacher.MachTeacher.model.Session;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.repository.ReviewRepository;
import MachTeacher.MachTeacher.repository.SessionRepository;
import MachTeacher.MachTeacher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepo;
    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;

    @PostMapping
    public Review create(@RequestParam Long sessionId,
            @RequestParam Long studentId,
            @RequestParam Long mentorId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {

        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be 1..5");
        }

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student user not found"));

        User mentor = userRepo.findById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mentor user not found"));

        // Validaciones de rol
        if (student.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId does not belong to a STUDENT user");
        }
        if (mentor.getRole() != Role.MENTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mentorId does not belong to a MENTOR user");
        }

        Review review = Review.builder()
                .session(session)
                .student(student)
                .mentor(mentor)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepo.save(review);
    }

    @GetMapping("/mentor/{mentorId}")
    public List<Review> byMentor(@PathVariable Long mentorId) {
        return reviewRepo.findAll().stream()
                .filter(r -> r.getMentor() != null && mentorId.equals(r.getMentor().getId()))
                .toList();
    }

    @GetMapping("/session/{sessionId}")
    public List<Review> bySession(@PathVariable Long sessionId) {
        return reviewRepo.findAll().stream()
                .filter(r -> r.getSession() != null && sessionId.equals(r.getSession().getId()))
                .toList();
    }
}
