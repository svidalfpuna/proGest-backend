package py.com.una.progest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.una.progest.models.Board;

import java.util.Optional;
import java.util.Set;
import py.com.una.progest.models.Space;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByName(String name);

    Optional<Set<Board>> findBySpace(Space space);

    Boolean existsByName(String name);

}
