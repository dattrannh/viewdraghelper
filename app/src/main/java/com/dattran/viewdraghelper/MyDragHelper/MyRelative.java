package com.dattran.viewdraghelper.MyDragHelper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dattran.viewdraghelper.DragHelper.YoutubeLayout;
import com.dattran.viewdraghelper.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by DatTran on 05/01/2018.
 */

public class MyRelative extends RelativeLayout {
    private ViewDragHelper dragHelper;
    private View dragView;
    private View secondView;
    private boolean isMin, isBottom, isUndo, isClosing, hasClosed;
    private int dragRangeHeight, mTop, originalHeight, originalWidth = 0;
    private float perX, perY, scale = 1;
    private LayoutParams params;
    private final int distanceBottom = 10, disanceRight = 10;
    private final float speed = 460f;

    public MyRelative(Context context) {
        super(context);
    }

    public MyRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragView = findViewById(R.id.headerView);
        secondView = findViewById(R.id.contentView);
        dragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        params = (LayoutParams) dragView.getLayoutParams();

    }

    int marginLeft, height, width;

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return dragView == child;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            mTop = top;
            perY = (float) top / dragRangeHeight;
            isBottom = perY > 0.98f;
            if (isClosing) {
                dragView.layout(left, top, left + width, top + height);
            } else {
                scale = 1 - perY / 2;
                width = (int) (originalWidth * scale);
                height = (int) (originalHeight * scale);
                int disR = (int) (disanceRight * perY);
                marginLeft = (int) (originalWidth * perY / 2) - disR;
                params.width = width;
                params.height = height;
                dragView.setLayoutParams(params);
                dragView.layout(marginLeft, top, originalWidth - disR, top + height);
                secondView.setAlpha(1 - perY);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            super.onViewReleased(releasedChild, xvel, yvel);
            int top;
            if (isBottom) {
                if (perX > 0.5f || xvel > speed) {
                    closeToRight();
                } else if (perX < -0.4f || xvel < -speed) {
                    closeToLeft();
                }
            }
            if (!hasClosed) {
                if (yvel > 0 && (perY > 0.2f || yvel > speed)) {
                    top = dragRangeHeight;
                    minimize();
                } else {
                    if (perY < 0.6f || yvel < -speed) {
                        maximize();
                        top = 0;
                    } else {
                        top = dragRangeHeight;
                        if (!isUndo) {
                            minimize();
                        }
                    }
                }
//                dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
//                invalidate();
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top >= dragRangeHeight) {
                isMin = true;
                return dragRangeHeight;
            } else if (top < 0) {
                return 0;
            }
            if (isMin) {
                return dragRangeHeight;
            }
            return top;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            float x = left - (float) originalWidth / 2 + disanceRight;
            isUndo = Math.abs(x) < 4;
            if (isBottom) {
                isClosing = true;
                if (x > 0) {
                    perX = 2 * x / originalWidth;
                } else {
                    perX = x / originalWidth;
                }
//                float alpha = 1 - Math.min(Math.abs(perX), 1);
//                dragView.setAlpha(alpha);
//                print(left,originalWidth,perX,1-x);
                return left;
            }
            isClosing = false;
            return super.clampViewPositionHorizontal(child, left, dx);
        }
    }

    public boolean isViewAtBottom() {
        return isBottom;
    }
    public boolean isClosed() {
        return hasClosed;
    }

    public boolean isViewAtTop() {
        return dragView.getTop() == 0;
    }

    public boolean isViewAtRight() {
        return dragView.getRight() == originalWidth - disanceRight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        boolean isDragViewHit = isViewHit(dragView, x, y);
        boolean isSecondViewHit = isViewHit(secondView, x, y);
        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isUndo = true;
                hasClosed = false;
                break;
            case MotionEvent.ACTION_UP:
                isMin = false;
                if (isBottom) {
                    if (isUndo) {
                        maximize();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return isDragViewHit || isSecondViewHit;
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            return false;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * dragRangeHeight);
        int x = (int) (marginLeft * slideOffset);
        if (dragHelper.smoothSlideViewTo(dragView, x, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public void maximize() {
        perX = 0;
        isClosing = false;
        if (dragView.getAlpha() < 1) {
            dragView.setAlpha(1);
        }
        smoothSlideTo(0f);
        if (listener != null) {
            listener.onMaximized();
        }
    }

    public void minimize() {
        if (dragView.getAlpha() < 1) {
            dragView.setAlpha(1);
        }
        smoothSlideTo(1f);
        if (listener != null) {
            listener.onMinimized();
        }
    }


    public void closeToRight() {
        if (dragHelper.smoothSlideViewTo(dragView, dragView.getWidth() + getWidth(),
                dragRangeHeight)) {
            hasClosed = true;
            ViewCompat.postInvalidateOnAnimation(this);
            if (listener != null) {
                listener.onClosed();
            }
        } else {
            hasClosed = false;
        }

    }

    public void closeToLeft() {
        if (dragHelper.smoothSlideViewTo(dragView, -dragView.getWidth(),
                dragRangeHeight)) {
            hasClosed = true;
            ViewCompat.postInvalidateOnAnimation(this);
            if (listener != null) {
                listener.onClosed();
            }
        } else {
            hasClosed = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        print(left,top,right,bottom);
        if (originalWidth == 0) {
            originalWidth = dragView.getMeasuredWidth();
            originalHeight = dragView.getMeasuredHeight();
            dragRangeHeight = getHeight() - originalHeight / 2 - distanceBottom;
        }
        if (isViewAtTop()) {
//            int x = mTop + dragView.getMeasuredHeight();
            dragView.layout(0, 0, originalWidth, originalHeight);
            secondView.layout(0, originalHeight, originalWidth, bottom + originalHeight);
        } else {
            int dis = (int) (distanceBottom * perY);
            secondView.layout(0, mTop + height + dis, originalWidth, bottom + dis+(int)(perY*bottom));
        }
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//            originalWidth = dragView.getMeasuredWidth();
//            originalHeight = dragView.getMeasuredHeight();
        print(newConfig.orientation,"height="+getHeight(),"width="+getWidth());
            dragRangeHeight = getHeight() - originalHeight / 2 - distanceBottom;
//        if (isViewAtTop()) {
////            int x = mTop + dragView.getMeasuredHeight();
//            dragView.layout(0, 0, originalWidth, originalHeight);
//            secondView.layout(0, originalHeight, originalWidth, getHeight());
//        } else {
//            int dis = (int) (distanceBottom * perY);
//            secondView.layout(0, mTop + height + dis, originalWidth, getHeight()+ dis);
//        }

    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

//    public int getOriginalHeight() {
//        if (originalHeight == 0) {
//            originalHeight = dragView.getMeasuredHeight();
//        }
//        return originalHeight;
//    }
//
//    public int getOriginalWidth() {
//        if (originalWidth == 0) {
//            originalWidth = dragView.getMeasuredWidth();
//        }
//        return originalWidth;
//    }

    private DraggableListener listener;

    public void setDraggableListener(DraggableListener listener) {
        this.listener = listener;
    }

    public interface DraggableListener {

        /**
         * Called when the view is maximized.
         */
        void onMaximized();

        /**
         * Called when the view is minimized.
         */
        void onMinimized();

        /**
         * Called when the view is closed to the left,right.
         */
        void onClosed();
    }

    public static void print(Object... objs) {
        String s = objs[0] + "";
        for (int i = 1; i < objs.length; i++) {
            s += ";  " + objs[i];
        }
        Log.d("thunghiem", s);
    }
}
