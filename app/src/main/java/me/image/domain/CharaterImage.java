package me.image.domain;

/**
 * 於使用連通區塊標記後各字型區域
 */
public class CharaterImage {

    private Pixel top = new Pixel();

    private Pixel bottom = new Pixel();

    private Pixel left = new Pixel();

    private Pixel right = new Pixel();

    private int width = 0;

    private int height = 0;

    private int[][] images = null;


    public Pixel getTop() {
        return top;
    }

    public void setTop(Pixel top) {
        this.top = top;
    }

    public Pixel getBottom() {
        return bottom;
    }

    public void setBottom(Pixel bottom) {
        this.bottom = bottom;
    }

    public Pixel getLeft() {
        return left;
    }

    public void setLeft(Pixel left) {
        this.left = left;
    }

    public Pixel getRight() {
        return right;
    }

    public void setRight(Pixel right) {
        this.right = right;
    }

    public int getWidth() {
        return (right.getX() - left.getX());
    }


    public int getHeight() {
        return (bottom.getY() - top.getY());
    }


    public int[][] getImages() {
        return images;
    }

    public void setImages(int[][] images) {
        this.images = images;
    }


}
