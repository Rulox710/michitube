#!/bin/bash

function jumpto {
  label=$1
  cmd=$(sed -n "/$label:/{:a;n;p;ba};" $0 | grep -v ':$')
  eval "$cmd"
  exit
}

function pause {
  read -s -n 1 -p "Presione alguna tecla para continuar . . ."
  echo ""
}

EXEC="app.Main"
RES_DIR="../src/main/resources"
SRC_DIR="../src/main/java"
JAVA_PRIN="../src/main/java/app/Main.java"


# Pregunta al usuario si desea compilar
while true; do
  read -p "¿Compilar? (S/N)? " yn
  if [[ "$yn" =~ [sS] ]]; then jumpto exe; fi
  if [[ "$yn" =~ [nN] ]]; then jumpto cont; fi
  echo echo "Opción inválida. Por favor, seleccione S o N."
done

# Compilar si se ha seleccionado
comp:
if [[ ! -d ./bin ]]; then mkdir bin; fi
echo "=== Compilando código fuente ==="
javac -d ../bin -sourcepath "$SRC_DIR" -cp ../lib/* "$JAVA_PRIN"

echo "=== Copiando recursos ==="
cp -r "$RES_DIR"/* ../bin/

# Ejecutar el programa compilado
exe:
echo "=== Ejecutando el programa ==="
java -cp ../bin:../lib/* "$EXEC"
pause

exit
