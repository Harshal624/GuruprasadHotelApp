package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class TallyExcel extends Fragment {
    private DatePicker datePicker;
    private int day, month, year;
    private Button calculate;
    private String type = "Daily";
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tally_excel, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        calculate = view.findViewById(R.id.calculate);
        ((Manager) getActivity()).toolbar.setTitle("Tally");
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
                setUpPinAlert();
            }
        });
    }

    private void setUpPinAlert() {

        View pinView = LayoutInflater.from(getContext()).inflate(R.layout.pin_alertdialog, null);
        final EditText enter_pin = pinView.findViewById(R.id.enter_pin);
        ImageButton confirmpin = pinView.findViewById(R.id.confirmpin);
        ImageButton cancelpin = pinView.findViewById(R.id.cancelpin);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog pinAlert = builder.create();
        pinAlert.setView(pinView);

        enter_pin.setText("");
        enter_pin.requestFocus();
        enter_pin.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(enter_pin, 0);
            }
        }, 100);
        confirmpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_pin.getText().toString().trim().equals("") || enter_pin.getText().toString().trim().equals(null)) {
                    enter_pin.setError("Enter manager pin");
                } else {
                    int input_pin = Integer.parseInt(enter_pin.getText().toString().trim());
                    verifyPin(input_pin);
                }
            }

            private void verifyPin(final int input_pin) {
                db.collection("MANAGERPIN").document("PIN").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            int manager_pin = task.getResult().getDouble("pin").intValue();
                            if (input_pin == manager_pin) {
                                pinAlert.dismiss();
                                /*Intent intent = new Intent(getContext(), ConfirmFinalBill.class);
                                startActivity(intent);*/
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

                            } else {
                                enter_pin.setError("Wrong Pin");
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to verify pin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        cancelpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinAlert.dismiss();
            }
        });
        pinAlert.show();
    }
}
