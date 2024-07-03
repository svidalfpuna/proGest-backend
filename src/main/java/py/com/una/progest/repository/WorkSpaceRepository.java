package py.com.una.progest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.una.progest.models.Board;

import java.util.Optional;
import py.com.una.progest.models.Space;

@Repository
public interface WorkSpaceRepository extends JpaRepository<Space, Long> {
    

}
