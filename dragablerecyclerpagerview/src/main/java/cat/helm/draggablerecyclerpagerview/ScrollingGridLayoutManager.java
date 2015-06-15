/*
Created by Helm  22/5/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ScrollingGridLayoutManager extends GridLayoutManager {
    private final int rows;
    private int columns;


    public ScrollingGridLayoutManager(Context context, int rows, int columns, boolean reverseLayout) {
        super(context, rows, LinearLayoutManager.HORIZONTAL, reverseLayout);
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public void addView(View child, int index) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        layoutParams.width = getWidth() / columns;
        child.setLayoutParams(layoutParams);
        super.addView(child, index);
    }

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            final int viewPosition = lp.getViewLayoutPosition();
        }



    }



}
