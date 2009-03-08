package de.hatoka.poker.player.internal.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerDao extends JpaRepository<PlayerPO, Long>
{
    public Optional<PlayerPO> findByNickname(String name);
    public List<PlayerPO> getByUserref(String globalUserRef);
}