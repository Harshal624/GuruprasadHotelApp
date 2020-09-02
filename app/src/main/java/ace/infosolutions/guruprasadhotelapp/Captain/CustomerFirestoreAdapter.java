package ace.infosolutions.guruprasadhotelapp.Captain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import ace.infosolutions.guruprasadhotelapp.R;


public class CustomerFirestoreAdapter extends FirestoreRecyclerAdapter<customerclass, CustomerFirestoreAdapter.CustomerHolder > {

    public CustomerFirestoreAdapter(@NonNull FirestoreRecyclerOptions<customerclass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull customerclass model) {
        holder.table_type.setText(model.getTable_type());
        holder.cost.setText(""+model.getCost());
        holder.table_no.setText(""+model.getTable_no());

    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlistitem,parent,false);
        return new CustomerHolder(view);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView table_no;
        private TextView table_type;
        private TextView cost;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.tableno);
            cost = itemView.findViewById(R.id.cost);
            table_type = itemView.findViewById(R.id.tabletype);
        }
    }
}
