package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class ChangeManagerPin extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button confirm;
    private EditText old_pin, new_pin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.changemanagerpin, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Change PIN");
        old_pin = view.findViewById(R.id.oldpin);
        new_pin = view.findViewById(R.id.newpin);
        confirm = view.findViewById(R.id.confirmpin);
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
                } else if (!old_pin.getText().toString().equals("") && new_pin.getText().toString().equals("")) {
                    new_pin.setError("Enter new pin");
                } else if (old_pin.getText().toString().equals("") && !new_pin.getText().toString().equals("")) {
                    old_pin.setError("Enter old pin");
                } else {
                    Toast.makeText(getContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
