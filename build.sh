#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

# java separates classpath entries with ; on Windows and : everywhere else
case "$(uname -m)" in
    arm64|aarch64) ARCH=arm64 ;;
    *) ARCH=x64 ;;
esac
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=';'; OS=windows; ICON=packaging/icon.ico ;;
    Darwin) SEP=':'; OS=macos-$ARCH; ICON=out/icon.icns ;;
    *) SEP=':'; OS=linux-$ARCH; ICON=packaging/icon.png ;;
esac

rm -rf out
mkdir -p out/classes out/dist

# a Mac app wants its icon as an .icns, made here from the png with the tools every Mac has
if [ "$ICON" = out/icon.icns ]; then
    mkdir -p out/icon.iconset
    for s in 16 32 128 256 512; do
        sips -z $s $s packaging/icon.png --out out/icon.iconset/icon_${s}x${s}.png >/dev/null
        sips -z $((s * 2)) $((s * 2)) packaging/icon.png --out out/icon.iconset/icon_${s}x${s}@2x.png >/dev/null
    done
    iconutil -c icns out/icon.iconset -o out/icon.icns || ICON=
fi

CP=$(ls lib/*.jar | tr '\n' "$SEP")
javac -encoding UTF-8 --release 21 -nowarn -cp "$CP" -d out/classes $(find src -name '*.java')

cp -r resources/* out/classes/

# LWJGL finds its natives on the classpath, so unpacking the jars in is enough. every platform's go in,
# so the one jar runs on Windows, Linux and Mac
cd out/classes
for j in ../../lib/*.jar; do jar xf "$j"; done
rm -rf META-INF/*.SF META-INF/*.RSA META-INF/*.DSA META-INF/MANIFEST.MF module-info.class META-INF/versions/*/module-info.class
cd ../..

printf 'Main-Class: me.thesnowbound.shaderIDE.Main\nMulti-Release: true\n' > out/manifest.txt
jar cfm "out/dist/Shader IDE.jar" out/manifest.txt -C out/classes .
echo "built out/dist/Shader IDE.jar"

# the command line builder: only the packages it needs, so there's no LWJGL or editor in it
rm -rf out/builder && mkdir -p out/builder
for d in build library project util presets; do cp -r out/classes/me/thesnowbound/shaderIDE/$d out/builder/ 2>/dev/null; done
mkdir -p out/builder/me/thesnowbound/shaderIDE && cp -r out/classes/me out/builder/ 2>/dev/null
rm -rf out/builder/me/thesnowbound/shaderIDE/ui out/builder/me/thesnowbound/shaderIDE/editor out/builder/me/thesnowbound/shaderIDE/preview out/builder/me/thesnowbound/shaderIDE/learn out/builder/me/thesnowbound/shaderIDE/discord out/builder/me/thesnowbound/shaderIDE/*.class
cp -r out/classes/tiers out/classes/stubs out/builder/ 2>/dev/null
printf 'Main-Class: me.thesnowbound.shaderIDE.build.Builder\n' > out/builder-manifest.txt
jar cfm out/dist/shader-build.jar out/builder-manifest.txt -C out/builder .
echo "built out/dist/shader-build.jar"

# a self contained app with its own Java for this platform, packed up for the GitHub release
VERSION=${VERSION:-$(grep -o 'VERSION = "[^"]*"' src/me/thesnowbound/shaderIDE/build/Forge.java | cut -d'"' -f2)}
rm -rf out/jpkg out/app
mkdir -p out/jpkg
cp "out/dist/Shader IDE.jar" out/jpkg/
jpackage --type app-image --name "Shader IDE" --app-version "$VERSION" --vendor Thesnowbound \
    --input out/jpkg --main-jar "Shader IDE.jar" --main-class me.thesnowbound.shaderIDE.Main \
    ${ICON:+--icon "$ICON"} --dest out/app
APP=$(ls out/app)
NAME=Shader-IDE-$VERSION-$OS
# ditto and tar keep the launcher executable, a plain zip would lose that on Mac and Linux
case "$OS" in
    windows) (cd out/app && python -c "import shutil; shutil.make_archive('../dist/$NAME', 'zip', '.', '$APP')"); FILE=$NAME.zip ;;
    macos-*)
        mkdir -p out/dmg && cp -R "out/app/$APP" out/dmg/ && ln -s /Applications out/dmg/Applications
        hdiutil create -volname "Shader IDE" -srcfolder out/dmg -ov -format UDZO "out/dist/$NAME.dmg"
        FILE=$NAME.dmg ;;
    *) tar -czf "out/dist/$NAME.tar.gz" -C out/app "$APP"; FILE=$NAME.tar.gz ;;
esac
echo "built out/dist/$FILE"

# a Linux rpm too, when the rpm tools are installed (Fedora has them, the release workflow installs them)
if [ "$(uname -s)" = Linux ] && command -v rpmbuild >/dev/null 2>&1; then
    rm -rf out/rpm && mkdir -p out/rpm
    jpackage --type rpm --name "Shader IDE" --app-version "$VERSION" --vendor Thesnowbound \
        --input out/jpkg --main-jar "Shader IDE.jar" --main-class me.thesnowbound.shaderIDE.Main \
        --icon packaging/icon.png --linux-shortcut --linux-package-name shader-ide \
        --linux-app-category Development --description "An IDE for Minecraft core shaders" \
        --license-file LICENSE --dest out/rpm
    cp out/rpm/*.rpm out/dist/
    echo "built out/dist/$(cd out/dist && ls *.rpm)"
fi
