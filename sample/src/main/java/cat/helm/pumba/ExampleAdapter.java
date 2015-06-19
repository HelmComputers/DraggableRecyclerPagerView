/*
 * Copyright (C) 2015 Vincent Mi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cat.helm.pumba;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.NoForegroundShadowBuilder;

import java.util.List;
import java.util.Vector;

public class ExampleAdapter extends DragSortAdapter<ExampleAdapter.MainViewHolder> {

    public static final String TAG = ExampleAdapter.class.getSimpleName();
    private final Vector<Integer> integers;
    private onItemMovedListener listener;
    private final List<Integer> data;

    public ExampleAdapter(RecyclerView recyclerView, List<Integer> data) {
        super(recyclerView);
        this.data = data;
        int size = data.size() +(6 - data.size()%6);
        integers = new Vector<>(size);
        for (int i = 0; i < data.size(); i++) {
            integers.add(i,i);
        }
    }

    public interface onItemMovedListener {

        void onItemMoved(int fomPosition, int toPosition);

    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_example, parent, false);
        MainViewHolder holder = new MainViewHolder(this, view);
        view.setOnClickListener(holder);
        view.setOnLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        int itemId = 0;
        if(position < data.size()) {
            itemId = integers.get(position);
            holder.text.setText(data.get(position) + "");
            holder.jabber = false;

        }else{
            integers.get(integers.size()-1);
            holder.text.setText("relleno");
            holder.jabber = true;
        }
  //      holder.text.setText(data.get(integers.get(position)) + "");
        // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.container.postInvalidate();
    }

    @Override
    public long getItemId(int position) {
        if(position >= integers.size()){
            return -1;
        }
        return integers.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size() +(6 - data.size()%6);
    }

    @Override
    public int getPositionForId(long id) {
        return integers.indexOf((int) id);
    }

    @Override
    public boolean move(int fromPosition, int toPosition) {
        int limit = integers.size();
        if( fromPosition < limit && toPosition < limit) {
            if (canMove) {
                data.add(toPosition, data.remove(fromPosition));
                integers.add(toPosition, integers.remove(fromPosition));

     /*       if (listener != null) {
                listener.onItemMoved(fromPosition, toPosition);
            }
       */
            }
        }else{
            return false;
        }
        return canMove;
    }


    void setOnItemMovedListener(onItemMovedListener listener){
        this.listener = listener;
    }

    static class MainViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        @InjectView(R.id.container)
        ViewGroup container;
        @InjectView(R.id.text)
        TextView text;
        boolean jabber = false;

        public MainViewHolder(DragSortAdapter adapter, View itemView) {
            super(adapter, itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onClick(@NonNull View v) {
            Log.d(TAG, text.getText() + " clicked!");
        }

        @Override
        public boolean onLongClick(@NonNull View v) {
            if(! jabber) startDrag();
            return true;
        }

        @Override
        public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
            return new NoForegroundShadowBuilder(itemView, touchPoint);
        }
    }
}
