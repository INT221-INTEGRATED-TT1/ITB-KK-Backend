package sit.int221.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.Statuses;

import java.util.List;


public interface StatusesRepository extends JpaRepository<Statuses, Integer> {
    Statuses findByNameContains(String name);
}
