package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Statuses3;

public interface Statuses3Repository extends JpaRepository<Statuses3, Integer> {
    Statuses3 findByStatusName(String name);
}