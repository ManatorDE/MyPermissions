package org.manator.mypermissions.core.api;

import org.manator.mypermissions.core.domain.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    /**
     * @param uuid Player UUID
     * @param permission Permission-Node
     * @return
     */
    boolean addPlayerPermission(UUID uuid, Permission permission);
    boolean addGroupPermission(String name, Permission permission);

    boolean removePlayerPermission(UUID uuid, Permission permission);
    boolean removeGroupPermission(String name, Permission permission);

    List<Permission> getPlayerPermissions(String name);
    List<Permission> getGroupPermissions(String name);
}
