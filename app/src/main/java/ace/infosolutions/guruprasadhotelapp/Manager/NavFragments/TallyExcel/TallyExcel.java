package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ace.infosolutions.guruprasadhotelapp.R;

public class TallyExcel extends Fragment {
    private DatePicker datePicker;
    private int day, month, year;
    private Button calculate;
    private String type = "Daily";
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
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setTitle("Calculating...");
                dialog.setMessage("Please wait for sometime");
                dialog.setIcon(R.drawable.tally);
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth();
                        year = datePicker.getYear();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                        String formatedDate = sdf.format(calendar.getTime());
                        Intent intent = new Intent(getContext(), CalculateTallyExcel.class);
                        if (type.equals("Daily")) {
                            //day format - 02-04-20
                            intent.putExtra("DATE", formatedDate);
                        } else {
                            //month format - 02-20
                            intent.putExtra("DATE", formatedDate.substring(3));
                        }

                        intent.putExtra("TYPE", type);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }, 1000);

            }
        });


    }
}
