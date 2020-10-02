package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class TallyFragment extends Fragment {

    private Button daily_grand,monthly_grand,daily_online,monthly_online,daily_parcel,monthly_parcel;
    private Button daily_table,monthly_table;

    private AlertDialog pinAlert;
    private View pinView;
    private AlertDialog.Builder builder;
    private ImageButton confirmpin, cancelpin;
    private EditText enter_pin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tallyfragment,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("Tally");
        daily_grand = view.findViewById(R.id.daily_grand);
        monthly_grand = view.findViewById(R.id.monthly_grand);
        daily_table = view.findViewById(R.id.daily_table);
        monthly_table = view.findViewById(R.id.monthly_table);
        builder = new AlertDialog.Builder(getContext());
        pinView = inflater.inflate(R.layout.pin_alertdialog, null);
        pinAlert = builder.create();
        confirmpin = pinView.findViewById(R.id.confirmpin);
        cancelpin = pinView.findViewById(R.id.cancelpin);
        enter_pin = pinView.findViewById(R.id.enter_pin);


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
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();

            }
        });

        monthly_grand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyGrand.class);
                intent.putExtra("TALLYTYPE","MONTHLYGRAND");
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });

        daily_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyParcel.class);
                intent.putExtra("TALLYTYPE","DAILYPARCEL");
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });

        monthly_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyParcel.class);
                intent.putExtra("TALLYTYPE","MONTHLYPARCEL");
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });

        daily_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyOnline.class);
                intent.putExtra("TALLYTYPE","DAILYONLINE");
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });

        monthly_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalculateTallyOnline.class);
                intent.putExtra("TALLYTYPE","MONTHLYONLINE");
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();

            }
        });

        daily_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TableTallyDateList.class);
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });

        monthly_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TableTallyMonthList.class);
                setUpPinAlert(intent);
                pinAlert.setView(pinView);
                pinAlert.setCancelable(true);
                pinAlert.show();
            }
        });
    }


    private void setUpPinAlert(final Intent intent) {
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
    }
}
