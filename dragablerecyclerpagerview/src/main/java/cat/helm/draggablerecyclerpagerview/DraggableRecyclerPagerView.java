/*
Created by Helm  21/5/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.makeramen.dragsortadapter.DragSortAdapter;

public class DraggableRecyclerPagerView extends RecyclerView implements GestureDetector.OnGestureListener {

    public static final int DEFAULT_VELOCITY = 300;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    float lastScrolledX;
    private GestureDetector gestureScanner;
    private int lastVisibleItemPosition;
    private int itemsCount = -1;
    private Adapter adapter;

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


    private void init() {
        if (!isInEditMode()) {
            gestureScanner = new GestureDetector(getContext(), this);
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean specialEventUsed = gestureScanner.onTouchEvent(event);
                setUpItemsCount();
                computeLastScrollX(event);
                if (lastScrolledX == 0.0) return false;
                if (!specialEventUsed && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    float lastScrolledXAbs = Math.abs(lastScrolledX);
                    if (lastScrolledXAbs <= getMeasuredWidth() / 3) {
                    } else {
                        scrollToPage();
                    }
                    return true;
                } else {
                    return specialEventUsed;
                }
            }
        });
    }


    private void computeLastScrollX(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastScrolledX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            lastScrolledX -= event.getX();
        }
    }

    private void setUpItemsCount() {
        if (itemsCount == -1) {
          /*  itemsCount = ((ScrollingGridLayoutManager) getLayoutManager()).getColumnCount();
            itemsCount *= ((ScrollingGridLayoutManager) getLayoutManager()).getRowCount();
            lastVisibleItemPosition = itemsCount - 1;
        }*/
            lastVisibleItemPosition = 6 - 1;
            itemsCount = 6;
        }
    }

    private void reverseScroll() {
        if (getScrollDirection() == LEFT) {
            smoothScrollToPosition(lastVisibleItemPosition);
        } else if(lastVisibleItemPosition != adapter.getItemCount() -1){
            smoothScrollToPosition(lastVisibleItemPosition);
            Rect r = new Rect();
            View view = getChildAt(itemsCount + 1);
            if (view != null) {
                view.getLocalVisibleRect(r);
                int leftMargin = ((PagedLayoutManager.LayoutParams) view.getLayoutParams()).leftMargin;
                leftMargin += ((PagedLayoutManager.LayoutParams) view.getLayoutParams()).insetLef;
                smoothScrollBy(-r.width() - leftMargin, 0);
            }
        }
    }

    private void scrollToPage() {
        if (getScrollDirection() == RIGHT) {
            scrollNextPage();
        } else {
            scrollPreviousPage();
        }
    }

    public void scrollPreviousPage() {
        int scrollTo = lastVisibleItemPosition - (itemsCount * 2 - 1) <= 0 ? 0 : lastVisibleItemPosition - (itemsCount * 2 - 1);
        smoothScrollToPosition(scrollTo);
        lastVisibleItemPosition = scrollTo + (itemsCount - 1);
    }

    public void scrollNextPage() {
        int scrollTo = lastVisibleItemPosition + itemsCount;
        if(scrollTo > adapter.getItemCount()){
            scrollTo = adapter.getItemCount() - 1;
        }
        else {
            smoothScrollToPosition(scrollTo);
        }
        lastVisibleItemPosition = scrollTo;

    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(state == SCROLL_STATE_IDLE ) {
            ((DragSortAdapter) getAdapter()).setCanMove(true);
        }
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
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 != null && e2 != null) {
            float x1, x2;
            x1 = e1.getX();
            x2 = e2.getX();
            if ((x1 - x2) > 0 && velocityX < -DEFAULT_VELOCITY) {
                scrollNextPage();
                return true;

            } else if (x1 - x2 < 0 && velocityX > DEFAULT_VELOCITY) {
                scrollPreviousPage();
                return true;

            }
        }
        return false;
    }

    public int getScrollDirection() {
        if (lastScrolledX < 0) {
            return LEFT;
        } else {
            return RIGHT;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            lastScrolledX = e.getX();
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        setUpItemsCount();
    }


}
