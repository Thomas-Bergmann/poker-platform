package de.hatoka.poker.table.internal.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatDao extends JpaRepository<SeatPO, Long>
{
    public List<SeatPO> findByTableid(Long tableId);
    public Optional<SeatPO> findByTableidAndPosition(Long tableId, Integer position);
}