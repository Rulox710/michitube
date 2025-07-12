package app.maker.controllers.objects;

public final class Objects {

    public static class Delta {
        public double x, y;
    }

    public static class DirectionLock {
        public boolean horizontal = false;
        public boolean vertical = false;
        public boolean locked = false;

        public void reset() {
            horizontal = false;
            vertical = false;
            locked = false;
        }
    }

    public static class ImageInfo {
        public int x, y, width, height;
        public String path;
    }

    public static class SheetInfo {
        public int width, height;
        public String color;
    }
}
