package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ace.infosolutions.guruprasadhotelapp.R;

public class TallyExcel extends Fragment {
    private DatePicker datePicker;
    private int day, month, year;
    private Button calculate;
    private String type = "Daily";
    private String type2 = "Order";
    private RadioButton radioButton;
    private RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tally_excel, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        calculate = view.findViewById(R.id.calculate);
        radioGroup = view.findViewById(R.id.radiogroup);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = view.findViewById(i);
                if (radioButton.getText().toString().equals("Daily")) {
                    type = "Daily";
                } else {
                    type = "Monthly";
                }
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth() + 1;
                year = datePicker.getYear();
                String year_string = String.valueOf(year);
                String new_year = year_string.substring(2, 4);
                String formed_date = String.valueOf(day) + "-" + String.valueOf(month) + "-" + new_year;
                Intent intent = new Intent(getContext(), CalculateTallyExcel.class);
                intent.putExtra("DATE", formed_date);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });


    }
}
