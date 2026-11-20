#!/bin/sh
# installs the command line builder so `shader-build` works from any folder
# usage: sh packaging/install-builder.sh [prefix]   (default prefix: /usr/local, may need sudo)
set -e
PREFIX="${1:-/usr/local}"
HERE="$(cd "$(dirname "$0")" && pwd)"
JAR="$HERE/../out/dist/shader-build.jar"
[ -f "$JAR" ] || { echo "no $JAR yet, run ./build.sh first"; exit 1; }
mkdir -p "$PREFIX/share/shader-build" "$PREFIX/bin"
cp "$JAR" "$PREFIX/share/shader-build/shader-build.jar"
cat > "$PREFIX/bin/shader-build" <<LAUNCHER
#!/bin/sh
exec java -jar "$PREFIX/share/shader-build/shader-build.jar" "\$@"
LAUNCHER
chmod +x "$PREFIX/bin/shader-build"
echo "installed: $PREFIX/bin/shader-build (try: shader-build --help)"
