package de.hatoka.poker.table.internal.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEventDao extends JpaRepository<GameEventPO, Long>
{
    public List<GameEventPO> getByTableidAndGameno(Long tableId, Long gameNo);
}