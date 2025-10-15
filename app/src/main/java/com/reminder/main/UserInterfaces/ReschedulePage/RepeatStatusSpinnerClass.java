package com.reminder.main.UserInterfaces.ReschedulePage;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;


public class RepeatStatusSpinnerClass extends AppCompatSpinner implements AdapterView.OnItemSelectedListener {

    private int prevSelection;
    private final ApplicationCustomInterfaces.RepeatStatus repeatStatus;

    public RepeatStatusSpinnerClass(@NonNull Context context, int initialRepeatStatus, AppCompatSpinner spinner) {
        super(context);

        this.prevSelection = initialRepeatStatus;
        this.repeatStatus = (ApplicationCustomInterfaces.RepeatStatus) context;
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.repeatType, R.layout.spinner_layout);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(initialRepeatStatus - 1);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        position += 1;
        if (prevSelection != position) {
            prevSelection = position;
            repeatStatus.setRepeatStatus((byte) position);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
