/*
Created by Helm  21/5/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class DraggableRecyclerPagerView extends RecyclerView implements GestureDetector.OnGestureListener {

    private GestureDetector gestureScanner;
    float lastScrolledX;
    private int lastVisibleItemPosition;
    private int itemsCount = -1;

    public DraggableRecyclerPagerView(Context context) {
        super(context);
        init();
    }

    public DraggableRecyclerPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableRecyclerPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void init() {
        if (!isInEditMode()) {
            gestureScanner = new GestureDetector(getContext(), this);
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(itemsCount == -1){
                    itemsCount = ((ScrollingGridLayoutManager) getLayoutManager()).getColumnCount();
                    itemsCount *= ((ScrollingGridLayoutManager) getLayoutManager()).getRowCount();
                    lastVisibleItemPosition = itemsCount - 1;
                }
                boolean specialEventUsed = gestureScanner.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastScrolledX = event.getX();
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    lastScrolledX -= event.getX();
                }
                if (!specialEventUsed && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    scrollToPage();
                    return true;
                }else {
                    return specialEventUsed;
                }
            }
        });
    }


    private void scrollToPage() {
        if(lastScrolledX > 0) {
            int scrollTo = lastVisibleItemPosition + itemsCount;
            smoothScrollToPosition(scrollTo);
            lastVisibleItemPosition = scrollTo;
        }else {
            int scrollTo = lastVisibleItemPosition - (itemsCount * 2 - 1) <= 0 ? 0 :lastVisibleItemPosition-(itemsCount * 2 - 1);
            smoothScrollToPosition(scrollTo);
            lastVisibleItemPosition = scrollTo + (itemsCount - 1);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}
