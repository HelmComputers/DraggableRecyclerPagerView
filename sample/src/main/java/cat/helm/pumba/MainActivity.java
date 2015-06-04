package cat.helm.pumba;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.helm.draggablerecyclerpagerview.PagedLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.recycler)
    RecyclerView draggableRecyclerPagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        List<String> items = createItems();
        PagedLayoutManager gridLayoutManager = new PagedLayoutManager(this, 2,3);
        Adapter adapter = new Adapter(items);
        draggableRecyclerPagerView.setLayoutManager(gridLayoutManager);
        draggableRecyclerPagerView.setAdapter(adapter);
    }

    private List<String> createItems() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            items.add(String.valueOf(i) );
        }
        return items;
    }

}
