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

import ace.infosolutions.guruprasadhotelapp.R;


public class TableDateListAdapter extends FirestoreRecyclerAdapter<DateModel, TableDateListAdapter.CustomerHolder > {
    private View view;
    private ImageView empty_cartIV;
    private TextView empty_cartTV;
    OnDateClickListener listener;


    public TableDateListAdapter(@NonNull FirestoreRecyclerOptions<DateModel> options) {
        super(options);
    }
    public TableDateListAdapter(@NonNull FirestoreRecyclerOptions<DateModel> options, View view) {
        super(options);
        this.view = view;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull DateModel model) {

          holder.date.setText(model.getDate());
    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabledatetally_item,parent,false);
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
      private TextView date;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION && listener!=null){
                        listener.OnDateClick(getSnapshots().getSnapshot(pos));
                    }
                }
            });
        }
    }

    public interface OnDateClickListener{
        void OnDateClick(DocumentSnapshot snapshot);
    }
    public void setOnDateClickListener(OnDateClickListener listener){
        this.listener = listener;
    }

}

