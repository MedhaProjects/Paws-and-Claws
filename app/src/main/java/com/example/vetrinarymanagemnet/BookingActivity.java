package com.example.vetrinarymanagemnet;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private Spinner petSpinner;
    private List<String> petNames = new ArrayList<>();
    private DatabaseReference petReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_form);

        petSpinner = findViewById(R.id.pet_spinner);
        petReference = FirebaseDatabase.getInstance().getReference("Users");

        // Fetch pet names from Firebase
        fetchPetNames();

        // Set a listener for the confirm button
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle booking confirmation and send email
                confirmBooking();
            }
        });
    }

    private void fetchPetNames() {
        petReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                petNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String petName = snapshot.child("petname").getValue(String.class);
                    if (petName != null) {
                        petNames.add(petName);
                    }
                }
                if (petNames.isEmpty()) {
                    Toast.makeText(BookingActivity.this, "No pets found. Please add a pet profile first.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_spinner_item, petNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    petSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookingActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmBooking() {
        // Get the selected pet name
        String selectedPet = petSpinner.getSelectedItem().toString();
        // Retrieve other booking details, then send the confirmation email
        sendConfirmationEmail(selectedPet);
    }

    private void sendConfirmationEmail(String petName) {
        // Include the pet name in the email body
        String emailBody = "Booking confirmed for pet: " + petName;
        // Implement email sending logic here
    }
}
