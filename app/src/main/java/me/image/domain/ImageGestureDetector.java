package me.image.domain;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class ImageGestureDetector extends SimpleOnGestureListener {

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onDoubleTap(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        System.out.println("---on onDoubleTapEvent---");
        return super.onDoubleTapEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        super.onLongPress(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
        super.onShowPress(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onSingleTapUp(e);
    }


}
