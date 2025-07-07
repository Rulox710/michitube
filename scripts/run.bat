@ECHO off
@chcp 65001>nul

SET "EXEC=app.Main"
SET "RES_DIR=../src/main/resources"
SET "SRC_DIR=../src/main/java"
SET "JAVA_PRIN=../src/main/java/app/Main.java"

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
javac -d ../bin -sourcepath %SRC_DIR% --module-path "../lib/javafx-sdk-17.0.15/lib" --add-modules javafx.graphics,javafx.controls,javafx.fxml -cp ../lib/*;../lib/javafx-sdk-17.0.15/lib/* %JAVA_PRIN%

ECHO === Copiando recursos ===
XCOPY "%RES_DIR%\*" "../bin\" /E /I /Y

REM Ejecutar el programa compilado
:EXE
ECHO === Ejecutando el programa ===
java -cp ../bin;../lib/*;../lib/javafx-sdk-17.0.15/lib/* --module-path "../lib/javafx-sdk-17.0.15/lib" --add-modules javafx.graphics,javafx.controls,javafx.fxml %EXEC%
PAUSE

EXIT
