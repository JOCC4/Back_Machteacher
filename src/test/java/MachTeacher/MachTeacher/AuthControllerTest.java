package MachTeacher.MachTeacher;

import MachTeacher.MachTeacher.controller.AuthController;
import MachTeacher.MachTeacher.dto.AuthResponse;
import MachTeacher.MachTeacher.dto.LoginRequest;
import MachTeacher.MachTeacher.dto.RegisterRequest;
import MachTeacher.MachTeacher.model.Role;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.repository.UserRepository;
import MachTeacher.MachTeacher.repository.AvailabilitySlotRepository;
import MachTeacher.MachTeacher.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthenticationManager authManager;
    @Mock
    PasswordEncoder encoder;
    @Mock
    UserRepository userRepo;
    @Mock
    JwtUtil jwt;
    @Mock
    AvailabilitySlotRepository availabilitySlotRepository;

    @InjectMocks
    AuthController controller;

    @Test
    void register_creaUsuarioYRetornaTokenCorrecto() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Juan Test");
        req.setEmail("juan@test.com");
        req.setPassword("123456");
        req.setRole("student");

        when(userRepo.existsByEmail(req.getEmail())).thenReturn(false);
        when(encoder.encode("123456")).thenReturn("ENCODED");

        User saved = User.builder()
                .id(10L)
                .fullName("Juan Test")
                .email("juan@test.com")
                .password("ENCODED")
                .role(Role.STUDENT)
                .build();

        when(userRepo.save(any())).thenReturn(saved);
        when(jwt.generateToken(eq("juan@test.com"), anyMap())).thenReturn("ABC123");

        AuthResponse res = controller.register(req);

        assertEquals("ABC123", res.getToken());
        assertEquals(10L, res.getId());
        assertEquals("Juan Test", res.getFullName());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void login_retornaTokenCorrecto() {
        LoginRequest req = new LoginRequest();
        req.setEmail("a@test.com");
        req.setPassword("pass");

        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        User u = User.builder()
                .id(4L)
                .email("a@test.com")
                .fullName("Tester")
                .role(Role.STUDENT)
                .build();

        when(userRepo.findByEmail("a@test.com")).thenReturn(java.util.Optional.of(u));
        when(jwt.generateToken(eq("a@test.com"), anyMap())).thenReturn("TOKEN123");

        AuthResponse res = controller.login(req);

        assertEquals("TOKEN123", res.getToken());
        assertEquals(4L, res.getId());
    }
}
