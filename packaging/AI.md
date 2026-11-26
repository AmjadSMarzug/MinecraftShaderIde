# Using Shader IDE from an AI tool

Shader IDE has a built-in MCP server. It lets an AI tool list a project's effects, read and write
their code, check them with glslang, build the resource pack, and look at a preview picture.

Start it with the app's `--mcp` option. With the installed RPM the command is `shader-ide --mcp`.

## Claude Code

    claude mcp add shader-ide -- shader-ide --mcp

## Codex

Add this to `~/.codex/config.toml`:

    [mcp_servers.shader-ide]
    command = "shader-ide"
    args = ["--mcp"]

## Other MCP clients (for example Antigravity)

Add a stdio server with command `shader-ide` and argument `--mcp`, using the client's MCP settings.

## Tools

- `list_effects` (project)
- `read_effect` (project, effect)
- `write_effect` (project, effect, source): saves and checks the code
- `check_effect` (project, effect)
- `build_pack` (project)
- `preview_effect` (project, effect): returns a picture; needs a display

`project` is the folder that holds `project.json`.
