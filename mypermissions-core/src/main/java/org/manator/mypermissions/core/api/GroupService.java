package org.manator.mypermissions.core.api;

import org.manator.mypermissions.core.domain.Group;
import org.manator.mypermissions.core.domain.Permission;

import java.util.List;

public interface GroupService {
    List<Group> getParentGroups(String group);
    int getPriority(String name);
    boolean setPriority(int priority);
}
