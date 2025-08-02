@ECHO off
@chcp 65001>nul

SET "EXEC=app.AppLauncher"
SET "RES_DIR=../src/main/resources"
SET "SRC_DIR=../src/main/java"
SET "JAVA_PRIN=../src/main/java/app/AppLauncher.java"
SET "FLAG=--verbose"
@REM --verbose --log --debug

REM Pregunta al usuario si desea compilar
choice /c SN /n /m "¿Compilar (S/N)?"
IF ERRORLEVEL == 2 GOTO EXE
IF ERRORLEVEL == 1 GOTO COM

REM Compilar si se ha seleccionado
:COM
IF NOT EXIST ../bin (
    MKDIR ../bin
)
ECHO === Compilando código fuente ===
javac -d ../bin -sourcepath %SRC_DIR% --module-path "../lib/javafx-17.0.15" --add-modules javafx.graphics,javafx.controls,javafx.fxml -cp ../lib/*;../lib/javafx-17.0.15/* %JAVA_PRIN%

ECHO === Copiando recursos ===
XCOPY "%RES_DIR%\*" "../bin\" /E /I /Y

REM Ejecutar el programa compilado
:EXE
ECHO === Ejecutando el programa ===
java -cp ../bin;../lib/*;../lib/javafx-17.0.15/* --module-path "../lib/javafx-17.0.15" --add-modules javafx.graphics,javafx.controls,javafx.fxml %EXEC% %FLAG%
PAUSE

EXIT
