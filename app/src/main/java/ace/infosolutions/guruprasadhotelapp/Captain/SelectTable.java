package ace.infosolutions.guruprasadhotelapp.Captain;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import ace.infosolutions.guruprasadhotelapp.R;

public class SelectTable extends AppCompatActivity {
    private RecyclerView tableListRecycler;
    private RecyclerView.Adapter tableListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_table);
        tableListRecycler = findViewById(R.id.tablerecycler);
        ArrayList<String> tables = new ArrayList<>();
        Collections.addAll(tables,getResources().getStringArray(R.array.Tables));


        //Set up table recyclerview
        tableListRecycler.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        tableListAdapter = new TableAdapter(tables);
        tableListRecycler.setLayoutManager(layoutManager);
        tableListRecycler.setAdapter(tableListAdapter);

    }
}
