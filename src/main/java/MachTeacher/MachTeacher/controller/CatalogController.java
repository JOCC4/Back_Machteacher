package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.model.Career;
import MachTeacher.MachTeacher.model.PackageType;
import MachTeacher.MachTeacher.model.Subject;
import MachTeacher.MachTeacher.model.University;
import MachTeacher.MachTeacher.repository.CareerRepository;
import MachTeacher.MachTeacher.repository.PackageTypeRepository;
import MachTeacher.MachTeacher.repository.SubjectRepository;
import MachTeacher.MachTeacher.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final SubjectRepository subjectRepo;
    private final UniversityRepository universityRepo;
    private final CareerRepository careerRepo;
    private final PackageTypeRepository packageRepo;

    @GetMapping("/subjects")
    public List<Subject> subjects() {
        return subjectRepo.findAll();
    }

    @GetMapping("/universities")
    public List<University> universities() {
        return universityRepo.findAll();
    }

    @GetMapping("/universities/{id}/careers")
    public List<Career> careersByUniversity(@PathVariable Long id) {
        return careerRepo.findAll().stream()
                .filter(c -> c.getUniversity() != null && c.getUniversity().getId().equals(id))
                .toList();
    }

    @GetMapping("/packages")
    public List<PackageType> packages() {
        return packageRepo.findAll();
    }
}
