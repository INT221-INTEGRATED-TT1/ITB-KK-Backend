package sit.int221.primary.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.dtos.request.LimitStatusMaskReq;
import sit.int221.dtos.request.NewStatusDTO;
import sit.int221.dtos.response.LimitStatusMaskRes;
import sit.int221.dtos.response.StatusDetailDTO;
import sit.int221.dtos.response.StatusHomeCountDTO;
import sit.int221.primary.entities.Statuses;
import sit.int221.services.ListMapper;
import sit.int221.services.StatusesService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173","http://intproj23.sit.kmutt.ac.th","http://localhost:80" ,"http://ip23tt1.sit.kmutt.ac.th","http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080"})
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
        return statusesService.insertStatus(status);
    }

    @PutMapping("/{statusId}")
    public StatusDetailDTO putStatus(@PathVariable Integer statusId,@Valid @RequestBody NewStatusDTO newStatus){
        Statuses updatedStatus = statusesService.updateStatus(statusId, newStatus);
        return modelMapper.map(updatedStatus, StatusDetailDTO.class);
    }

    @PatchMapping("/maximum-task")
    public ResponseEntity<Object> toggleLimitStatus(@RequestBody LimitStatusMaskReq limitStatusMaskReq){
        LimitStatusMaskRes limitStatusMaskRes = statusesService.toggleLimitStatusMask(limitStatusMaskReq);
        return ResponseEntity.ok(limitStatusMaskRes);
    }

    @DeleteMapping("/{statusId}")
    public Map<String, Object> deleteStatus(@PathVariable Integer statusId){
        statusesService.removeStatus(statusId);
        return Collections.emptyMap();
    }

    @DeleteMapping("/{oldStatusId}/{newStatusId}")
    public Map<String, Object> transferStatus(@PathVariable Integer oldStatusId, @PathVariable Integer newStatusId){
        statusesService.updateTasksStatusAndDelete(oldStatusId,newStatusId);
        return Collections.emptyMap();
    }


}
