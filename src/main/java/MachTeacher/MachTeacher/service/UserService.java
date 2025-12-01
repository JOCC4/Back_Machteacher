package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.dto.RegisterRequest;
import MachTeacher.MachTeacher.model.Role;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {

        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(Long id, User data) {
        User u = getById(id);

        if (data.getFullName() != null && !data.getFullName().isBlank()) {
            u.setFullName(data.getFullName());
        }
        if (data.getEmail() != null && !data.getEmail().isBlank()) {
            u.setEmail(data.getEmail());
        }
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            u.setPassword(data.getPassword());
        }
        if (data.getRole() != null) {
            u.setRole(data.getRole());
        }

        
        if (data.getUniversity() != null) {
            u.setUniversity(data.getUniversity());
        }
        if (data.getCareer() != null) {
            u.setCareer(data.getCareer());
        }
        if (data.getSemester() != null) {
            u.setSemester(data.getSemester());
        }

        return u;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public User buildUserFrom(RegisterRequest req, String encodedPassword) {
        return User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .city(req.getCity())
                .country(req.getCountry())
                .password(encodedPassword)
                .role(req.getRole() != null ? Role.valueOf(req.getRole()) : Role.STUDENT)
                .university(req.getUniversity())
                .career(req.getCareer())
                .semester(req.getSemester())
                .build();
    }
}
