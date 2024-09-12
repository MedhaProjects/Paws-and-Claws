package com.example.vetrinarymanagemnet;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetrinarymanagemnet.ui.home.HomeFragment;

public class ClinicListAdapter extends ArrayAdapter<Clinic> {

    private Context context;
    private Location userLocation;
    private List<Booking> bookings; // List for Booking class instances

    public ClinicListAdapter(@NonNull Context context, @NonNull List<Clinic> clinics, Location userLocation) {
        super(context, 0, clinics);
        this.context = context;
        this.userLocation = userLocation;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Clinic clinic = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.clinic_list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.clinic_name);
        TextView addressTextView = convertView.findViewById(R.id.clinic_address);
        TextView distanceTextView = convertView.findViewById(R.id.clinic_distance);
        Button bookNowButton = convertView.findViewById(R.id.book_now_button);
        Button viewOnMapButton = convertView.findViewById(R.id.view_on_map_button);

        if (clinic != null) {
            nameTextView.setText(clinic.getName());
            addressTextView.setText("Address: " + clinic.getAddress());

            float[] results = new float[1];
            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                    clinic.getLatitude(), clinic.getLongitude(), results);
            float distance = results[0];
            distanceTextView.setText("Distance: " + String.format("%.2f", distance / 1000) + " km");
        }

        // Set up the "View on Map" button to open Google Maps with directions
        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + clinic.getLatitude() + "," + clinic.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Check if any app can handle this intent
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(mapIntent);
                } else {
                    // Fallback to Google Maps URL if the app cannot be found
                    Uri mapWebUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                            + clinic.getLatitude() + "," + clinic.getLongitude());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, mapWebUri);
                    getContext().startActivity(webIntent);
                }
            }
        });

        // Handle "Book Now" button click
        bookNowButton.setOnClickListener(v -> showBookingForm());

        return convertView;
    }

    private void showBookingForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View formView = LayoutInflater.from(context).inflate(R.layout.booking_form, null);

        DatePicker datePicker = formView.findViewById(R.id.date_picker);
        Spinner timeSpinner = formView.findViewById(R.id.time_spinner);
        Button confirmButton = formView.findViewById(R.id.confirm_button);

        // Set up date picker to limit to 30 days
        Calendar calendar = Calendar.getInstance();
        datePicker.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        datePicker.setMaxDate(calendar.getTimeInMillis());

        // Populate time spinner with 30-minute slots from 10 AM to 9 PM, with default prompt
        ArrayList<String> timeSlots = new ArrayList<>();
        timeSlots.add("Select Time Slot"); // Prompt for default unselected state
        for (int hour = 10; hour <= 21; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                timeSlots.add(time);
            }
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        builder.setView(formView);
        AlertDialog dialog = builder.create();

        // Handle confirm button click
        confirmButton.setOnClickListener(v -> {
            // Collect date and time selections
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            String selectedTime = timeSpinner.getSelectedItem().toString();

            // Check if time slot is selected
            if (selectedTime.equals("Select Time Slot")) {
                Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT).show();
            } else {
                // Process the booking with the selected date and time
                dialog.dismiss();

                // Show "Booking Successful" toast message
                Toast.makeText(context, "Booking Successful", Toast.LENGTH_SHORT).show();

                // Delay to show the toast before navigating back to the home page
                new android.os.Handler().postDelayed(() -> {
                    // Navigate back to the home page
                    Intent intent = new Intent(context, HomeFragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }, 2000); // 2 seconds delay
            }
        });

        dialog.show();
    }

    // RecyclerView Adapter ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView petNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petNameTextView = itemView.findViewById(R.id.petname); // Assuming an ID for pet name view
        }

        public void bindData(Booking booking) {
            petNameTextView.setText(booking.getPetName()); // Bind pet name from booking
        }
    }

    // RecyclerView Adapter onBindViewHolder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bindData(booking);
    }
}
