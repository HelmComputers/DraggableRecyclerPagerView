/*
Created by Helm  21/5/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class DraggableRecyclerPagerView extends RecyclerView implements GestureDetector.OnGestureListener {

    public static final int DEFAULT_VELOCITY = 500;
    private GestureDetector gestureScanner;
    float lastScrolledX;
    private int lastVisibleItemPosition;
    private int itemsCount = -1;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private boolean trobat;

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
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            private int adapterPosition;

            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                adapterPosition = viewHolder.getAdapterPosition();
                final int fromPos = adapterPosition;
                final int toPos = target.getAdapterPosition();
                ((BaseAdapter) getAdapter()).move(fromPos, toPos);
                getAdapter().notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {

            }

            @Override
            public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
                if (!trobat) {
                    trobat = true;
                    scrollNextPage();
                    ((BaseAdapter) getAdapter()).move(adapterPosition, adapterPosition+5);
                    getAdapter().notifyItemChanged(adapterPosition+5);
                }
                return 0;
            }

        });
        itemTouchHelper.attachToRecyclerView(this);
        if (!isInEditMode()) {
            gestureScanner = new GestureDetector(getContext(), this);
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean specialEventUsed = gestureScanner.onTouchEvent(event);
                setUpItemsCount();
                computeLastScrollX(event);
                if (!specialEventUsed && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    float lastScrolledXAbs = Math.abs(lastScrolledX);
                    if (lastScrolledXAbs < getMeasuredWidth() / 2) {
                        reverseScroll();
                    } else {
                        scrollToPage();
                    }
                    return true;
                } else {
           /*         if(trobat) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                trobat = false;
                            }
                        },3000);
                        return true;

                    }*/
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
            itemsCount = ((ScrollingGridLayoutManager) getLayoutManager()).getColumnCount();
            itemsCount *= ((ScrollingGridLayoutManager) getLayoutManager()).getRowCount();
            lastVisibleItemPosition = itemsCount - 1;
        }
    }

    private void reverseScroll() {
        if (getScrollDirection() == LEFT) {
            smoothScrollToPosition(lastVisibleItemPosition);
        } else {
            smoothScrollToPosition(lastVisibleItemPosition);
            Rect r = new Rect();
            View view = getChildAt(itemsCount + 1);
            if (view != null) {
                view.getLocalVisibleRect(r);
                int leftMargin = ((MarginLayoutParams) view.getLayoutParams()).leftMargin;
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

    private void scrollPreviousPage() {
        int scrollTo = lastVisibleItemPosition - (itemsCount * 2 - 1) <= 0 ? 0 : lastVisibleItemPosition - (itemsCount * 2 - 1);
        smoothScrollToPosition(scrollTo);
        lastVisibleItemPosition = scrollTo + (itemsCount - 1);
    }

    private void scrollNextPage() {
        final int scrollTo = lastVisibleItemPosition + itemsCount;
        scrollToPosition(scrollTo);
        lastVisibleItemPosition = scrollTo;
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
        if ((e1.getX() - e2.getX()) > 0 && velocityX < -DEFAULT_VELOCITY) {
            scrollNextPage();
            return true;


        } else if (e1.getX() - e2.getX() < 0 && velocityX > DEFAULT_VELOCITY) {
            scrollPreviousPage();
            return true;

        }
        return false;
    }

    public int getScrollDirection() {
        if (lastScrolledX <= 0) {
            return LEFT;
        } else {
            return RIGHT;
        }

    }


}
