package py.com.una.progest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.una.progest.models.ListColumn;
import py.com.una.progest.models.Task;
import py.com.una.progest.models.TaskData;
import py.com.una.progest.repository.ListColumnRepository;
import py.com.una.progest.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private final ListColumnRepository listColumnRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ListColumnRepository listColumnRepository) {
        this.taskRepository = taskRepository;
        this.listColumnRepository = listColumnRepository;
    }

    @Transactional
    public Task updateTaskListColumn(TaskData taskData, Long newListColumnId) {
        Task task = taskRepository.findById(taskData.getId()).orElse(null);

        if (task != null) {
            ListColumn newListColumn = listColumnRepository.findById(newListColumnId).orElse(null);
            if (newListColumn != null) {
                task.setDescription(taskData.getDescription());
                task.setTitle(taskData.getTitle());
                task.setExpirationDate(taskData.getExpirationDate());
                task.setListColumn(newListColumn);
                task.setOrderTask(taskData.getOrder());
                return taskRepository.save(task);
            }
        }

        return null;
    }
}