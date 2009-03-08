package de.hatoka.poker.table.internal.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TableDao extends JpaRepository<TablePO, Long>
{
    public Optional<TablePO> findByName(String name);
}