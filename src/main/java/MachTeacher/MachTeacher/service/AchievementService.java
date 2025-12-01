package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.model.Achievement;
import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.model.UserAchievement;
import MachTeacher.MachTeacher.repository.AchievementRepository;
import MachTeacher.MachTeacher.repository.UserAchievementRepository;
import MachTeacher.MachTeacher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final UserAchievementRepository userAchievementRepository;

    // --- Achievements---
    public Achievement create(Achievement a) {
        return achievementRepository.save(a);
    }

    public Achievement getById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + id));
    }

    public List<Achievement> getAll() {
        return achievementRepository.findAll();
    }

    @Transactional
    public Achievement update(Long id, Achievement data) {
        Achievement a = getById(id);
        if (data.getName() != null)
            a.setName(data.getName());
        if (data.getDescription() != null)
            a.setDescription(data.getDescription());
        if (data.getIcon() != null)
            a.setIcon(data.getIcon());
        return a;
    }

    public void delete(Long id) {
        achievementRepository.deleteById(id);
    }

    // --- Asignación a usuarios ---
    @Transactional
    public UserAchievement grantToUser(Long userId, Long achievementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Achievement ach = getById(achievementId);

        UserAchievement ua = UserAchievement.builder()
                .user(user)
                .achievement(ach)
                .earnedAt(LocalDateTime.now())
                .build();

        return userAchievementRepository.save(ua);
    }

    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findAll()
                .stream()
                .filter(ua -> ua.getUser().getId().equals(userId))
                .toList();
    }

    public void revokeFromUser(Long userAchievementId) {
        userAchievementRepository.deleteById(userAchievementId);
    }
}
