# Shader IDE - Fork of [Mirothe's Hogyoku Vessel](https://github.com/Mirothe/Hogyoku-Vessel)

An IDE for Minecraft shaders. This fork is by Thesnowbound.

An IDE for Minecraft shaders. You write GLSL, watch it run live as you type, and Shader IDE builds it into a resource pack that works on every version from 1.21 to 26.3.

Minecraft's core shaders changed shape five times across those versions, with different uniforms, includes, fog and lighting each time. Shader IDE keeps a template for each one, drops your code into all of them, and compiles every result with glslang, so a mistake shows up in the editor instead of as a broken game.

## Features

- **Editor** with GLSL highlighting, errors marked on the line as you type, colours written in code shown in their own colour, undo and smart indent
- **Live preview** in OpenGL that recompiles on every change and lowers its own resolution when a shader gets heavy
- **Every version at once**: each effect goes into the 1.21, 1.21.6, 26.1, 26.2 and 26.3 shader layouts, with the right overlays and pack metadata
- **Shader check** with glslang for every variant Minecraft uses, with 26.3 also checked as Vulkan SPIR-V
- **Obfuscator** that renames functions and variables, splits numbers and adds decoy code, so shipped shaders are hard to copy
- **Five kinds of effect**: skies, objects floating inside an item, surfaces, materials that repaint an item's pixels, and overlays over the player's view
- **Skript output**: an optional `.sk` file with commands to show your effects in game
- **Lessons and examples** to learn from, plus themes and Discord status

## Writing an effect

An effect is one `.glsl` file with a main function. Which function it has decides what kind of effect it is:

| Kind | Main function |
|---|---|
| Sky | `vec3 sky(vec3 dir, float time)` |
| Object | `vec4 object(vec3 origin, vec3 dir, float time)` |
| Surface | `vec4 surface(vec2 uv, vec3 pos, vec3 normal, vec3 view, float time)` |
| Material | `vec4 material(vec4 texel, vec2 uv, vec3 normal, vec3 view, float time)` |
| Overlay | `vec4 overlay(vec2 screen, vec3 dir, float time)` |

The examples in `resources/examples/` show each kind, and the Learn tab in the app walks through them.

### Built-in functions

Common shader helpers come with the app. Call them directly and they're added for you, or import a module by name at the top of the file:

| Module | Functions |
|---|---|
| `hash` | `vHash1(float)`, `vHash2(vec2)`, `vHash3(vec3)`, `vHash22(vec2)` |
| `noise` | `vNoise(vec2)`, `vNoise3(vec3)` (uses `hash`) |
| `fbm` | `vFbm(vec2)`, `vFbm3(vec3)` (uses `noise`) |
| `voronoi` | `vVoronoi(vec2)`, `vVoronoiEdge(vec2)` (uses `hash`) |
| `rotate` | `vRotate(vec2, float)` |
| `palette` | `vPalette(float)` |
| `sdf` | `vSdSphere(vec3, float)`, `vSdBox(vec3, vec3)`, `vSdTorus(vec3, vec2)` |

```glsl
#import noise fbm palette

vec3 sky(vec3 dir, float time) {
    return vPalette(vFbm(dir.xz * 3.0 + time * 0.1)) * 0.5 + 0.2;
}
```

A call to a built-in function without an `#import` adds its module automatically. Functions you define yourself with the same name win over the built-in ones. Imports don't change the line numbers in the editor.

## Running

Shader IDE runs on Windows, Linux and macOS (Intel and Apple Silicon) with Java 21 or newer. The live preview needs OpenGL 3.3.

Every download on the [releases page](https://github.com/AmjadSMarzug/shaderIDE/releases/latest) comes with its own Java.

- **Windows**: run the installer. It adds a Start menu shortcut and keeps the app up to date.
- **macOS**: open the Apple Silicon or Intel disk image and drag the app onto the Applications folder. It isn't signed by Apple yet, so the first time, right-click it and choose Open.
- **Linux**: unpack the tar.gz and run `Shader IDE/bin/Shader IDE`.
- **Anything with Java 21**: `java -jar "Shader IDE.jar"`.

The shader check uses glslang. Windows has it built in; on Linux and macOS install it from your package manager (`sudo apt install glslang-tools`, `sudo pacman -S glslang` or `brew install glslang`). Without it packs still build, just unchecked.

On Linux, Discord status works with the regular, Flatpak and Snap builds of Discord.

## Building

You need a JDK 21 or newer with `javac`, `jar` and `jpackage` on the path, bash (Git Bash on Windows) and Python 3.

```sh
bash build.sh
```

That makes `out/dist/Shader IDE.jar`, which runs on every platform, and the app with its own Java for the platform you built on. A Mac or Linux app can only be built on that system, so `.github/workflows/release.yml` builds all of them on GitHub: push a tag like `v1.1.0` and the Windows, Linux and both Mac builds land on that release. The version comes from `Forge.VERSION` unless you set `VERSION=` yourself.

The installer is a small C# program built with the .NET Framework compiler that ships with Windows:

```sh
bash installer/build.sh owner/repo
```

## Layout

The code lives in `src/me/thesnowbound/shaderIDE/`, one folder per job:

| Folder | What's there |
|---|---|
| `Main.java` | the window, the workspace and everything that wires the parts together |
| `ui/` | the custom-painted parts (`Chrome`, `Fx`), the home pages (`Home`), the example picker (`Gallery`), the command palette (`Palette`) and themes (`Theme`) |
| `editor/` | the code editor (`CodeEditor`) and the colour wheel (`ColorWheel`) |
| `preview/` | the live OpenGL preview (`Preview`) |
| `build/` | building the pack (`Forge`), the obfuscator (`Obfuscator`) and the Skript output (`Skript`) |
| `project/` | projects on disk (`Project`) and the form that makes them (`ProjectForm`) |
| `learn/` | the lessons (`Lessons`), the guides (`Guides`), the task checks (`Checks`) and the Learn panel (`Learn`) |
| `presets/` | the starter effects and their thumbnails (`Templates`) |
| `library/` | the built in shader functions (`Library`) |
| `discord/` | Discord status (`Discord`) |
| `util/` | the JSON reader (`Json`) |
| `resources/tiers`, `resources/stubs` | the Minecraft shader templates for each version |
| `resources/examples` | example effects and their thumbnails |
| `site/` | the website, with a live shader editor that runs in the browser |

## License

MIT, see [LICENSE](LICENSE). The bundled libraries, the font and the Minecraft shader templates keep their own terms, listed in [THIRD_PARTY.md](THIRD_PARTY.md).
