package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Tasks1;


public interface TasksRepository extends JpaRepository<Tasks1, Integer> {
}
