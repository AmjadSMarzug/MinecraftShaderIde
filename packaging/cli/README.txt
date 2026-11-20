Shader Build - command line builder for Shader IDE projects

Builds a project folder (the one with project.json) into a resource pack.
It needs Java 21 or newer.

Install
  Linux and macOS:  sh install.sh          (installs to /usr/local, asks for sudo if needed)
  Windows:          powershell -ExecutionPolicy Bypass -File install.ps1
Then open a new terminal and run:  shader-build --help

Use
  shader-build path/to/project             obfuscated pack
  shader-build --readable path/to/project  readable names
  shader-build --no-check                  skip the glslang check
  shader-build --skript                    also write the Skript file

Settings file
  build.json in the project folder sets the build defaults. Every key is optional:
    {"obfuscate": true, "check": true, "skript": false, "remap": true}
  Command line flags override it. A typo in a key stops the build with a message.

Item model per effect
  Each effect in project.json can set its item shape with "model":
    "cube", "cylinder" (default) or "circle"
  Example: {"id": "sky", "file": "effects/sky.glsl", "alpha": 10, "item": "sky_1", "model": "circle", "defines": {}}

Putting the names back
  Obfuscated builds write <name>.remap.json into the project's build folder.
  To read a shader again with its original names:
    shader-build --unmap "build/Name.remap.json" shader.glsl
  The result is printed to the screen. Save it with > to keep it.

Remove
  Linux and macOS:  sh uninstall.sh
  Windows:          powershell -ExecutionPolicy Bypass -File uninstall.ps1
