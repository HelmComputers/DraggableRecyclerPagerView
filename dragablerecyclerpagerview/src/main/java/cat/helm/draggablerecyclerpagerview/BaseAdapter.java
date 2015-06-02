/*
Created by Helm  2/6/15.
*/


package cat.helm.draggablerecyclerpagerview;

import android.support.v7.widget.RecyclerView;

public abstract class  BaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{


    public abstract void move(int fromPosition, int toPosition);
}
