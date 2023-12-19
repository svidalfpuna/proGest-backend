package py.com.una.progest.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import py.com.una.progest.models.BoardData;
import py.com.una.progest.models.TaskData;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardDataRequest {
	private List<BoardData> boardDataReq = new ArrayList<>();

}
