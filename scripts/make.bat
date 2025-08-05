@ECHO off
@chcp 65001>nul

SET "EXEC=app.AppLauncher"
SET "RES_DIR=../src/main/resources"
SET "JAR_DIR=../build"
SET "JAR=MichiTuber.jar"
SET "ICO=../build/icon.ico"
SET "NAME=MichiTuber"

IF NOT EXIST ../build (
    MKDIR ../build
)

jar cfm %JAR% ../META-INF/MANIFEST.MF -C ../bin .
MOVE %JAR% %JAR_DIR%

jlink ^
  --module-path "%JAVA_HOME%\jmods;..\lib\javafx-17.0.15" ^
  --add-modules java.base,java.logging,javafx.controls,javafx.fxml,javafx.graphics ^
  --output ../build/runtime ^
  --strip-debug ^
  --compress=2 ^
  --no-header-files ^
  --no-man-pages
XCOPY "..\lib\bin\*.*" "..\build\runtime\bin\" /D /Y /R

jpackage ^
  --type app-image ^
  --name %NAME% ^
  --input %JAR_DIR% ^
  --main-jar %JAR% ^
  --main-class app.AppLauncher ^
  --java-options "--enable-preview" ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --runtime-image ../build/runtime ^
  --icon %ICO% ^
  --app-version 1.0.1

MOVE %NAME% %JAR_DIR%

pause

EXIT
