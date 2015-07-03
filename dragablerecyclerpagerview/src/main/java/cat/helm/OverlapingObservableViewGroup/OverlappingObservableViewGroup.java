/*
Created by Helm  29/6/15.
*/


package cat.helm.OverlapingObservableViewGroup;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class OverlappingObservableViewGroup extends RelativeLayout {


    List<Rect> viewRectList;
    List<OnOverlapListener> viewListenerList;
    private View lastDragged;


    public interface OnOverlapListener {
        public void onOverlap(View lastDragged);
    }


    public OverlappingObservableViewGroup(Context context) {
        super(context);
        init();
    }

    public OverlappingObservableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlappingObservableViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewRectList = new ArrayList<>();
        viewListenerList = new ArrayList<>();
    }


    public void onOverlapDraggingView(View overlappedView, OnOverlapListener listener) {
        Rect hitBox = new Rect();
        overlappedView.getDrawingRect(hitBox);
        viewRectList.add(hitBox);
        viewListenerList.add(listener);
    }

    public void onDraggingViewMoved(View lastDragged, long itemId) {
        this.lastDragged = lastDragged;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        for (int i = 0; i < viewRectList.size(); i++) {
            if (viewRectList.get(i).contains((int) event.getX(), (int) event.getY())) {
                viewListenerList.get(i).onOverlap(lastDragged);
            }
        }
        return super.onDragEvent(event);
    }


}
