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

Remove
  Linux and macOS:  sh uninstall.sh
  Windows:          powershell -ExecutionPolicy Bypass -File uninstall.ps1
