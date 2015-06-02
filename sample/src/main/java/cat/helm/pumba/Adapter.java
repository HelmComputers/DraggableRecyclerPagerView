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

import dragsortadapter.DragSortAdapter;
import dragsortadapter.NoForegroundShadowBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Adapter extends DragSortAdapter<Adapter.ViewHolder> {


    private final List<Integer> items;

    public Adapter(RecyclerView recyclerView, List<Integer> items) {
        super(recyclerView);
        this.items = items;
    }

    @Override public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder holder = new ViewHolder(this, view);
        view.setOnClickListener(holder);
        view.setOnLongClickListener(holder);

        //TextView textView = (TextView) v.findViewById(R.id.text_view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemId = items.get(position);

        holder.text.setText(items.get(position).toString());

        holder.text.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.text.postInvalidate();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getPositionForId(long l) {
        return items.indexOf((int) l);
    }

    @Override
    public boolean move(int fromPosition, int toPosition) {
        items.add(toPosition, items.remove(fromPosition));
        return true;
    }

    public static class ViewHolder extends  DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

//        @InjectView(R.id.container) ViewGroup container;
        @InjectView(R.id.text_view) TextView text;

        public ViewHolder(DragSortAdapter adapter, View itemView) {
            super(adapter, itemView);
            ButterKnife.inject(this, itemView);

        }

        @Override
        public void onClick(View view) {
            Log.d("TAG", text.getText() + " clicked!");

        }

        @Override
        public boolean onLongClick(View view) {
            startDrag();
            return true;
        }

        @Override public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
            return new NoForegroundShadowBuilder(itemView, touchPoint);
        }
//
//        public ViewHolder(TextView v) {
//            super(v);
//            mTextView = v;
//        }
    }


}
