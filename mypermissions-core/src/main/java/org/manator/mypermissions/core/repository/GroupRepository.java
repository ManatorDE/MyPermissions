package org.manator.mypermissions.core.repository;

import org.manator.mypermissions.core.domain.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Optional<Group> findByName(String name);
    List<Group> findAll();
    void save(Group group);
    void delete(String name);
}
