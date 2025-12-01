package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
public class BookingService {

        private final SessionRepository sessionRepo;
        private final UserRepository userRepo;
        private final SubjectRepository subjectRepo;
        private final PackageTypeRepository packageRepo;
        private final AvailabilitySlotRepository availabilityRepo;

        public Session book(Long studentId,
                        Long mentorId,
                        Long subjectId,
                        Long packageTypeId,
                        LocalDate date,
                        String startTime,
                        int durationMinutes,
                        Modality modality,
                        String notes) {

                // ===== Cargas y validaciones básicas =====
                User student = userRepo.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
                if (student.getRole() != Role.STUDENT) {
                        throw new IllegalArgumentException("studentId does not belong to a STUDENT user");
                }

                User mentor = userRepo.findById(mentorId)
                                .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + mentorId));
                if (mentor.getRole() != Role.MENTOR) {
                        throw new IllegalArgumentException("mentorId does not belong to a MENTOR user");
                }

                Subject subject = subjectRepo.findById(subjectId)
                                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

                PackageType pkg = null;
                double discount = 0.0;
                if (packageTypeId != null) {
                        pkg = packageRepo.findById(packageTypeId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Package not found: " + packageTypeId));
                        discount = pkg.getDiscountPercent();
                }

                // ===== Disponibilidad =====
                LocalTime start = LocalTime.parse(startTime);
                LocalTime end = start.plusMinutes(Math.max(1, durationMinutes));
                int dow = date.getDayOfWeek().getValue();

                boolean fitsAvailability = availabilityRepo.findAll().stream().anyMatch(slot -> {
                        if (!slot.getMentor().getId().equals(mentorId))
                                return false; // mentor es User ahora
                        if (slot.getDayOfWeek() != dow)
                                return false;
                        if (slot.getModality() != modality)
                                return false;
                        LocalTime s = LocalTime.parse(slot.getStartTime());
                        LocalTime e = LocalTime.parse(slot.getEndTime());
                        return !start.isBefore(s) && !end.isAfter(e);
                });

                if (!fitsAvailability) {
                        throw new IllegalArgumentException("Mentor not available at requested time/modality.");
                }

                // ===== Precio =====
                double base;
                try {
                        base = mentor.getHourlyRate() != null && !mentor.getHourlyRate().isBlank()
                                        ? Double.parseDouble(mentor.getHourlyRate())
                                        : 0.0;
                } catch (NumberFormatException e) {
                        base = 0.0;
                }

                double hours = Math.max(1, durationMinutes) / 60.0;
                double price = base * hours;
                if (discount > 0)
                        price = price * (1 - (discount / 100.0));
                price = Math.round(price * 100.0) / 100.0;

                // ===== Crear sesión =====
                Session session = Session.builder()
                                .student(student) 
                                .mentor(mentor) 
                                .subject(subject)
                                .packageType(pkg)
                                .date(date)
                                .startTime(startTime)
                                .durationMinutes(durationMinutes)
                                .modality(modality)
                                .status(SessionStatus.SCHEDULED)
                                .priceUsd(price)
                                .notes(notes)
                                .build();

                return sessionRepo.save(session);
        }
}
