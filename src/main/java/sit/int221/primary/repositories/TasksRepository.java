package sit.int221.primary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.primary.entities.Tasks;


public interface TasksRepository extends JpaRepository<Tasks, Integer> {
}
