package com.brgarcias.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brgarcias.todolist.utils.Utils;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    /**
     * Creates a new task.
     *
     * @param taskModel The details of the task to be created.
     * @return ResponseEntity with created task and corresponding HTTP status code
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, @RequestAttribute("idUser") UUID idUser) {
        taskModel.setIdUser(idUser);

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Started or Ended date must be greater than actual date ");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Started date must be shorter than Ended date ");
        }

        TaskModel task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    /**
     * Gets the list of all tasks.
     *
     * @return ResponseEntity with the task list and corresponding HTTP status code
     */
    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks() {
        List<TaskModel> tasks = this.taskRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    /**
     * Gets task by id.
     *
     * @param id The ID of the task to be find.
     * @return ResponseEntity with the task and corresponding HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable UUID id) {
        Optional<TaskModel> task = this.taskRepository.findById(id);
        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(task.get());
    }

    /**
     * Gets the list of all tasks by user.
     *
     * @return ResponseEntity with the task list by user and corresponding HTTP
     *         status code
     */
    @GetMapping("/user")
    public ResponseEntity<List<TaskModel>> getTasksByUser(@RequestAttribute("idUser") UUID idUser) {
        List<TaskModel> tasks = this.taskRepository.findByIdUser(idUser);

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    /**
     * Updates a task.
     *
     * @param taskModel The details of the task to be updated.
     * @param id        The ID of the task to be updated.
     * @return ResponseEntity with updated task and corresponding HTTP status code
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, @PathVariable UUID id) {

        TaskModel existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
        Utils.copyNullProperties(taskModel, existingTask);

        TaskModel updatedTask = this.taskRepository.save(existingTask);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedTask);
    }
}