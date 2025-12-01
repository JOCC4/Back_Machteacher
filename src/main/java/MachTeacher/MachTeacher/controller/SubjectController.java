package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.model.Subject;
import MachTeacher.MachTeacher.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> listAll() {
        return subjectRepository.findAll();
    }
}
