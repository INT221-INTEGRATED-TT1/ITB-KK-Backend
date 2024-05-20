package sit.int221.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.Tasks;


public interface TasksRepository extends JpaRepository<Tasks, Integer> {
}
