package py.com.una.progest.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTaskRequest {
	@NotNull
	private Integer board;
	@NotBlank
	private String title;

	private String description;

	private Integer order;

	//private Date expirationDate;
}
