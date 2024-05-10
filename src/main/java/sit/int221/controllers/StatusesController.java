package sit.int221.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import sit.int221.dtos.request.NewStatusDTO;
import sit.int221.dtos.response.StatusDetailDTO;
import sit.int221.entities.Statuses;
import sit.int221.exceptions.ErrorResponse;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.services.ListMapper;
import sit.int221.services.StatusesService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174","http://localhost:80" ,"http://ip23tt1.sit.kmutt.ac.th","http://ip23tt1.sit.kmutt.ac.th:1449", "http://10.0.208.95:5173/"})
@RestController
@RequestMapping("/v2/statuses")
public class StatusesController {
    @Autowired
    StatusesService statusesService;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("")
    public List<Statuses> getAllTasks() {
//        List<Statuses> statusesList = statusesService.getAllStatusesList();
//        return listMapper.mapList(statusesList, StatusDetailDTO.class, modelMapper);
        return statusesService.getAllStatusesList();
    }

    @GetMapping("/{statusId}")
    public Statuses findStatusById(@PathVariable Integer statusId) {
        return statusesService.findStatusById(statusId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Statuses createStatus(@Valid @RequestBody NewStatusDTO status){
//        Statuses insertedTask = statusesService.insertStatus(status);
//        return modelMapper.map(insertedTask, StatusDetailDTO.class);
        return statusesService.insertStatus(status);
    }

    @PutMapping("/{statusId}")
    public StatusDetailDTO putStatus(@PathVariable Integer statusId, @RequestBody NewStatusDTO newStatus){
        Statuses updatedStatus = statusesService.updateStatus(statusId, newStatus);
        return modelMapper.map(updatedStatus, StatusDetailDTO.class);
    }

    @DeleteMapping("/{statusId}")
    public Map<String, Object> deleteStatus(@PathVariable Integer statusId){
        statusesService.removeStatus(statusId);
        return Collections.emptyMap();
    }

    @DeleteMapping("/{newStatusId}/{oldStatusId}")
    public Map<String, Object> deleteStatus(@PathVariable Integer newStatusId, @PathVariable Integer oldStatusId){
        statusesService.updateTasksStatus(newStatusId,oldStatusId);
        return Collections.emptyMap();
    }

    @ExceptionHandler(StatusNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(TaskNotFoundException ex, WebRequest request) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        ErrorResponse errorResponse = new ErrorResponse(
                zonedDateTime.format(formatter),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
