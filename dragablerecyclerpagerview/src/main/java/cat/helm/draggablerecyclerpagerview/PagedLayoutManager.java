/*
Created by Helm  4/6/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.makeramen.dragsortadapter.DragSortAdapter;

public class PagedLayoutManager extends RecyclerView.LayoutManager {

    private static final int DIRECTION_END = 0;
    private static final int DIRECTION_START = 1;
    private static final int DIRECTION_NONE = -1;
    private final Context context;
    private final int rowCount;
    private final int columnCount;
    private final int itemsPerPage;
    private int decoratedChildWidth;
    private int decoratedChildHeight;
    private int visibleColumCount;
    private int visibleRowCount;
    private int firstVisiblePosition = 0;
    private OrientationHelper orientationHelper;
    private int dx;
    private int firstVisibleItem = 0;

    public PagedLayoutManager(@NonNull Context context, int rowCount, int columnCount) {
        this.context = context;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        if (rowCount <= 0 || columnCount <= 0) {
            throw new IllegalArgumentException("row and column must be positive greater than 0 values");
        }
        itemsPerPage = rowCount * columnCount;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }



    public static class LayoutParams extends RecyclerView.LayoutParams {

        //Current row in the grid
        public int row;
        //Current column in the grid
        public int column;
        public int insetRight;
        public int insetLef;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        View scrap = recycler.getViewForPosition(0);
       // addView(scrap);
        measureChildWithMargins(scrap, 0, 0);

        decoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        decoratedChildHeight = getDecoratedMeasuredHeight(scrap);
//        detachAndScrapView(scrap, recycler);
        firstVisiblePosition = firstVisibleItem;

        updateWindowSpace();
        detachAndScrapAttachedViews(recycler);
        fillGrid(DIRECTION_NONE, state, recycler);
    }

    //region UpdateWindowSpace related functions
    private void updateWindowSpace() {
        visibleColumCount = (getHorizontalSpace() / decoratedChildWidth);

        if (visibleColumCount > getTotalColumnCount()) {
            visibleColumCount = getTotalColumnCount();
        }

        visibleRowCount = (getVerticalSpace() / decoratedChildHeight);

        if (visibleRowCount > getTotalRowCount()) {
            visibleRowCount = getTotalRowCount();
        }
    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getTotalColumnCount() {
        int completePages = getItemCount() / itemsPerPage;
        int remainingItems = getItemCount() % itemsPerPage;
        int remainingColumns = remainingItems - remainingItems % columnCount;
        return completePages * columnCount + remainingColumns;
    }

    private int getTotalRowCount() {
        int completePages = getItemCount() / itemsPerPage;
        int remainingItems = getItemCount() % itemsPerPage;
        int remainingRows = remainingItems / columnCount;
        remainingRows += remainingRows % columnCount > 0
                ? 1
                : 0;
        return completePages * rowCount + remainingRows;
    }
    //endregion


    //region fillGrid related functions
    private void fillGrid(int direction, RecyclerView.State state, RecyclerView.Recycler recycler) {
        if (firstVisiblePosition < 0) firstVisiblePosition = 0;
        if (firstVisiblePosition >= getItemCount()) firstVisiblePosition = (getItemCount() - 1);
        SparseArray<View> viewCache = new SparseArray<View>(getChildCount());
        int startLeftOffset = getPaddingLeft();
        int startTopOffset = getPaddingTop();
        if (getChildCount() != 0) {
            final View topView = getChildAt(0);
            startLeftOffset = getDecoratedLeft(topView);
            startTopOffset = getDecoratedTop(topView);
            switch (direction) {
                case DIRECTION_START:
                    startLeftOffset -= decoratedChildWidth;
                    break;
                case DIRECTION_END:
                    startLeftOffset += decoratedChildWidth;
                    break;
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            int position = getPositionForIndex(i);
            final View child = getChildAt(i);
            viewCache.put(position, child);
        }
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }
        switch (direction) {
            case DIRECTION_START:
                firstVisiblePosition -= 2;
                break;
            case DIRECTION_END:
                firstVisiblePosition += 2;
                break;
        }



        int leftOffset = startLeftOffset;
        int topOffset = startTopOffset;
        int drawingChild = 12;
        final View topView = getChildAt(0);
        for (int i = 0; i < drawingChild; i++) {
            int nextPosition = getPositionForIndex(i);
            if(nextPosition >= getItemCount()) nextPosition = getItemCount() -1;
            View view = viewCache.get(nextPosition);
            if (view == null) {
                    view = recycler.getViewForPosition(nextPosition);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);
                    int startOffsetLeft = getStartOffsetLefForIndex(nextPosition, topView);
                    int startOffsetTop = getStartOffsetTopForIndex(nextPosition, view);
                    startOffsetLeft += startLeftOffset;
                                    layoutDecorated(view,
                                            startOffsetLeft,
                                            startOffsetTop,
                                            startOffsetLeft + decoratedChildWidth,
                                            startOffsetTop + decoratedChildHeight);

            } else {
                attachView(view);
                viewCache.remove(nextPosition);
            }

        }

        for (int i = 0; i < viewCache.size(); i++) {
            final View removingView = viewCache.valueAt(i);
            recycler.recycleView(removingView);
        }

    }


    private int getPositionForIndex(int i) {
        i += firstVisiblePosition;
        if (i % 2 == 0) {
            return i - ((i / 2) % 3);
        } else {
            return i + (((i % 3) + 1) % 3);
        }


        //  return firstVisiblePosition + i;
    }

    private int getStartOffsetLefForIndex(int i, View view) {
        int currentColumn = getColumnForPosition(i);
        currentColumn -= firstVisiblePosition / 2;
        View topView = getChildAt(0);
        int leftOffsetWithoutMargins = currentColumn * decoratedChildWidth;
        int leftMargin = 0;//(((RecyclerView.LayoutParams) view.getLayoutParams()).leftMargin);
        int rightMargin = 0;//(((RecyclerView.LayoutParams) view.getLayoutParams()).rightMargin);
        int x = leftOffsetWithoutMargins + leftMargin + rightMargin; // currentColumn * rightMargin + (currentColumn + 1) * leftMargin;
        return x;
    }

    private int getColumnForPosition(int i) {
        return (i % columnCount) + ((i / itemsPerPage) * columnCount);
    }

    private int getStartOffsetTopForIndex(int i, View view) {
        int currentRow = (i / columnCount) % rowCount;
        int topOffsetWithoutMargins = currentRow * decoratedChildHeight + getPaddingTop();
        int topMargin = ((RecyclerView.LayoutParams) view.getLayoutParams()).topMargin;
        int bottomMargin = ((RecyclerView.LayoutParams) view.getLayoutParams()).bottomMargin;
        return topOffsetWithoutMargins + currentRow * bottomMargin + (currentRow + 1) * topMargin;
    }
    //endregion


    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        Rect r = new Rect();
        calculateItemDecorationsForChild(child, r);
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        layoutParams.insetLef =  r.left;
        layoutParams.insetRight = r.right;
        int width = View.MeasureSpec.makeMeasureSpec(getHorizontalSpace() / columnCount - lp.rightMargin - lp.leftMargin - r.left - r.right
                , View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(getVerticalSpace() / rowCount - lp.topMargin - lp.bottomMargin - r.top - r.bottom
                , View.MeasureSpec.EXACTLY);

        child.measure(width, height);


    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        View topView = getChildAt(0);
        View bottomView = getChildAt(itemsPerPage-1);
        boolean leftBoundReached = firstVisiblePosition <= 0;
        boolean rightBoundReached = firstVisiblePosition/2 >= getTotalColumnCount() - columnCount ;
        int delta = -dx;
        if (dx > 0) { // Contents are scrolling left
            //Check right bound
            if (rightBoundReached) {
                //If we've reached the last column, enforce limits
                int rightOffset = getHorizontalSpace() - getDecoratedRight(bottomView) + getPaddingRight();
                delta = Math.max(-dx, rightOffset);
            }
        } else { // Contents are scrolling right
            //Check left bound
            if (leftBoundReached) {
                int leftOffset = -getDecoratedLeft(topView) + getPaddingLeft();
                delta = Math.min(-dx, leftOffset);
            }
        }

        offsetChildrenHorizontal(delta);

        if (dx > 0) {
            if (getDecoratedRight(topView) < -16) {
                fillGrid(DIRECTION_END, state, recycler);
            } else {
                fillGrid(DIRECTION_NONE, state, recycler);
            }
        } else {
            if (getDecoratedLeft(topView) > 5) {
                fillGrid(DIRECTION_START, state, recycler);
            } else {
                fillGrid(DIRECTION_NONE, state, recycler);
            }
        }
        return -delta;
    }


    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount()) {
        //    Log.e(TAG, "Cannot scroll to " + position + ", item count is " + getItemCount());
            return;
        }

        /*
         * LinearSmoothScroller's default behavior is to scroll the contents until
         * the child is fully visible. It will snap to the top-left or bottom-right
         * of the parent depending on whether the direction of travel was positive
         * or negative.
         */
        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
            /*
             * LinearSmoothScroller, at a minimum, just need to know the vector
             * (x/y distance) to travel in order to get from the current positioning
             * to the target.
             */
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                final int vector = getColumnForPosition(targetPosition)
                        < getColumnForPosition(firstVisiblePosition)? -1 : 1;
                return new PointF(vector, 0);
            }

            @Override
            protected void onStart() {
                super.onStart();
                ((DragSortAdapter) recyclerView.getAdapter()).setCanMove(false);

            }

        };
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
        if(scroller.computeScrollVectorForPosition(position).x == 1){
         firstVisibleItem = position - itemsPerPage +1;
        }else {
            firstVisibleItem = position;
        }

        if(firstVisibleItem < 0) {
            firstVisibleItem = 0;
            ((DragSortAdapter) recyclerView.getAdapter()).setCanMove(true);
        }
    }


}
