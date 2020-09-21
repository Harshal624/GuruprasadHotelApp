package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.R;


public class RequestedKOTFirestoreAdapter extends FirestoreRecyclerAdapter<requestedkotmodel, RequestedKOTFirestoreAdapter.CustomerHolder > {
    private OnItemClickListener listener;
    private View reqkotView;
    private TextView emptyKOTreq;
    private ImageView emptyKOTreqs;

    public RequestedKOTFirestoreAdapter(@NonNull FirestoreRecyclerOptions<requestedkotmodel> options, View view) {
        super(options);
        this.reqkotView = view;
        emptyKOTreq = (TextView) reqkotView.findViewById(R.id.noreqkots);
        emptyKOTreqs= (ImageView) reqkotView.findViewById(R.id.empty_cart);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if(getItemCount() == 0){
           emptyKOTreq.setVisibility(View.VISIBLE);
           emptyKOTreqs.setVisibility(View.VISIBLE);
        }
        else{
            emptyKOTreq.setVisibility(View.GONE);
            emptyKOTreqs.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull requestedkotmodel model) {
        holder.table_type.setText(model.getTable_type());
        holder.table_no.setText(""+model.getTable_no());
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlistitem_reqkot,parent,false);
        return new CustomerHolder(view);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView table_no;
        private TextView table_type;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.tableno);
            table_type = itemView.findViewById(R.id.tabletype);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if(pos != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}

