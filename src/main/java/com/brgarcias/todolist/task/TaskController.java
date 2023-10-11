package com.brgarcias.todolist.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel) {
        TaskModel task = taskRepository.save(taskModel);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    /**
     * Gets the list of all tasks.
     *
     * @return ResponseEntity with the task list and corresponding HTTP status code
     */
    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks() {
        List<TaskModel> tasks = taskRepository.findAll();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}
