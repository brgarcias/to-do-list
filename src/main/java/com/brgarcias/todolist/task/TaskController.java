package com.brgarcias.todolist.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("")
    public TaskModel create(@RequestBody TaskModel taskModel) {
        TaskModel task = this.taskRepository.save(taskModel);
        return task;
    }

    @GetMapping("")
    public List<TaskModel> getTasks() {
        List<TaskModel> tasks = this.taskRepository.findAll();
        return tasks;
    }
}
