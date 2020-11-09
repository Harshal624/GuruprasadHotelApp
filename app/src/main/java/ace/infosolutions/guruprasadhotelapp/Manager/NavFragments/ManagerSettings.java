package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.MANAGER_PIN;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PIN;

public class ManagerSettings extends Fragment {
    private Button confirm;
    private EditText old_pin, new_pin, new_pin_confirm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference managerPinRef =
            db.collection(MANAGER_PIN).document(PIN);
    private TextView wrongpin;
    //  private SharedPreferences printerSharedPref;
    //   private EditText printerName;
    //  private Button printerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.changemanagerpin, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Change PIN");
        // printerName = view.findViewById(R.id.printerName);
        // printerButton = view.findViewById(R.id.printerButton);
        //   printerSharedPref = getContext().getSharedPreferences(Constants.SP_PRINTER, Context.MODE_PRIVATE);
        old_pin = view.findViewById(R.id.oldpin);
        new_pin = view.findViewById(R.id.newpin);
        confirm = view.findViewById(R.id.confirmpin);
        new_pin_confirm = view.findViewById(R.id.newpinconfirm);
        wrongpin = view.findViewById(R.id.retry);
        //   String printername = printerSharedPref.getString(Constants.SP_PRINTER_NAME, "");
     /*   try {
            if (!printername.isEmpty()) {
                printerName.setHint(printername);
            }
        } catch (NullPointerException e) {
            printerName.setHint("Enter printer name");
        }*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (old_pin.getText().toString().equals("") && new_pin.getText().toString().equals("")) {
                    old_pin.setError("Enter old pin");
                    new_pin.setError("Enter new pin");
                    old_pin.requestFocus();
                } else if (!old_pin.getText().toString().equals("") && new_pin.getText().toString().equals("")) {
                    new_pin.setError("Enter new pin");
                    new_pin.requestFocus();
                } else if (old_pin.getText().toString().equals("") && !new_pin.getText().toString().equals("")) {
                    old_pin.setError("Enter old pin");
                    old_pin.requestFocus();
                } else {
                    if (new_pin.getText().toString().equals(new_pin_confirm.getText().toString())) {
                        confirm.setEnabled(false);
                        changePin(old_pin.getText().toString(), new_pin.getText().toString());
                    } else {
                        new_pin_confirm.setError("*The specified pins do not match");
                    }
                }
            }
        });

       /* printerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String printerN = printerName.getText().toString().trim();
                if (printerN.isEmpty()) {
                    printerName.setError("Empty");
                } else {
                    SharedPreferences.Editor editor = printerSharedPref.edit();
                    editor.putString(Constants.SP_PRINTER_NAME, printerN);
                    editor.commit();
                    printerName.setText("");
                    printerName.setHint(printerN);
                    Toast.makeText(getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void changePin(String oldpinString, String newpinString) {
        final int old_pin = Integer.parseInt(oldpinString);
        final int new_pin = Integer.parseInt(newpinString);

        managerPinRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                int old_pinStored = snapshot.getDouble("pin").intValue();
                if (old_pin == old_pinStored) {
                    managerPinRef.update("pin", new_pin);
                    Toast.makeText(getContext(), "PIN Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), Manager.class));
                } else {
                    CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long l) {
                            confirm.setEnabled(false);
                            wrongpin.setVisibility(View.VISIBLE);
                            confirm.setText("Retry in " + l / 1000 + " seconds");
                        }

                        @Override
                        public void onFinish() {
                            confirm.setEnabled(true);
                            wrongpin.setVisibility(View.GONE);
                            confirm.setText("Confirm");
                        }
                    };
                    countDownTimer.start();
                }
            }
        });

    }


}
