package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.model.Modality;
import MachTeacher.MachTeacher.model.Session;
import MachTeacher.MachTeacher.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Session book(
            @RequestParam Long studentId,
            @RequestParam Long mentorId,
            @RequestParam Long subjectId,
            @RequestParam(required = false) Long packageTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String startTime,
            @RequestParam int durationMinutes,
            @RequestParam Modality modality,
            @RequestParam(required = false) String notes) {
        return bookingService.book(
                studentId, mentorId, subjectId, packageTypeId,
                date, startTime, durationMinutes, modality, notes);
    }
}
