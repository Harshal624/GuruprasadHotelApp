package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class TallyFragment extends Fragment {

    private Button daily_grand,monthly_grand,daily_online,monthly_online,daily_parcel,monthly_parcel;
    private Button daily_table,monthly_table;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tallyfragment,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("Tally");
        daily_grand = view.findViewById(R.id.daily_grand);
        monthly_grand = view.findViewById(R.id.monthly_grand);
        daily_table = view.findViewById(R.id.daily_table);
        monthly_table = view.findViewById(R.id.monthly_table);



        daily_parcel = view.findViewById(R.id.daily_parcel);
        monthly_parcel = view.findViewById(R.id.monthly_parcel);

        daily_online = view.findViewById(R.id.daily_online);
        monthly_online = view.findViewById(R.id.monthly_online);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daily_grand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyGrand.class);
                intent.putExtra("TALLYTYPE","DAILYGRAND");
                startActivity(intent);
            }
        });

        monthly_grand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyGrand.class);
                intent.putExtra("TALLYTYPE","MONTHLYGRAND");
                startActivity(intent);
            }
        });

        daily_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyParcel.class);
                intent.putExtra("TALLYTYPE","DAILYPARCEL");
                startActivity(intent);
            }
        });

        monthly_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyParcel.class);
                intent.putExtra("TALLYTYPE","MONTHLYPARCEL");
                startActivity(intent);
            }
        });

        daily_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyOnline.class);
                intent.putExtra("TALLYTYPE","DAILYONLINE");
                startActivity(intent);
            }
        });

        monthly_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyOnline.class);
                intent.putExtra("TALLYTYPE","MONTHLYONLINE");
                startActivity(intent);
            }
        });

        daily_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TableTallyDateList.class));
            }
        });

        monthly_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TableTallyMonthList.class));
            }
        });
    }
}
