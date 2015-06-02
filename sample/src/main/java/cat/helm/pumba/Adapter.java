/*
Created by Helm  21/5/15.
*/


package cat.helm.pumba;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.NoForegroundShadowBuilder;

import java.util.List;

public class Adapter extends DragSortAdapter<Adapter.ViewHolder> {


    private final List<String> items;

    public Adapter(RecyclerView recyclerview, List<String> items) {
        super(recyclerview);
        this.items = items;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        TextView textView = (TextView) v.findViewById(R.id.text_view);
        ViewHolder viewHolder = new ViewHolder(this, textView);
        textView.setOnClickListener(viewHolder);
        textView.setOnLongClickListener(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(items.get(position));
        holder.mTextView.setVisibility(getDraggingId() == position ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        public TextView mTextView;
        public ViewHolder(DragSortAdapter dragSortAdapter, TextView v) {
            super(dragSortAdapter, v);
            mTextView = v;
            ButterKnife.inject(this, itemView);

        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            Log.e("ViewHolder", "onLongClick" + "D:");
            startDrag();
            return true;
        }


        @Override public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
            return new NoForegroundShadowBuilder(itemView, touchPoint);
        }
    }


    @Override
    public int getPositionForId(long id) {
        Log.e("Adapter", "getPositionForId" + id);
        return (int) id;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean move(int fromPosition, int toPosition){
        items.add(toPosition, items.remove(fromPosition));
        return true;
    }


}
