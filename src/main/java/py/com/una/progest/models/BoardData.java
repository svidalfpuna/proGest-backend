package py.com.una.progest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardData {
    private Long id;
    private String name;
    private List<TaskData> tasks;
    private Integer order;

}
     