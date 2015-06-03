/*
Created by Helm  22/5/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

public class ScrollingGridLayoutManager extends GridLayoutManager implements ItemTouchHelper.ViewDropHandler{
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

}
