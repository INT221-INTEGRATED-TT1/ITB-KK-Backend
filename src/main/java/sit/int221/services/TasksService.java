//package sit.int221.services;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//import sit.int221.dtos.request.NewTaskDTO;
//import sit.int221.entities.primary.TaskStatus;
//import sit.int221.entities.primary.Tasks1;
//import sit.int221.exceptions.TaskNotFoundException;
//import sit.int221.repositories.primary.TasksRepository;
//
//
//import java.util.List;
//
//@Service
//public class TasksService {
//    @Autowired
//    TasksRepository tasksRepository;
//    @Autowired
//    ModelMapper modelMapper;
//
//    public List<Tasks1> getAllTasksList() {
//        return tasksRepository.findAll();
//    }
//
//    public Tasks1 findTaskById(Integer taskId) {
//        return tasksRepository.findById(taskId).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task "+ taskId + " Doesn't Exist!!!"));
//    }
//
//    public Tasks1 insertTask(NewTaskDTO newTaskDTO){
//        newTaskDTO.setTitle(newTaskDTO.getTitle().trim());
//        if(newTaskDTO.getDescription() != null && !newTaskDTO.getDescription().isBlank()){newTaskDTO.setDescription(newTaskDTO.getDescription().trim());}
//        else{newTaskDTO.setDescription(null);}
//        if(newTaskDTO.getAssignees() != null && !newTaskDTO.getAssignees().isBlank()){newTaskDTO.setAssignees(newTaskDTO.getAssignees().trim());}
//        else{newTaskDTO.setAssignees(null);}
//        if(newTaskDTO.getStatus() == null || newTaskDTO.getStatus().toString().trim().isEmpty()){newTaskDTO.setStatus(TaskStatus.NO_STATUS);}
//        Tasks1 task = modelMapper.map(newTaskDTO, Tasks1.class);
//        return tasksRepository.saveAndFlush(task);
//    }
//
//    public Tasks1 removeTask(Integer taskId) {
//        Tasks1 findTasks1 = tasksRepository.findById(taskId).orElseThrow(
//                () -> new TaskNotFoundException("NOT FOUND"));
//        tasksRepository.deleteById(taskId);
//        return findTasks1;
//    }
//
//    public Tasks1 updateTask(Integer taskId, Tasks1 newTaskData) {
//        Tasks1 findTasks1 = tasksRepository.findById(taskId).orElseThrow(
//                () -> new TaskNotFoundException("NOT FOUND"));
//        findTasks1.setTitle(newTaskData.getTitle().trim());
//        findTasks1.setAssignees(newTaskData.getAssignees().trim());
//        findTasks1.setDescription(newTaskData.getDescription().trim());
//        findTasks1.setStatus(newTaskData.getStatus());
//        tasksRepository.save(findTasks1);
//        return findTasks1;
//    }
//
//}
