package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.ProfileDto;
import MachTeacher.MachTeacher.model.Role;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;


@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://10.0.2.2:3000"
}, allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
}, allowCredentials = "true")
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepo;

    // =========================
    // STUDENT
    // =========================
    @GetMapping("/student/{id}")
    public ProfileDto getStudent(@PathVariable Long id) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        return toStudentDtoFromUser(u);
    }

    @PutMapping("/student/{id}")
    @Transactional
    public ProfileDto updateStudent(@PathVariable Long id, @RequestBody ProfileDto body) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        if (body.getBio() != null)
            u.setBio(emptyToNull(body.getBio()));
        if (body.getUniversity() != null)
            u.setUniversity(emptyToNull(body.getUniversity()));
        if (body.getCareer() != null)
            u.setCareer(emptyToNull(body.getCareer()));
        if (body.getSemester() != null)
            u.setSemester(emptyToNull(body.getSemester()));

        userRepo.save(u);
        return toStudentDtoFromUser(u);
    }

    // =========================
    // MENTOR
    // =========================
    @GetMapping("/mentor/{id}")
    public ProfileDto getMentor(@PathVariable Long id) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        if (u.getRole() != Role.MENTOR)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not MENTOR");

        return toMentorDtoFromUser(u);
    }

    @PutMapping("/mentor/{id}")
    @Transactional
    public ProfileDto updateMentor(@PathVariable Long id, @RequestBody ProfileDto body) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        if (u.getRole() != Role.MENTOR)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not MENTOR");

        if (body.getBio() != null)
            u.setBio(emptyToNull(body.getBio()));
        if (body.getSubjects() != null)
            u.setSubjects(emptyToNull(body.getSubjects()));
        if (body.getHourlyRate() != null)
            u.setHourlyRate(emptyToNull(body.getHourlyRate()));
        if (body.getTeachingExperience() != null)
            u.setTeachingExperience(emptyToNull(body.getTeachingExperience()));
        if (body.getAboutMe() != null)
            u.setAboutMe(emptyToNull(body.getAboutMe()));
        if (body.getReferences() != null)
            u.setReferences(emptyToNull(body.getReferences()));

        if (body.getUniversity() != null)
            u.setUniversity(emptyToNull(body.getUniversity()));
        if (body.getCareer() != null)
            u.setCareer(emptyToNull(body.getCareer()));
        if (body.getSemester() != null)
            u.setSemester(emptyToNull(body.getSemester()));

        userRepo.save(u);
        return toMentorDtoFromUser(u);
    }

    // =========================
    //SUBIR AVATAR (MENTOR / STUDENT / ADMIN)
    // =========================
    @PostMapping("/{type}/{id}/avatar")
    @Transactional
    public ProfileDto uploadAvatar(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }

        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        String t = type.toLowerCase();

        // Validación para mentor
        if ("mentor".equals(t) && u.getRole() != Role.MENTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not MENTOR");
        }

        
        if ("student".equals(t)
                && !(u.getRole() == Role.STUDENT || u.getRole() == Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not STUDENT or ADMIN");
        }

        // Carpeta física donde guardamos los avatares
        Path uploadDir = Paths.get("uploads", "avatars");
        Files.createDirectories(uploadDir);

        String originalName = file.getOriginalFilename();
        String cleanName = (originalName == null ? "avatar" : originalName)
                .replaceAll("[^a-zA-Z0-9.\\-]", "_");

        String fileName = "avatar-" + id + "-" + System.currentTimeMillis() + "-" + cleanName;
        Path target = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String url = "/uploads/avatars/" + fileName;

        u.setAvatarUrl(url);
        userRepo.save(u);

        ProfileDto dto = (u.getRole() == Role.MENTOR)
                ? toMentorDtoFromUser(u)
                : toStudentDtoFromUser(u);
        dto.setAvatarUrl(url);
        return dto;
    }

    // ---------- Mappers usando SOLO User ----------
    private ProfileDto toStudentDtoFromUser(User u) {
        ProfileDto dto = new ProfileDto();
        dto.setId(u.getId());
        dto.setName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setLocation(joinLocation(u.getCity(), u.getCountry()));
        dto.setRole(u.getRole().name());

        dto.setBio(u.getBio());
        dto.setUniversity(u.getUniversity());
        dto.setCareer(u.getCareer());
        dto.setSemester(u.getSemester());
        dto.setAvatarUrl(u.getAvatarUrl());
        return dto;
    }

    private ProfileDto toMentorDtoFromUser(User u) {
        ProfileDto dto = new ProfileDto();
        dto.setId(u.getId());
        dto.setName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setLocation(joinLocation(u.getCity(), u.getCountry()));
        dto.setRole(u.getRole().name());

        dto.setBio(u.getBio());
        dto.setUniversity(u.getUniversity());
        dto.setCareer(u.getCareer());
        dto.setSemester(u.getSemester());

        dto.setSubjects(u.getSubjects());
        dto.setHourlyRate(u.getHourlyRate());
        dto.setTeachingExperience(u.getTeachingExperience());
        dto.setAboutMe(u.getAboutMe());
        dto.setReferences(u.getReferences());
        dto.setAvatarUrl(u.getAvatarUrl());
        return dto;
    }

    // ---------- Helpers ----------
    private String joinLocation(String city, String country) {
        if (city == null || city.isBlank())
            return country;
        if (country == null || country.isBlank())
            return city;
        return city + ", " + country;
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    //Listar mentores
    @GetMapping("/mentors")
    public Page<ProfileDto> getMentorsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userRepo.findByRole(Role.MENTOR, pageable)
                .map(this::toMentorDtoFromUser);
    }

    // =========================
    // ELIMINAR USUARIO (solo mentores desde panel admin)
    // =========================
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteMentorFromAdmin(@PathVariable Long id) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + id));

        
        if (u.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar un ADMIN");
        }

        
        if (u.getRole() != Role.MENTOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden eliminar tutores (MENTOR)");
        }

        
        u.setRole(Role.STUDENT);

        
        u.setSubjects(null);
        u.setHourlyRate(null);
        u.setTeachingExperience(null);
        u.setAboutMe(null);
        u.setReferences(null);

        userRepo.save(u);
    }

}
