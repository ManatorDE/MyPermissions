package org.manator.mypermissions.core.domain;

import java.util.List;

public record Group(String name, List<String> parents, int priority, List<Permission> permissions) {
}
