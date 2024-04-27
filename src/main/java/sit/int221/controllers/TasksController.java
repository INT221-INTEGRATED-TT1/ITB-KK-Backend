package sit.int221.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.entities.Tasks;
import sit.int221.services.TasksService;
import java.util.List;
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174", "http://ip23kp3.sit.kmutt.ac.th:80","http://ip23kp3.sit.kmutt.ac.th:1449"})
@RestController
@RequestMapping("/v1/tasks")
public class TasksController {
    @Autowired
    TasksService tasksService;

    @GetMapping("")
    public List<Tasks> getAllTasks() {
        return tasksService.getAllTasksList();
    }

    @GetMapping("/{taskId}")
    public Tasks findTaskById(@PathVariable Integer taskId) {
        return tasksService.findTaskById(taskId);
    }

}
