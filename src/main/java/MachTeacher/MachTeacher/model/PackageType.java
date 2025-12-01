package MachTeacher.MachTeacher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int sessionsCount;

    @Column(nullable = false)
    private double discountPercent;
}
