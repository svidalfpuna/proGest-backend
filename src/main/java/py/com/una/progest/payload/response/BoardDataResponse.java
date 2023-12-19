package py.com.una.progest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import py.com.una.progest.models.BoardData;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardDataResponse {
    private List<BoardData> boardDataRes = new ArrayList<>();

}
