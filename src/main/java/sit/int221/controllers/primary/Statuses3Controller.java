package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.dtos.request.NewStatus3DTO;
import sit.int221.dtos.response.Status3DetailDTO;
import sit.int221.dtos.response.Status3HomeCountDTO;
import sit.int221.entities.primary.Statuses3;
import sit.int221.services.AuthorizationService;
import sit.int221.services.ListMapper;
import sit.int221.services.Statuses3Service;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "https://intproj23.sit.kmutt.ac.th", "http://localhost:80", "https://ip23tt1.sit.kmutt.ac.th", "http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080"})
@RestController
@RequestMapping("/v3/boards")
public class Statuses3Controller {
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;


    @GetMapping("/{boardId}/statuses")
    public List<Status3HomeCountDTO> getAllStatusesWithCount(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
        Claims claims = authorizationService.validateToken(token);
        return statuses3Service.getAllStatusesWithCountTasksInUse(claims, boardId);
    }

    @GetMapping("/{boardId}/statuses/{statusId}")
    public Statuses3 findStatusById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer statusId) {
        Claims claims = authorizationService.validateToken(token);
        return statuses3Service.findStatusById(claims, boardId, statusId);
    }

    @PostMapping("/{boardId}/statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public Statuses3 createStatus(@RequestHeader("Authorization") String token, @PathVariable String boardId, @Valid @RequestBody NewStatus3DTO newStatus) {
        Claims claims = authorizationService.validateToken(token);
        return statuses3Service.insertStatus(claims, boardId, newStatus);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public Status3DetailDTO putStatus(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer statusId, @Valid @RequestBody NewStatus3DTO newStatus) {
        Claims claims = authorizationService.validateToken(token);
        Statuses3 updatedStatus = statuses3Service.updateStatus(claims, boardId, statusId, newStatus);
        return modelMapper.map(updatedStatus, Status3DetailDTO.class);
    }

//    @PatchMapping("/maximum-task")
//    public ResponseEntity<Object> toggleLimitStatus(@RequestBody LimitStatusMaskReq limitStatusMaskReq){
//        LimitStatusMaskRes limitStatusMaskRes = statusesService.toggleLimitStatusMask(limitStatusMaskReq);
//        return ResponseEntity.ok(limitStatusMaskRes);
//    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public Statuses3 deleteStatus(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer statusId) {
        Claims claims = authorizationService.validateToken(token);
        return statuses3Service.removeStatus(claims, boardId, statusId);
    }

    @DeleteMapping("/{boardId}/statuses/{oldStatusId}/{newStatusId}")
    public Statuses3 transferStatus(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer oldStatusId, @PathVariable Integer newStatusId) {
        Claims claims = authorizationService.validateToken(token);
        return statuses3Service.updateTasksStatusAndDelete(claims, boardId, oldStatusId, newStatusId);
    }
}
