package app.engine;

/**
 * Tiene lo necesario para medir que tantas actualizaciones se hacen en
 * el equipo sin importar su calidad.
 */
public class DeltaTimeManager extends Observable {

    private boolean running;
    private int updatesPerSecond, frameCount;
    private float deltaTime;
    private long lastSecondTime;

    private final static int NS_PER_SECOND = 1000000000;
    private int maxUPS = 60;
    private static DeltaTimeManager deltaTimeManager = new DeltaTimeManager();

    /**
     * Constructor privado para poder usar el patrón singleton.
     */
    private DeltaTimeManager() {
        start();
    }

    public void changeMaxUPS(int ups) {
        maxUPS = ups;
    }

    /**
     * Obtiene la única instancia del administrador de tiempo delta.
     *
     * @return La instancia del tiempo delta.
     */
    public static DeltaTimeManager getInstance() {
        return deltaTimeManager;
    }

    /**
     * Empieza un hilo que va a informar cada que haya una
     * actualización.
     */
    public void start() {
        if(!running) {
            running = true;
            Thread gameThread = new Thread(() -> {
                long previousTime = System.nanoTime();
                lastSecondTime = System.nanoTime();
                while(running) {
                    deltaTime = (System.nanoTime() - previousTime)
                            / (float) NS_PER_SECOND;
                    previousTime = System.nanoTime();
                    frameCount++;
                    long currentTime = System.nanoTime();
                    if(currentTime - lastSecondTime >= NS_PER_SECOND) {
                        updatesPerSecond = frameCount;
                        frameCount = 0;
                        lastSecondTime = currentTime;
                    }
                    long targetTime = NS_PER_SECOND / maxUPS;
                    long sleepTime = targetTime
                            - (System.nanoTime() - currentTime);
                    if(sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime / 1000000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    notifyObservers('u', null);
                }
            });
            gameThread.start();
        }
    }

    /**
     * Detiene el hilo.
     */
    public void stop() {
        running = false;
    }

    /**
     * Obtiene el tiempo delta. Cuánto tiempo ha pasado desde la
     * última actualización en segundos. Esta debe ser usada para medir
     * tiempos, y movimientos para que distintos computadores no tengan
     * distintos rendimientos.
     *
     * @return Una cantidad de tiempo.
     */
    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Obtiene la cantidad de actualizaciones por segundo que hay.
     *
     * @return Una cantidad que representa cuántas actualizaciones hay.
     */
    public int getUpdatesPerSecond() {
        return updatesPerSecond;
    }
}
