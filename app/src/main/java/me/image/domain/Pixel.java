package me.image.domain;

/**
 * 每一個像素的點(x,y)
 */
public class Pixel {

    private int x = 0;

    private int y = 0;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * 比較是現有的像素與新輸入(x,y)何者為Top像素，
     * (x,0)為Top
     *
     * @return true(現有像素為top)
     */
    public boolean compareTop(int x, int y) {
        if (y < this.y) {

            return false;
        } else {

            return true;

        }
    }

    /**
     * 比較是現有的像素與新輸入(x,y)何者為Left像素，
     * (0,y)為left
     *
     * @return true(現有像素為left)
     */
    public boolean compareLeft(int x, int y) {
        if (x < this.x) {

            return false;
        } else {

            return true;

        }
    }

}
