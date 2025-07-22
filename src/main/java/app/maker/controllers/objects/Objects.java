package app.maker.controllers.objects;

/**
 * Clase que contiene clases auxiliares para manejar doatos primitivos.
 */
public final class Objects {

    /**
     * Clase que puede llevar dos números enteros, util para
     * representar coordenadas o dimensiones.
     */
    public static class Delta {
        public double x, y;
    }

    /**
     * Clase que indica si un objeto se puede mover en una dirección
     * horizontal o verticalmente, y si está bloqueado.
     */
    public static class DirectionLock {
        public boolean horizontal = false;
        public boolean vertical = false;
        public boolean locked = false;

        /**
         * Vuelve los valores de dirección a su estado inicial y
         * desbloquea el objeto.
         */
        public void reset() {
            horizontal = false;
            vertical = false;
            locked = false;
        }
    }
}
