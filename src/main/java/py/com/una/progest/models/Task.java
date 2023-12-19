package py.com.una.progest.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;


    @NotNull
    private String description;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date expirationDate;

    @NotNull
    private Integer orderTask;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private ListColumn listColumn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
