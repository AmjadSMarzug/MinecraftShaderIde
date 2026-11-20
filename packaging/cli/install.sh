#!/bin/sh
# installs shader-build so it runs from any folder (Linux and macOS)
# usage: sh install.sh [prefix]     default prefix /usr/local, sudo is used when that folder needs it
set -e
# sudo is only needed when the first folder that already exists above the target isn't writable
needs_sudo() {
    d="$1"
    while [ ! -e "$d" ]; do d=$(dirname "$d"); done
    [ "$(id -u)" -ne 0 ] && [ ! -w "$d" ]
}
HERE=$(cd "$(dirname "$0")" && pwd)
PREFIX="${1:-/usr/local}"

if ! command -v java >/dev/null 2>&1; then
    echo "warning: java isn't installed. shader-build needs Java 21 or newer."
fi

SUDO=""
if needs_sudo "$PREFIX/bin"; then SUDO=sudo; fi

$SUDO mkdir -p "$PREFIX/share/shader-build" "$PREFIX/bin"
$SUDO cp "$HERE/shader-build.jar" "$PREFIX/share/shader-build/shader-build.jar"

LAUNCHER=$(mktemp)
printf '#!/bin/sh\nexec java -jar "%s/share/shader-build/shader-build.jar" "$@"\n' "$PREFIX" > "$LAUNCHER"
$SUDO install -m 755 "$LAUNCHER" "$PREFIX/bin/shader-build"
rm -f "$LAUNCHER"

echo "installed: $PREFIX/bin/shader-build"
case ":$PATH:" in
    *":$PREFIX/bin:"*) echo "run: shader-build --help" ;;
    *) echo "warning: $PREFIX/bin isn't on your PATH. Add it to your shell profile." ;;
esac
