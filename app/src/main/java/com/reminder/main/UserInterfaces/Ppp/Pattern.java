package com.reminder.main.UserInterfaces.Ppp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.itsxtt.patternlock.PatternLockView;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;

import java.util.ArrayList;

public class Pattern extends Fragment {

    private final String key;
    private ApplicationCustomInterfaces.AllowUserToNavigate allowUserToNavigate;


    public Pattern(String key) {
        this.key = key;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pattern, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allowUserToNavigate = (ApplicationCustomInterfaces.AllowUserToNavigate) requireContext();


        ((PatternLockView) view.findViewById(R.id.patternLockView)).setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {

            }

            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                if (key == null || key.length() == 0) {
                    Toast.makeText(requireContext(), "Please set your pattern first", Toast.LENGTH_SHORT).show();
                } else {
                    if (arrayList.toString().equals(key)) {
                        // do something...
                        allowUserToNavigate.authorized();
                        return true;
                    }
                }
                return false;

            }
        });
    }
}
