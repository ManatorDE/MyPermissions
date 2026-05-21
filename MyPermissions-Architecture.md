# MyPermissions — Architecture Overview

> **Core principle:** Simple for the user, clean for the developer.  
> One jar. No dependencies. No compromises on functionality.

---

## 1. Module Overview

```
mypermissions-core
mypermissions-webui           (included in all platform jars)
mypermissions-spigot          (shaded → MyPermissions-Spigot.jar)
mypermissions-multiworld      (shaded → MyPermissions-Multiworld.jar)
mypermissions-folia           (shaded → MyPermissions-Folia.jar)
mypermissions-fabric          (shaded → MyPermissions-Fabric.jar)
mypermissions-forge           (shaded → MyPermissions-Forge.jar)
```

### Dependency Graph

```
                              mypermissions-core
               ↑                    ↑          ↑             ↑
  mypermissions-spigot    mypermissions-fabric    mypermissions-forge
          ↑
  mypermissions-folia
          ↑
  mypermissions-multiworld

  mypermissions-webui  →  mypermissions-core (API only)
          ↑ embedded in every platform adapter
```

- `core` has **zero** platform dependencies — pure Java
- `webui` knows only the Core API (`PermissionService`, `UserService`, `GroupService`) — no platform code
- `spigot` knows `core`, `webui`, and the Bukkit/Spigot API — provides the `SchedulerAdapter` abstraction
- `folia` extends `spigot`, replaces `SchedulerAdapter` with Folia's regional scheduler
- `multiworld` extends `folia` (for the Folia jar) or `spigot` (for the Spigot jar)
- `fabric` knows `core`, `webui`, and the Fabric API — wraps the Fabric Permissions API
- `forge` knows `core`, `webui`, and the Forge API — wraps Forge Permissions
- Users install **one** jar only — never multiple simultaneously

### Distribution Matrix

| Jar | Platform | Multiworld | Vault | Web UI | Use case |
|---|---|---|---|---|---|
| `MyPermissions-Spigot.jar` | Spigot / Paper | ✗ | ✓ | ✓ | Standard Spigot server |
| `MyPermissions-Multiworld.jar` | Spigot / Paper | ✓ | ✓ | ✓ | Multi-world Spigot server |
| `MyPermissions-Folia.jar` | Folia | ✗ | ✗ | ✓ | Folia dedicated server |
| `MyPermissions-Fabric.jar` | Fabric | ✗ | ✗ | ✓ | Fabric dedicated server |
| `MyPermissions-Forge.jar` | Forge | ✗ | ✗ | ✓ | Forge dedicated server |

> **Note:** Vault is not supported on Folia. Vault assumes a single main thread; Folia's regional multithreading makes this incompatible by design. This is a known limitation of the Vault API itself, not MyPermissions.

---

## 2. Module: `mypermissions-core`

Platform-independent domain logic. No Bukkit, Fabric, or Forge imports anywhere.

### Package Structure

```
dev.mypermissions.core
├── api
│   ├── PermissionService.java          # Primary interface for permission queries
│   ├── UserService.java                # CRUD for users
│   └── GroupService.java               # CRUD for groups
├── domain
│   ├── User.java                       # Data class: UUID, group(s), permissions
│   ├── Group.java                      # Data class: name, parents, permissions, priority
│   ├── Permission.java                 # Single permission entry (node, value)
│   └── InheritanceResolver.java        # Group inheritance resolution logic
├── repository
│   ├── UserRepository.java             # Port (interface) for user persistence
│   └── GroupRepository.java            # Port (interface) for group persistence
├── service
│   ├── PermissionServiceImpl.java
│   ├── UserServiceImpl.java
│   └── GroupServiceImpl.java
└── util
    └── PermissionNode.java             # Helpers: wildcards, negation, node parsing
```

### Data Model

```java
// Immutable core objects
record Permission(String node, boolean value) {}

record Group(
    String name,
    List<String> parents,       // Multiple inheritance supported
    int priority,
    List<Permission> permissions
) {}

record User(
    UUID uuid,
    String name,
    List<String> groups,        // Primary group + additional groups
    List<Permission> permissions // User-specific overrides
) {}
```

### Resolution Order (Core)

```
1. User-specific permission  (direct override)
2. User's primary group
3. Parent groups             (by priority, recursive)
4. Default group
```

### Repository Ports

```java
// The core does not care about persistence — it only defines the interface
interface UserRepository {
    Optional<User> findByUuid(UUID uuid);
    void save(User user);
    void delete(UUID uuid);
}

interface GroupRepository {
    Optional<Group> findByName(String name);
    List<Group> findAll();
    void save(Group group);
    void delete(String name);
}
```

---

## 3. Module: `mypermissions-webui`

Platform-independent embedded web interface. Provides full read/write configuration of all MyPermissions data via a REST API and a browser-based frontend. Knows only the Core API — no platform code. Embedded in every platform adapter jar. **Disabled by default** — enabled explicitly via `config.yml`.

**Technology stack:** JDK `com.sun.net.httpserver.HttpServer` + Gson (~250KB). No Jetty, no Kotlin stdlib, no framework. Zero external HTTP dependencies — full control, minimal jar footprint.

### Design

```
Browser  →  HTTP  →  HttpServer (JDK built-in)
                         ↓
                    TokenAuthFilter         # HttpFilter: rejects invalid token
                         ↓
                    RouterHandler           # Dispatches method + path to handler
                         ↓
                    Route Handlers          # Maps routes to Core service calls
                         ↓
              PermissionService / UserService / GroupService  (core)
```

The web server starts only if `webui.enabled: true` in `config.yml`. When disabled, `HttpServer` is never instantiated — zero runtime overhead. On first enable, a token is auto-generated via `SecureRandom` and written back to `config.yml`. The port and bind address are configurable. All routes return `401` without a valid token.

### Package Structure

```
dev.mypermissions.webui
├── WebServer.java                      # HttpServer setup, lifecycle (start/stop)
├── auth
│   └── TokenAuthFilter.java            # HttpFilter: validates Authorization: Bearer <token>
├── router
│   └── RouterHandler.java              # Dispatches (method + path) → handler
├── handler
│   ├── UserHandler.java                # /api/users/**
│   ├── GroupHandler.java               # /api/groups/**
│   └── StatusHandler.java              # /api/status, /api/reload
├── dto
│   ├── UserDto.java                    # JSON shape for User (Gson)
│   ├── GroupDto.java                   # JSON shape for Group (Gson)
│   └── PermissionDto.java              # JSON shape for Permission (Gson)
├── util
│   └── HttpUtil.java                   # sendJson(), sendError(), readBody() helpers
└── config
    └── WebConfig.java                  # Port, bind, token, enabled flag
```

### Routing

The JDK HttpServer has no built-in router — `RouterHandler` handles dispatch manually. All traffic is registered under a single context (`/`):

```java
class RouterHandler implements HttpHandler {
    void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();   // GET, POST, PUT, DELETE
        String path   = exchange.getRequestURI().getPath(); // /api/users/550e84...

        if (method.equals("GET") && path.equals("/api/groups"))         handleListGroups(exchange);
        else if (method.equals("POST") && path.equals("/api/groups"))   handleCreateGroup(exchange);
        else if (method.equals("GET") && path.startsWith("/api/users/")) handleGetUser(exchange, extractUuid(path));
        // ...
        else HttpUtil.sendError(exchange, 404, "Not found");
    }
}
```

### REST API

All endpoints require the header `Authorization: Bearer <token>`.
All request and response bodies are JSON.

**Users**

| Method | Route | Description |
|---|---|---|
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{uuid}` | Get a single user |
| `PUT` | `/api/users/{uuid}/groups` | Set groups for a user |
| `PUT` | `/api/users/{uuid}/permissions` | Set permission overrides for a user |
| `DELETE` | `/api/users/{uuid}` | Remove user data |

**Groups**

| Method | Route | Description |
|---|---|---|
| `GET` | `/api/groups` | List all groups |
| `GET` | `/api/groups/{name}` | Get a single group |
| `POST` | `/api/groups` | Create a group |
| `PUT` | `/api/groups/{name}` | Update a group (parents, priority, permissions) |
| `DELETE` | `/api/groups/{name}` | Delete a group |

**Server**

| Method | Route | Description |
|---|---|---|
| `GET` | `/api/status` | Plugin version, platform, uptime, online players |
| `POST` | `/api/reload` | Reload config and YAML data from disk |

### Authentication & Lifecycle

```yaml
# config.yml
webui:
  enabled: false          # false by default — must be explicitly enabled
  port: 8080
  bind: 0.0.0.0           # use 127.0.0.1 to restrict to local access only
  token: ""               # auto-generated on first enable, do not share
```

**Lifecycle:**
- `enabled: false` (default) — `HttpServer` is never instantiated, no port is opened, zero overhead
- `enabled: true`, no token — token generated via `SecureRandom` (256-bit hex), written to `config.yml`, then server starts
- `enabled: true`, token present — server starts immediately with the existing token
- Changing `enabled` at runtime requires `/permissions reload` or a server restart

Token validation runs as an `HttpFilter` before every handler:

```java
class TokenAuthFilter implements Filter {
    void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.equals("Bearer " + config.token())) {
            HttpUtil.sendError(exchange, 401, "Unauthorized");
            return;
        }
        chain.doFilter(exchange);
    }
}
```

### Error Responses

All errors return a consistent JSON shape:

```json
{ "error": "Group 'admin' not found" }
```

HTTP status codes: `200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `404 Not Found`, `500 Internal Server Error`.

### Frontend

A lightweight single-page frontend (HTML + vanilla JS, no framework) is served from the `StaticHandler` as classpath resources embedded in the jar. It calls the REST API directly and requires no build step or external hosting. Server admins access it at `http://<server-ip>:<port>/`.

The frontend is maintained in a separate directory (`webui-frontend/`) within the repository, making it accessible to contributors without Java knowledge.

### Security Considerations

- The web server binds to `0.0.0.0` by default — admins should bind to `127.0.0.1` via config if remote access is not needed
- Token is stored in plaintext in `config.yml` — file permissions on the server are the admin's responsibility
- HTTPS is out of scope; admins requiring TLS should place a reverse proxy (nginx, Caddy) in front

---

## 4. Module: `mypermissions-spigot`

Bukkit/Spigot adapter. Implements the repository ports, integrates Vault, exports as a **shaded jar**.

### Package Structure

```
dev.mypermissions.spigot
├── MyPermissionsPlugin.java            # Entry point (JavaPlugin)
├── adapter
│   ├── BukkitPermissionAdapter.java    # Applies Bukkit permissions at runtime
│   └── VaultPermissionAdapter.java     # Vault integration (Permission + Chat)
├── command
│   ├── PermissionCommand.java
│   ├── GroupCommand.java
│   └── UserCommand.java
├── listener
│   ├── PlayerLoginListener.java        # Load/cache user on login
│   └── PlayerQuitListener.java         # Evict cache on quit
├── persistence
│   ├── YamlUserRepository.java         # Implements UserRepository → YAML
│   ├── YamlGroupRepository.java        # Implements GroupRepository → YAML
│   └── config
│       └── ConfigManager.java
├── scheduler
│   ├── SchedulerAdapter.java           # Interface: async, sync, entity-bound tasks
│   └── BukkitSchedulerAdapter.java     # Default implementation via BukkitScheduler
└── service
    └── ServiceLocator.java             # Registers API as a Bukkit service
```

### Scheduler Abstraction

The `SchedulerAdapter` interface is the critical extension point that enables the Folia module. It abstracts all task scheduling so the rest of the Spigot module never touches `BukkitScheduler` directly.

```java
interface SchedulerAdapter {
    void runAsync(Runnable task);
    void runSync(Runnable task);
    void runForEntity(Entity entity, Runnable task);
    void runAsyncRepeating(Runnable task, long delayTicks, long periodTicks);
}

// Default implementation — used by mypermissions-spigot
class BukkitSchedulerAdapter implements SchedulerAdapter {
    // delegates to Bukkit.getScheduler()
}
```

### Persistence

Default: **YAML** (human-readable, manually editable without a server restart).

```yaml
# groups.yml
groups:
  admin:
    priority: 100
    parents: [moderator]
    permissions:
      - mypermissions.admin: true
  moderator:
    priority: 50
    parents: [default]
    permissions:
      - mypermissions.kick: true
  default:
    priority: 0
    permissions:
      - mypermissions.chat: true

# users.yml
users:
  550e8400-e29b-41d4-a716-446655440000:
    name: Notch
    groups: [admin]
    permissions:
      - some.special.node: true
```

### Vault Integration

```
VaultPermissionAdapter
  implements net.milkbowl.vault.permission.Permission
  implements net.milkbowl.vault.chat.Chat
  → delegates internally to PermissionService (core)
```

---

## 5. Module: `mypermissions-multiworld`

Full Spigot adapter **plus** world-context logic. Standalone shaded jar — no separate MyPermissions-Spigot.jar required.

### Package Structure

```
dev.mypermissions.multiworld
├── MyPermissionsMultiworldPlugin.java  # Entry point (JavaPlugin)
├── domain
│   ├── WorldUser.java                  # Extends User with per-world overrides
│   └── WorldGroup.java                 # Extends Group with per-world overrides
├── resolver
│   └── WorldPermissionResolver.java    # Resolution logic with world context
├── persistence
│   ├── YamlWorldUserRepository.java
│   └── YamlWorldGroupRepository.java
└── listener
    └── WorldChangeListener.java        # Re-evaluates permissions on world change
```

### Extended Data Model

```java
// Multiworld extends the core model — the core knows nothing about this
record WorldOverride(String world, List<Permission> permissions) {}

record WorldUser(
    User base,                          // Core user as-is
    List<WorldOverride> worldOverrides
) {}

record WorldGroup(
    Group base,                         // Core group as-is
    List<WorldOverride> worldOverrides
) {}
```

### Resolution Order (Multiworld)

```
1. Per-world user permission   (current world context)
2. Global user permission          ┐
3. Per-world group permission      │ via core resolver
4. Global group permission         ┘
```

### Persistence

```yaml
# users.yml (Multiworld)
users:
  550e8400-e29b-41d4-a716-446655440000:
    name: Notch
    groups: [admin]
    permissions:
      - some.global.node: true
    worlds:
      world_nether:
        permissions:
          - mypermissions.fly: false    # Negation overrides global
      world_end:
        groups: [moderator]             # Different group only in this world
```

---

## 6. Module: `mypermissions-folia`

Folia adapter. Extends `mypermissions-spigot` and replaces `BukkitSchedulerAdapter` with Folia's regional scheduler system. Exports as a **shaded standalone jar** — no Spigot jar required.

### Design Decision: Extend, Don't Duplicate

Folia's API is a superset of Paper's API. Rather than duplicating all Spigot adapter code, `mypermissions-folia` extends `mypermissions-spigot` and overrides only the scheduling layer. All listeners, commands, persistence, and core integration remain identical.

```
mypermissions-spigot  (base: all adapter logic, BukkitSchedulerAdapter)
          ↑
mypermissions-folia   (overrides: FoliaSchedulerAdapter, thread-safe cache)
```

### The Core Problem: No Single Main Thread

Folia replaces the global main thread with per-region threads. Every operation must run on the correct regional thread for the entity or chunk it concerns. `BukkitScheduler` throws `UnsupportedOperationException` at runtime on Folia.

```
Folia Schedulers:
  GlobalRegionScheduler   → server-wide tasks (no world/entity binding)
  RegionScheduler         → chunk- and world-bound tasks
  EntityScheduler         → per-entity tasks (e.g. per player)
```

### Package Structure

```
dev.mypermissions.folia
├── MyPermissionsFoliaPlugin.java       # Entry point — overrides MyPermissionsPlugin
├── scheduler
│   └── FoliaSchedulerAdapter.java      # Implements SchedulerAdapter via Folia APIs
└── cache
    └── ThreadSafeUserCache.java        # ConcurrentHashMap-backed user cache
```

### Folia Scheduler Adapter

```java
class FoliaSchedulerAdapter implements SchedulerAdapter {

    // Server-wide async work (YAML I/O, cache population)
    void runAsync(Runnable task) {
        plugin.getServer()
              .getAsyncScheduler()
              .runNow(plugin, t -> task.run());
    }

    // Player-bound operations (apply permissions, cache eviction)
    void runForEntity(Entity entity, Runnable task) {
        entity.getScheduler()
              .run(plugin, t -> task.run(), null);
    }

    // Repeating background tasks (e.g. timed permission expiry)
    void runAsyncRepeating(Runnable task, long delayTicks, long periodTicks) {
        plugin.getServer()
              .getAsyncScheduler()
              .runAtFixedRate(plugin, t -> task.run(), delayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS);
    }
}
```

### Thread Safety Requirements

Because Folia runs multiple regional threads concurrently, the user cache must be thread-safe. `HashMap` is replaced with `ConcurrentHashMap` in the Folia module. No locks required for reads — only atomic write operations on login/logout.

```java
class ThreadSafeUserCache {
    private final ConcurrentHashMap<UUID, User> cache = new ConcurrentHashMap<>();

    // Called from EntityScheduler on player login (regional thread)
    void put(UUID uuid, User user)  { cache.put(uuid, user); }

    // Called from EntityScheduler on player quit (regional thread)
    void evict(UUID uuid)           { cache.remove(uuid); }

    // Called from permission check (any thread — read-only, safe)
    Optional<User> get(UUID uuid)   { return Optional.ofNullable(cache.get(uuid)); }
}
```

### plugin.yml

```yaml
folia-supported: true
```

This flag is required — Folia refuses to load plugins that do not explicitly declare Folia support.

### Vault Support

**Vault is not supported on Folia.** Vault's permission API assumes synchronous calls on a single main thread. This is architecturally incompatible with Folia's regional multithreading and cannot be worked around safely. MyPermissions-Folia exposes its own `PermissionService` via a `ServiceLocator` for other plugins that wish to integrate directly.

---

## 7. Module: `mypermissions-fabric`

Fabric adapter. Wraps the [Fabric Permissions API](https://github.com/lucko/fabric-permissions-api) and delegates to the core for user/group management. Exports as a **shaded jar** (Fabric mod).

### Design Decision: Wrapper Approach

MyPermissions does **not** replace Fabric's permission system — it wraps it. Other mods continue to query the Fabric Permissions API as usual. MyPermissions provides the backing implementation and adds user/group management on top.

```
Fabric Permissions API  (other mods query this)
          ↓
FabricPermissionProvider  (MyPermissions registers this)
          ↓
mypermissions-core  (PermissionService, InheritanceResolver)
          ↓
YamlUserRepository / YamlGroupRepository
```

### Package Structure

```
dev.mypermissions.fabric
├── MyPermissionsFabricMod.java         # Entry point (ModInitializer)
├── adapter
│   └── FabricPermissionProvider.java   # Implements Fabric Permissions API
├── command
│   ├── PermissionCommand.java          # Registered via CommandRegistrationCallback
│   ├── GroupCommand.java
│   └── UserCommand.java
├── listener
│   ├── PlayerLoginCallback.java        # ServerPlayConnectionEvents.JOIN
│   └── PlayerLeaveCallback.java        # ServerPlayConnectionEvents.DISCONNECT
├── persistence
│   ├── YamlUserRepository.java         # Same port as Spigot — different adapter
│   ├── YamlGroupRepository.java
│   └── config
│       └── ConfigManager.java
└── service
    └── ServiceLocator.java             # Makes core services accessible to other mods
```

### Fabric-Specific Notes

- Entry point declared in `fabric.mod.json` via the `main` entrypoint key
- Commands registered via `CommandRegistrationCallback` (Fabric API)
- Player events via `ServerPlayConnectionEvents.JOIN` / `DISCONNECT`
- Permission checks via `Permissions.check(player, node)` (Fabric Permissions API)
- No Vault equivalent on Fabric — no chat adapter layer needed
- Dimension-awareness (Fabric equivalent of worlds) is **out of scope for Phase 1**

### `fabric.mod.json` (excerpt)

```json
{
  "id": "mypermissions",
  "environment": "server",
  "entrypoints": {
    "main": ["dev.mypermissions.fabric.MyPermissionsFabricMod"]
  },
  "depends": {
    "fabricloader": ">=0.15.0",
    "fabric-api": "*",
    "fabric-permissions-api": "*"
  }
}
```

---

## 8. Module: `mypermissions-forge`

Forge adapter. Wraps Forge's permission system and delegates to the core. Exports as a **shaded jar** (Forge mod).

### Design Decision: Wrapper Approach

Same principle as Fabric — MyPermissions wraps Forge Permissions, not replaces them. Other mods query Forge Permissions as usual; MyPermissions provides the backing data.

```
Forge Permission system  (other mods query this)
          ↓
ForgePermissionHandler  (MyPermissions registers this)
          ↓
mypermissions-core  (PermissionService, InheritanceResolver)
          ↓
YamlUserRepository / YamlGroupRepository
```

### Package Structure

```
dev.mypermissions.forge
├── MyPermissionsForgemMod.java         # Entry point (@Mod)
├── adapter
│   └── ForgePermissionHandler.java     # Registered via Forge PermissionAPI
├── command
│   ├── PermissionCommand.java          # Registered via RegisterCommandsEvent
│   ├── GroupCommand.java
│   └── UserCommand.java
├── listener
│   ├── PlayerLoginHandler.java         # PlayerLoggedInEvent
│   └── PlayerLeaveHandler.java         # PlayerLoggedOutEvent
├── persistence
│   ├── YamlUserRepository.java
│   ├── YamlGroupRepository.java
│   └── config
│       └── ConfigManager.java
└── service
    └── ServiceLocator.java
```

### Forge-Specific Notes

- Entry point via `@Mod` annotation, setup in `FMLCommonSetupEvent`
- Commands registered via `RegisterCommandsEvent`
- Player events via `PlayerLoggedInEvent` / `PlayerLoggedOutEvent`
- Permission handler registered via `PermissionAPI.setPermissionHandler()`
- Dimension-awareness is **out of scope for Phase 1**

### `mods.toml` (excerpt)

```toml
[[dependencies.mypermissions]]
    modId = "forge"
    mandatory = true
    versionRange = "[47,)"
    ordering = "NONE"
    side = "SERVER"
```

---

## 9. Shared Patterns Across All Adapters

Every platform adapter follows the same structure — only the platform-specific APIs differ.

| Concern | Spigot / Multiworld | Folia | Fabric | Forge |
|---|---|---|---|---|
| Entry point | `JavaPlugin` | `JavaPlugin` (folia-supported) | `ModInitializer` | `@Mod` class |
| Commands | Bukkit `CommandExecutor` | Bukkit `CommandExecutor` | `CommandRegistrationCallback` | `RegisterCommandsEvent` |
| Player login | `PlayerLoginEvent` | `PlayerLoginEvent` + `EntityScheduler` | `ServerPlayConnectionEvents.JOIN` | `PlayerLoggedInEvent` |
| Player quit | `PlayerQuitEvent` | `PlayerQuitEvent` + `EntityScheduler` | `ServerPlayConnectionEvents.DISCONNECT` | `PlayerLoggedOutEvent` |
| Permission hook | Vault + Bukkit | ServiceLocator only (no Vault) | Fabric Permissions API | Forge PermissionAPI |
| Scheduler | `BukkitScheduler` | `GlobalRegionScheduler` / `EntityScheduler` | Fabric threading model | Forge threading model |
| User cache | `HashMap` | `ConcurrentHashMap` | `HashMap` | `HashMap` |
| Persistence | YAML (shared format) | YAML (shared format) | YAML (shared format) | YAML (shared format) |
| Core usage | `PermissionService` | `PermissionService` | `PermissionService` | `PermissionService` |

**The YAML format is identical across all platforms.** A server owner migrating from Spigot to Fabric can reuse their `users.yml` and `groups.yml` without modification.

---

## 10. Build Configuration (Gradle)

```
settings.gradle
├── mypermissions-core              (java-library, no shading)
├── mypermissions-webui             (java-library, no shading — embedded by platform adapters)
├── mypermissions-spigot            (shadow jar → MyPermissions-Spigot.jar)
├── mypermissions-multiworld        (shadow jar → MyPermissions-Multiworld.jar)
├── mypermissions-folia             (shadow jar → MyPermissions-Folia.jar)
├── mypermissions-fabric            (shadow jar → MyPermissions-Fabric.jar)
└── mypermissions-forge             (shadow jar → MyPermissions-Forge.jar)
```

All shaded jars use **package relocation** for any bundled dependencies to avoid classloader conflicts with other plugins or mods.

---

## 11. Development Phases

### Phase 1 — Foundation (Spigot)
- `mypermissions-core` — domain model, services, repository ports
- `mypermissions-webui` — embedded REST API, token auth, static frontend, Javalin
- `mypermissions-spigot` — Bukkit adapter, Vault integration, YAML persistence, commands, `SchedulerAdapter` abstraction
- `mypermissions-multiworld` — world-context extension on top of Spigot

### Phase 1.5 — Folia
- `mypermissions-folia` — extends Spigot module, replaces scheduler, thread-safe cache
- Core, WebUI, and persistence layers are untouched — purely additive
- Target: shortly after Phase 1 while the codebase is fresh

### Phase 2 — Platform Expansion
- `mypermissions-fabric` — Fabric adapter, wraps Fabric Permissions API, embeds WebUI
- `mypermissions-forge` — Forge adapter, wraps Forge PermissionAPI, embeds WebUI
- Core interfaces remain unchanged — new adapters are purely additive

### Phase 3 — Potential Extensions *(not committed)*
- Dimension-aware permissions for Fabric/Forge (parallel to Multiworld)
- Database persistence backend (SQLite / MySQL) as an alternative to YAML
- BungeeCord / Velocity adapter for proxy-level permissions
- HTTPS support via reverse-proxy documentation

---

## 12. Decision Log

| Topic | Decision | Rationale |
|---|---|---|
| Build system | Gradle | Multi-module support, Shadow plugin, Minecraft toolchain compatibility |
| Persistence | YAML | Human-readable, no database dependency, portable across platforms |
| Distribution | Shaded jar per platform | No API plugin required, zero installation friction |
| Multiworld | Separate module | Clean separation, no overhead for users who don't need it |
| Vault | Spigot / Multiworld only | Incompatible with Folia's threading model by design |
| Folia | Extends Spigot module | No code duplication; only scheduler and cache are overridden |
| Scheduler abstraction | `SchedulerAdapter` in Spigot | Required extension point for Folia; keeps Spigot module clean |
| Thread-safe cache | Folia only | Overhead not justified on single-threaded Spigot/Paper |
| Fabric/Forge | Wrapper approach | Non-invasive; other mods remain fully unaffected |
| World context in core | No | Core stays lean; context is the adapter's responsibility |
| YAML format | Shared across platforms | Enables data portability when migrating between platforms |
| Fabric/Forge dimensions | Out of scope (Phase 1) | Requires deeper platform knowledge; revisit in Phase 2 |
| Web UI | Core feature, embedded in all jars | Central config interface; no separate install required |
| Web UI default state | Disabled (`enabled: false`) | No port opened unless explicitly opted in; safe default |
| Web UI HTTP stack | JDK HttpServer + Gson | Zero external HTTP dependencies; ~250KB vs ~5MB for Javalin |
| Web UI auth | Single token, Bearer header | Minimal complexity; single-admin use case |
| Web UI protocol | REST + Request/Response | No WebSocket complexity; sufficient for config management |
| Web UI frontend | Vanilla JS, embedded in jar | No build step; accessible to non-Java contributors |
| Web UI TLS | Out of scope | Delegated to reverse proxy (nginx, Caddy) by the admin |
