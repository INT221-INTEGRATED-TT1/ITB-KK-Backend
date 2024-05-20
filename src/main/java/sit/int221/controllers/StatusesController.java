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
import sit.int221.dtos.response.StatusHomeCountDTO;
import sit.int221.entities.Statuses;
import sit.int221.exceptions.ErrorResponse;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.services.ListMapper;
import sit.int221.services.StatusesService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@CrossOrigin(origins = {"http://localhost:5173","http://intproj23.sit.kmutt.ac.th","http://localhost:80" ,"http://ip23tt1.sit.kmutt.ac.th","http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080", "http://10.5.5.180:5173"})
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
    public List<StatusHomeCountDTO> getAllStatusesWithCount() {
        return statusesService.getStatusWithCountTasksInUse();
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

    @GetMapping("/maximum-task")
    public Statuses toggleLimitStatuses(@PathVariable Integer statusId) {
        return statusesService.findStatusById(statusId);
    }

    @DeleteMapping("/{statusId}")
    public Map<String, Object> deleteStatus(@PathVariable Integer statusId){
        statusesService.removeStatus(statusId);
        return Collections.emptyMap();
    }

    @DeleteMapping("/{oldStatusId}/{newStatusId}")
    public Map<String, Object> transferStatus(@PathVariable Integer oldStatusId, @PathVariable Integer newStatusId){
        statusesService.updateTasksStatus(oldStatusId,newStatusId);
        return Collections.emptyMap();
    }

    @ExceptionHandler(StatusNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(StatusNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
