package org.manator.mypermissions.core.api;

import org.manator.mypermissions.core.domain.Group;
import org.manator.mypermissions.core.domain.Permission;

import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean addGroup(UUID uuid, Group group);
    boolean removeGroup(UUID uuid, Group group);

    List<Permission> getPermissions(UUID uuid);
}
