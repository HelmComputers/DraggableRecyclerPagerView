package com.makeramen.dragsortadapter;

import android.graphics.Point;
import android.graphics.PointF;

final class DragInfo {
    private final long itemId;
    private final Point shadowSize;
    private final Point shadowTouchPoint;
    private final PointF dragPoint;
    private long lastScroll;
    public static final long MILLIS_TO_SCROLL = 400;

    public DragInfo(long itemId, Point shadowSize, Point shadowTouchPoint, PointF dragPoint) {
        this.itemId = itemId;
        this.shadowSize = new Point(shadowSize);
        this.shadowTouchPoint = new Point(shadowTouchPoint);
        this.dragPoint = dragPoint;
        this.lastScroll = 0;
    }

    long itemId() {
        return itemId;
    }

    boolean shouldScrollLeft() {
        boolean scrollLeft = dragPoint.x < shadowTouchPoint.x;
        if (scrollLeft) {
            if ((System.currentTimeMillis() - lastScroll) > MILLIS_TO_SCROLL) {
                lastScroll = System.currentTimeMillis();
            } else {
                scrollLeft = false;
            }
        }
        return scrollLeft;
    }

    boolean shouldScrollRight(int parentWidth) {
        boolean scrollRight = dragPoint.x > (parentWidth - (shadowSize.x - shadowTouchPoint.x));
        if (scrollRight) {
            if ((System.currentTimeMillis() - lastScroll) > MILLIS_TO_SCROLL) {
                lastScroll = System.currentTimeMillis();
            } else {
                scrollRight = false;
            }
        }
        return scrollRight;
    }

    boolean isEligibleForScroll(int parentWidth){
        boolean scrollLeft = dragPoint.x < shadowTouchPoint.x;
        boolean scrollRight = dragPoint.x > (parentWidth - (shadowSize.x - shadowTouchPoint.x));
        return  scrollLeft || scrollRight;
    }

    boolean shouldScrollUp() {
        return dragPoint.y < shadowTouchPoint.y;
    }

    boolean shouldScrollDown(int parentHeight) {
        return dragPoint.y > (parentHeight - (shadowSize.y - shadowTouchPoint.y));
    }

    void setDragPoint(float x, float y) {
        dragPoint.set(x, y);
    }
}
