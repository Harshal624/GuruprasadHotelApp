package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;

public class ParcelFragment extends Fragment {
    private static final String PARCELS = "PARCELS";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private FloatingActionButton addParcel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private ParcelFirestoreAdapter adapter;
    private SharedPreferences preferences;
    private CollectionReference parcelRef, currRef, confRef;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    public static boolean ismanager = false;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    public static boolean isconfirmed = false;

    private View pinView;
    private androidx.appcompat.app.AlertDialog.Builder Alertbuilder;
    private ImageButton confirmpin, cancelpin;
    private EditText enter_pin;
    private androidx.appcompat.app.AlertDialog pinAlert;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parcel_fragment, container, false);
        db = FirebaseFirestore.getInstance();
        addParcel = view.findViewById(R.id.parcelAdd);
        recyclerView = view.findViewById(R.id.recyclerViewParcel);
        preferences = getContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        layoutManager = new LinearLayoutManager(getContext());
        builder = new AlertDialog.Builder(getContext());
        alertDialog = builder.create();
        if (auth.getUid().equals(getContext().getResources().getString(R.string.MANAGER_UID))) {
            Alertbuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            pinView = inflater.inflate(R.layout.pin_alertdialog, null);
            pinAlert = Alertbuilder.create();
            confirmpin = pinView.findViewById(R.id.confirmpin);
            cancelpin = pinView.findViewById(R.id.cancelpin);
            enter_pin = pinView.findViewById(R.id.enter_pin);
            ismanager = true;
        } else {
            ismanager = false;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddParcel.class));

            }
        });

        setupRecyclerView();

        adapter.setOnItemClickListener(new ParcelFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
                if (ismanager) {
                    pinAlert.setView(pinView);
                    pinAlert.setCancelable(true);
                    pinAlert.show();
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
                            db.collection(Constants.MANAGER_PIN).document(Constants.PIN).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int manager_pin = task.getResult().getDouble("pin").intValue();
                                        if (input_pin == manager_pin) {
                                            pinAlert.dismiss();
                                            String parcel_id = documentSnapshot.getId();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString(PARCEL_ID_KEY, parcel_id);
                                            editor.commit();
                                            startActivity(new Intent(getContext(), FoodMenuParcel.class));
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
                } else {
                    String parcel_id = documentSnapshot.getId();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PARCEL_ID_KEY, parcel_id);
                    editor.commit();
                    startActivity(new Intent(getContext(), FoodMenuParcel.class));
                }
            }
        });

        adapter.setOnItemLongClickListener(new ParcelFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final DocumentSnapshot documentSnapshot, final int pos) {
                //check current and confirmed cost first
                //delete subcollections
                //delete parent document
                ParcelModel model = documentSnapshot.toObject(ParcelModel.class);
                String cust_name = model.getCustomer_name();
                alertDialog.setTitle("Delete Parcel");
                alertDialog.setIcon(R.drawable.parcel);
                alertDialog.setMessage("Are you sure want to delete the parcel of " + cust_name + "?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String doc_id = documentSnapshot.getId();
                        confRef = db.collection(PARCELS).document(doc_id).collection(CONFIRMED_KOT);
                        currRef = db.collection(PARCELS).document(doc_id).collection(CURRENT_KOT);
                        parcelRef = db.collection(PARCELS);
                        checkCostandDelete(doc_id, pos);
                    }
                });
                alertDialog.show();
            }

            private void checkCostandDelete(final String doc_id, final int pos) {
                parcelRef.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            double current_cost = task.getResult().getDouble("current_cost");
                            double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                            if (current_cost == 0 && confirmed_cost == 0) {
                                deleteParentDoc();
                            } else if (confirmed_cost != 0 && current_cost == 0) {
                                deleteConfCost();
                            } else if (confirmed_cost == 0 && current_cost != 0) {
                                deleteCurrCost();
                            } else {
                                deleteBoth();
                            }
                        } else {
                            //failed
                            Toast.makeText(getContext(), "Failed to delete order, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void deleteBoth() {
                        final WriteBatch batch = db.batch();
                        final DocumentReference parRef = parcelRef.document(doc_id);
                        currRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        DocumentReference currentKotRef =
                                                currRef.document(snapshot.getId());
                                        batch.delete(currentKotRef);
                                    }
                                    confRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                    DocumentReference confKotRef =
                                                            confRef.document(snapshot.getId());
                                                    batch.delete(confKotRef);
                                                }
                                                batch.delete(parRef);
                                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Parcel removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                    private void deleteConfCost() {
                        final WriteBatch batch = db.batch();
                        final DocumentReference parRef = parcelRef.document(doc_id);
                        confRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        DocumentReference confKotRef =
                                                confRef.document(snapshot.getId());
                                        batch.delete(confKotRef);
                                    }
                                    batch.delete(parRef);
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Parcel removed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                    private void deleteCurrCost() {
                        final WriteBatch batch = db.batch();
                        final DocumentReference parRef = parcelRef.document(doc_id);
                        currRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        DocumentReference currentKotRef =
                                                currRef.document(snapshot.getId());
                                        batch.delete(currentKotRef);
                                    }
                                    batch.delete(parRef);
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Parcel removed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                    private void deleteParentDoc() {
                        WriteBatch batch = db.batch();
                        DocumentReference reference = parcelRef.document(doc_id);
                        batch.delete(reference);
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Parcel removed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void setupRecyclerView() {
        Query query = db.collection(PARCELS);
        FirestoreRecyclerOptions<ParcelModel> parcel = new FirestoreRecyclerOptions.Builder<ParcelModel>()
                .setQuery(query, ParcelModel.class)
                .build();
        adapter = new ParcelFirestoreAdapter(parcel, getView());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
