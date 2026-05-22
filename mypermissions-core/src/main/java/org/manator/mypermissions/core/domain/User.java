package org.manator.mypermissions.core.domain;

import java.util.List;
import java.util.UUID;

public record User(UUID uuid, String name, List<String> groups, List<Permission> permissions) {
}
