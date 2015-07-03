package cat.helm.pumba;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.helm.OverlapingObservableViewGroup.OverlappingObservableViewGroup;
import cat.helm.draggablerecyclerpagerview.PagedLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.recycler)
    RecyclerView draggableRecyclerPagerView;
    @InjectView(R.id.parent_view_group)
    OverlappingObservableViewGroup viewGroup;
    @InjectView(R.id.select_button)
    TextView selectButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        List<Integer> items = createItems();
        PagedLayoutManager gridLayoutManager = new PagedLayoutManager(this, 2, 3);
        ExampleAdapter adapter = new ExampleAdapter(viewGroup, draggableRecyclerPagerView, items);
        draggableRecyclerPagerView.setLayoutManager(gridLayoutManager);
        draggableRecyclerPagerView.setAdapter(adapter);
        draggableRecyclerPagerView.addItemDecoration(new InsetDecoration(this));

        ViewTreeObserver vto = viewGroup.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("MainActivity", "onGlobalLayout" + "  ");
                viewGroup.onOverlapDraggingView(selectButton, new OverlappingObservableViewGroup.OnOverlapListener() {
                    @Override
                    public void onOverlap(View lastDragged) {
                        lastDragged.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        Log.e("MainActivity", "onOverlap" + "it works");
                    }
                });

            }
        });


    }

    private List<Integer> createItems() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            items.add(i);
        }
        return items;
    }
}
