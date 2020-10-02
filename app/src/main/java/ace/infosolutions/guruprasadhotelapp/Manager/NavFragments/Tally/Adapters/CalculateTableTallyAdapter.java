package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.TableTotalModel;
import ace.infosolutions.guruprasadhotelapp.R;


public class CalculateTableTallyAdapter extends FirestoreRecyclerAdapter<TableTotalModel, CalculateTableTallyAdapter.CustomerHolder > {
    private View view;
    private ImageView empty_cartIV;
    private TextView empty_cartTV;

    public CalculateTableTallyAdapter(@NonNull FirestoreRecyclerOptions<TableTotalModel> options) {
        super(options);
    }
    public CalculateTableTallyAdapter(@NonNull FirestoreRecyclerOptions<TableTotalModel> options, View view) {
        super(options);
        this.view = view;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull TableTotalModel model) {
          holder.table_no.setText("Table No: "+model.getTable_no());
          holder.total.setText(""+model.getTabletotal());
    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_tally_item,parent,false);
        return new CustomerHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        try {
            empty_cartIV = view.findViewById(R.id.empty_cartIV);
            empty_cartTV = view.findViewById(R.id.empty_cart);
            if(getItemCount() == 0){
                empty_cartIV.setVisibility(View.VISIBLE);
                empty_cartTV.setVisibility(View.VISIBLE);
            }
            else{
                empty_cartIV.setVisibility(View.GONE);
                empty_cartTV.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Exception",e.toString());
        }
    }

    public class CustomerHolder extends RecyclerView.ViewHolder{
      private TextView table_no;
        private TextView total;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.table_no);
            total = itemView.findViewById(R.id.total);

        }
    }


}

