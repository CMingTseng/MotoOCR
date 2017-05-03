package me.image.domain;

/**
 * 儲存view vs bitmap之x,y對應
 */
public class ViewPixelBean {

    private float viewX = -1;

    private float viewY = -1;

    private float bitmapX = -1;

    private float bitmapY = -1;

    public float getViewX() {
        return viewX;
    }

    public void setViewX(float viewX) {
        this.viewX = viewX;
    }

    public float getViewY() {
        return viewY;
    }

    public void setViewY(float viewY) {
        this.viewY = viewY;
    }

    public float getBitmapX() {
        return bitmapX;
    }

    public void setBitmapX(float bitmapX) {
        this.bitmapX = bitmapX;
    }

    public float getBitmapY() {
        return bitmapY;
    }

    public void setBitmapY(float bitmapY) {
        this.bitmapY = bitmapY;
    }

    @Override
    public boolean equals(Object o) {
        ViewPixelBean v2 = (ViewPixelBean) o;
        if (bitmapX == v2.getBitmapX() && bitmapY == v2.getBitmapY()) {

            return true;
        } else {

            return false;
        }

    }


}
