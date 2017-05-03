package me.image.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

public class DrawCG extends View {
    private Paint paint;
    private Canvas canv;
    private Bitmap mBitmap;

    public DrawCG(Context context) {
        super(context);
        // 畫筆
        paint = new Paint();
        // 顏色 
        paint.setColor(Color.RED);
        // 反鋸齒
        paint.setAntiAlias(true);
        // 線寬  
        paint.setStrokeWidth(3);

        // 畫布
        //canv = new Canvas(mBitmap);  
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // super.onDraw(canvas);  
    }

    /**
     * 畫線
     */
    public Bitmap drawLine() {

        canv.drawLine(200, 50, 600, 50, paint);
        return mBitmap;
    }

    /**
     * 畫圓
     */
    public Bitmap drawCircle(float x, float y, int color) {
        // 填充
        paint.setStyle(Style.FILL);
        paint.setColor(color);
        canv.drawCircle(x, y, 10.0f, paint);

        return mBitmap;
    }

    /**
     * 還原畫圓
     */
    public void restoreCircle(float x, float y, int count, int color) {
        System.out.println(" =======restoreCircle [" + count + "]");


    }

    /**
     * 畫三角形
     */
    public Bitmap drawTriangle() {
        Path path = new Path();
        path.moveTo(300, 600);
        path.lineTo(600, 200);
        path.lineTo(900, 600);
        path.lineTo(300, 600);
        canv.drawPath(path, paint);
        return mBitmap;
    }

    /**
     * 畫矩型
     */
    public Bitmap drawRect(int left, int top, int right, int bottom, int color, int strokeWidth) {
        // 非填充
        paint.setStyle(Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        canv.drawRect(new Rect(left, top, right, bottom), paint);
        return mBitmap;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        canv = new Canvas(mBitmap);
    }

}  