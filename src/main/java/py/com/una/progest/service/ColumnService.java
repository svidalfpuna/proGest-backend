package py.com.una.progest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.una.progest.models.BoardData;
import py.com.una.progest.models.ListColumn;
import py.com.una.progest.models.Task;
import py.com.una.progest.repository.ListColumnRepository;

@Service
public class ColumnService {

    private final ListColumnRepository listColumnRepository;

    @Autowired
    public ColumnService(ListColumnRepository listColumnRepository) {
        this.listColumnRepository = listColumnRepository;
    }

    @Transactional
    public ListColumn updateTaskListColumn(BoardData boardData) {
        ListColumn newListColumn = listColumnRepository.findById(boardData.getId()).orElse(null);
        if (newListColumn != null) {
            newListColumn.setOrderColumn(boardData.getOrder());
            newListColumn.setName(boardData.getName());
            return listColumnRepository.save(newListColumn);
        }

        return null;
    }
}