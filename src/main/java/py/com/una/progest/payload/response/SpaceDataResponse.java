package py.com.una.progest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import py.com.una.progest.models.BoardData;
import py.com.una.progest.models.WorkSpace;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceDataResponse {
    private List<WorkSpace> workSpaces = new ArrayList<>();

}
