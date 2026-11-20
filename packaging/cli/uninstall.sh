#!/bin/sh
# removes shader-build installed by install.sh
# usage: sh uninstall.sh [prefix]     default prefix /usr/local
set -e
# sudo is only needed when the first folder that already exists above the target isn't writable
needs_sudo() {
    d="$1"
    while [ ! -e "$d" ]; do d=$(dirname "$d"); done
    [ "$(id -u)" -ne 0 ] && [ ! -w "$d" ]
}
PREFIX="${1:-/usr/local}"
SUDO=""
if needs_sudo "$PREFIX/bin"; then SUDO=sudo; fi
$SUDO rm -f "$PREFIX/bin/shader-build"
$SUDO rm -rf "$PREFIX/share/shader-build"
echo "removed shader-build from $PREFIX"
