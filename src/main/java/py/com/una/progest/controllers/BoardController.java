package py.com.una.progest.controllers;

import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import py.com.una.progest.models.*;
import py.com.una.progest.payload.request.BoardDataRequest;
import py.com.una.progest.payload.request.NewBoardRequest;
import py.com.una.progest.payload.request.NewColumnRequest;
import py.com.una.progest.payload.request.NewTaskRequest;
import py.com.una.progest.payload.response.BoardDataResponse;
import py.com.una.progest.payload.response.MessageResponse;
import py.com.una.progest.payload.response.SpaceDataResponse;
import py.com.una.progest.repository.BoardRepository;
import py.com.una.progest.repository.ListColumnRepository;
import py.com.una.progest.repository.TaskRepository;
import py.com.una.progest.repository.UserRepository;
import py.com.una.progest.service.ColumnService;
import py.com.una.progest.service.TaskService;

import java.util.*;
import java.util.stream.Collectors;
import py.com.una.progest.payload.request.NewSpaceRequest;
import py.com.una.progest.repository.WorkSpaceRepository;

//@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardRepository boardRepository;
    private final ListColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final ColumnService columnService;
    private final WorkSpaceRepository spaceRepository;

    @Autowired
    public BoardController(BoardRepository boardRepository, ListColumnRepository columnRepository,
            UserRepository userRepository, TaskRepository taskRepository,
            TaskService taskService, ColumnService columnService, WorkSpaceRepository space) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.columnService = columnService;
        this.spaceRepository = space;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createBoard(@Valid @RequestBody NewBoardRequest boardRequest) {
        Date date = new Date();

        Space s = spaceRepository.findById(boardRequest.getIdSpace()).orElse(null);

        Board newBoard = new Board(null, boardRequest.getName(), date, userRepository.findByUsername(getUsername()).get(), s);
        newBoard = boardRepository.save(newBoard);

        Set<ListColumn> columnSet = new HashSet<>();
        ListColumn columnNew = new ListColumn(null, "New", date, 1, newBoard);
        ListColumn columnInProgress = new ListColumn(null, "In Progress", date, 2, newBoard);
        ListColumn columnDone = new ListColumn(null, "Done", date, 3, newBoard);

        columnSet.add(columnNew);
        columnSet.add(columnInProgress);
        columnSet.add(columnDone);

        columnRepository.saveAll(columnSet);

        return ResponseEntity.ok(new MessageResponse("Board created successfully!"));
    }

    @PostMapping("/newSpace")
    public ResponseEntity<?> createSpace(@Valid @RequestBody NewSpaceRequest spaceRequest) {
        Date date = new Date();

        Space space = new Space(null, spaceRequest.getName(), date);

        spaceRepository.save(space);

        return ResponseEntity.ok(new MessageResponse("Space created successfully!"));
    }

    @PostMapping("/column/new")
    public ResponseEntity<?> createColumn(@Valid @RequestBody NewColumnRequest columnRequest) {
        Date date = new Date();

        Board board = boardRepository.findById(columnRequest.getIdBoard()).orElse(null);

        ListColumn columnNew = new ListColumn(null, columnRequest.getName(), date, columnRequest.getOrder(), board);
        columnRepository.save(columnNew);

        return ResponseEntity.ok(new MessageResponse("Column created successfully!"));
    }

    @PostMapping("/task/new")
    public ResponseEntity<?> createTask(@Valid @RequestBody NewTaskRequest taskRequest) {

        ListColumn column = columnRepository.findById(taskRequest.getBoard().longValue()).get();
        if (!taskRepository.existsByTitleAndListColumn(taskRequest.getTitle(), column)) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // Suma dos semanas a la fecha actual (duracion del spring) por ||defecto
            calendar.add(Calendar.WEEK_OF_YEAR, 2);
            if (taskRequest.getExpiration() == null) {
                taskRequest.setExpiration(calendar.getTime());
            }

            Task task = new Task(null, taskRequest.getTitle(), taskRequest.getDescription() != null ? taskRequest.getDescription() : "",
                    date, taskRequest.getExpiration(),
                    taskRequest.getOrder(), column, null);
            taskRepository.save(task);

            return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Existing task"));
        }
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {

        taskRepository.deleteById(taskId);
        return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId) {
        Set<ListColumn> columns = columnRepository.findByBoardId(boardId).orElse(null);

        columns.forEach(
                column -> {
                    taskRepository.deleteAllByListColumnId(column.getId());
                });
        columnRepository.deleteAllByBoardId(boardId);

        boardRepository.deleteById(boardId);
        return ResponseEntity.ok(new MessageResponse("board is deleted!"));
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<?> deleteColumn(@PathVariable Long columnId) {
        taskRepository.deleteAllByListColumnId(columnId);
        columnRepository.deleteById(columnId);

        return ResponseEntity.ok(new MessageResponse("column is deleted!"));
    }

    @GetMapping("/getColumns/{boardId}")
    public ResponseEntity<?> getColumns(@PathVariable Long boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (Objects.nonNull(board)) {
            BoardDataResponse boardResponse = new BoardDataResponse();

            Set<ListColumn> columns = columnRepository.findByBoardId(boardId).orElse(null);

            boardResponse.setBoardDataRes(columns.stream()
                    .map(column
                            -> new BoardData(column.getId(), column.getName(), getTask(column.getId()), column.getOrderColumn())
                    )
                    .collect(Collectors.toList()));
            return ResponseEntity.ok(boardResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Board not exist"));
        }
    }

    @GetMapping("/workSpace")
    public ResponseEntity<?> getWorkSpace() {
        List<Space> spaces = spaceRepository.findAll();

        if (spaces.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("work is empty"));
        }

        SpaceDataResponse response = new SpaceDataResponse(
                spaces.stream().map(space
                        -> new WorkSpace(space.getId(), space.getName(), space.getCreationDate())
                )
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(response);

    }

    @GetMapping("/workSpace/{workspaceId}")
    public ResponseEntity<?> getWorkSpaceById(@PathVariable Long workspaceId) {
        Space sp = spaceRepository.findById(workspaceId).orElse(null);
        Set<Board> spaces = boardRepository.findBySpace(sp).orElse(null);

        if (spaces.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("work is empty"));
        }

        SpaceDataResponse response = new SpaceDataResponse(
                spaces.stream().map(board
                        -> new WorkSpace(board.getId(), board.getName(), board.getCreationDate())
                )
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBoard")
    public ResponseEntity<?> updateBoard(@Valid @RequestBody BoardDataRequest boardRequest) {
        List<BoardData> boardData = boardRequest.getBoardDataReq();
        boardData.forEach(board -> {
            columnService.updateTaskListColumn(board);
            board.getTasks().forEach(
                    taskData -> taskService.updateTaskListColumn(taskData, board.getId())
            );
        });

        return ResponseEntity.ok("Board is updated!!");
    }

    private static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return username;
    }

    private List<TaskData> getTask(Long columnId) {
        Set<Task> tasks = taskRepository.findByListColumnId(columnId).orElse(null);
        return tasks.stream()
                .map(task -> {
                    return new TaskData(task.getId(), task.getTitle(),
                            task.getDescription(), getDateInString(task.getCreationDate()), getDateInString(task.getExpirationDate()),
                            task.getOrderTask(), getExpired());
                })
                .collect(Collectors.toList());
    }

    private Integer getExpired() {
        return 1;
    }

    private String getDateInString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);

    }

}
