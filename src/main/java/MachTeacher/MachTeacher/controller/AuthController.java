package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.AuthResponse;
import MachTeacher.MachTeacher.dto.LoginRequest;
import MachTeacher.MachTeacher.dto.RegisterRequest;
import MachTeacher.MachTeacher.model.Role;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.model.AvailabilitySlot;
import MachTeacher.MachTeacher.model.Modality;
import MachTeacher.MachTeacher.repository.UserRepository;
import MachTeacher.MachTeacher.repository.AvailabilitySlotRepository;
import MachTeacher.MachTeacher.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authManager;
        private final PasswordEncoder encoder;
        private final UserRepository userRepo;
        private final JwtUtil jwt;

        
        private final AvailabilitySlotRepository availabilitySlotRepository;

        // ---------- REGISTER ----------
        @PostMapping("/register")
        public AuthResponse register(@RequestBody RegisterRequest req) {
                
                if (userRepo.existsByEmail(req.getEmail())) {
                        throw new IllegalArgumentException("Email already registered");
                }

                
                Role role = Role.STUDENT;
                if (req.getRole() != null && !req.getRole().isBlank()) {
                        role = Role.valueOf(req.getRole().toUpperCase());
                }

                
                User u = new User();
                u.setFullName(req.getFullName());
                u.setEmail(req.getEmail());
                u.setPassword(encoder.encode(req.getPassword()));
                u.setRole(role);

                u.setPhone(req.getPhone());
                u.setCity(req.getCity());
                u.setCountry(req.getCountry());

                
                u.setUniversity(req.getUniversity());
                u.setCareer(req.getCareer());
                u.setSemester(req.getSemester());

                
                u.setBio(req.getBio());
                u.setSubjects(req.getSubjects());
                u.setHourlyRate(req.getHourlyRate());
                u.setTeachingExperience(req.getTeachingExperience());
                u.setAboutMe(req.getAboutMe());
                u.setReferences(req.getReferences());

                u = userRepo.save(u);

                
                if (u.getRole() == Role.MENTOR) {
                        for (int dow = 1; dow <= 5; dow++) { 
                                AvailabilitySlot slot = new AvailabilitySlot();
                                slot.setMentor(u);
                                slot.setDayOfWeek(dow);
                                slot.setStartTime("14:00");
                                slot.setEndTime("18:00");
                                slot.setModality(Modality.ONLINE);
                                slot.setLocation("Online");
                                availabilitySlotRepository.save(slot);
                        }
                }

                
                String token = jwt.generateToken(
                                u.getEmail(),
                                Map.of("uid", u.getId(), "role", u.getRole().name()));

                
                AuthResponse res = new AuthResponse();
                res.setToken(token);
                res.setId(u.getId());
                res.setFullName(u.getFullName());
                res.setEmail(u.getEmail());
                res.setRole(u.getRole().name());
                res.setPhone(u.getPhone());
                res.setCity(u.getCity());
                res.setCountry(u.getCountry());
                res.setUniversity(u.getUniversity());
                res.setCareer(u.getCareer());
                res.setSemester(u.getSemester());
                return res;
        }

        // ---------- LOGIN ----------
        @PostMapping("/login")
        public AuthResponse login(@RequestBody LoginRequest req) {
                Authentication auth = authManager.authenticate(
                                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

                User user = userRepo.findByEmail(req.getEmail())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                String token = jwt.generateToken(
                                user.getEmail(),
                                Map.of("uid", user.getId(), "role", user.getRole().name()));

                AuthResponse res = new AuthResponse();
                res.setToken(token);
                res.setId(user.getId());
                res.setFullName(user.getFullName());
                res.setEmail(user.getEmail());
                res.setRole(user.getRole().name());
                res.setPhone(user.getPhone());
                res.setCity(user.getCity());
                res.setCountry(user.getCountry());
                res.setUniversity(user.getUniversity());
                res.setCareer(user.getCareer());
                res.setSemester(user.getSemester());
                return res;
        }
}
