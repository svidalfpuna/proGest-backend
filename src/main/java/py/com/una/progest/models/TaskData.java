package py.com.una.progest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskData {
    private Long id;
    private String title;
    private String description;
    private String creationDate;
    private String expirationDate;
    //private String user;
    private Integer order;
    private Integer expired;
}
