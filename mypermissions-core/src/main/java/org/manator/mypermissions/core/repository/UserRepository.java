package org.manator.mypermissions.core.repository;

import org.manator.mypermissions.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByUuid(UUID uuid);
    void save(User user);
    void delete(UUID uuid);
}
