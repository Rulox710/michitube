@ECHO off
@chcp 65001>nul

SET "EXEC=app.Main"
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

jpackage --name %NAME% --app-version 1.0.0.0 --vendor "UserRulox" ^
  --input %JAR_DIR% ^
  --main-jar %JAR% --main-class app.Main ^
  --type app-image --icon %ICO% --win-console
MOVE %NAME% %JAR_DIR%

EXIT
