/*
Created by Helm  4/6/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PagedLayoutManager extends RecyclerView.LayoutManager {

    private final Context context;
    private final int rowCount;
    private final int columnCount;
    private final int itemsPerPage;
    private int decoratedChildWidth;
    private int decoratedChildHeight;
    private int visibleColumCount;
    private int visibleRowCount;
    private int firstVisiblePosition;

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
    /**
     * {@inheritDoc}
     **/
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                100,
                100);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);

        decoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        decoratedChildHeight = getDecoratedMeasuredHeight(scrap);
        detachAndScrapView(scrap, recycler);

        updateWindowSpace();

        firstVisiblePosition = 0;

        int leftOffset, topOffset = 0;


        fillGrid(recycler);
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


    private void fillGrid(RecyclerView.Recycler recycler) {
        for (int i = 0; i < itemsPerPage; i++) {

            int nextPosition = getPositionForIndex(i);

            View view = recycler.getViewForPosition(nextPosition);
            addView(view);

            measureChildWithMargins(view, 0, 0);
            int startOffsetLeft = getStartOffsetLefForIndex(i, view);
            int startOffsetTop = getStartOffsetTopForIndex(i, view);
            layoutDecorated(view,
                    startOffsetLeft,
                    startOffsetTop,
                    startOffsetLeft + decoratedChildWidth,
                    startOffsetTop + decoratedChildHeight);
        }
    }


    private int getPositionForIndex(int i) {
        return firstVisiblePosition + i;
    }

    private int getStartOffsetLefForIndex(int i, View view) {
        int currentColumn = i % columnCount;
        int leftOffsetWithoutMargins = currentColumn * decoratedChildWidth + getPaddingLeft();
        int leftMargin = (((RecyclerView.LayoutParams) view.getLayoutParams()).leftMargin);
        int rightMargin = (((RecyclerView.LayoutParams) view.getLayoutParams()).rightMargin);
        return leftOffsetWithoutMargins + currentColumn * rightMargin + (currentColumn + 1) * leftMargin;
    }

    private int getStartOffsetTopForIndex(int i, View view) {
        int currentRow = i / columnCount;
        int topOffsetWithoutMargins = currentRow * decoratedChildHeight + getPaddingTop();
        int topMargin = ((RecyclerView.LayoutParams) view.getLayoutParams()).topMargin;
        int bottomMargin = ((RecyclerView.LayoutParams) view.getLayoutParams()).bottomMargin;
        return topOffsetWithoutMargins + currentRow * bottomMargin + (currentRow +1 ) * topMargin;
    }


    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();

        int width = View.MeasureSpec.makeMeasureSpec(getHorizontalSpace() / columnCount - lp.rightMargin - lp.leftMargin,
                View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(getVerticalSpace() / rowCount - lp.topMargin - lp.bottomMargin
                , View.MeasureSpec.EXACTLY);
        child.measure(width, height);

    }
}
