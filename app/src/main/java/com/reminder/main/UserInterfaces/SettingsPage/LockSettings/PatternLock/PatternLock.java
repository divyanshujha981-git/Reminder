package com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PatternLock;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.itsxtt.patternlock.PatternLockView;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockData;

import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.example.reminder/dex-files/0.dex */
public class PatternLock extends AppCompatActivity {
    public static final String PATTERN = "pattern";
    private ArrayList<Integer> initialPattern;
    private TextView patternText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_layout);
        PatternLockView patternLockView = (PatternLockView) findViewById(R.id.patternLockView);
        this.patternText = (TextView) findViewById(R.id.patternText);
        patternLockView.setOnPatternListener(new PatternLockView.OnPatternListener() { // from class: com.example.reminder.UserInterfaces.SettingsPage.LockSettings.PatternLock.PatternLock.1
            @Override // com.itsxtt.patternlock.PatternLockView.OnPatternListener
            public void onStarted() {
            }

            @Override // com.itsxtt.patternlock.PatternLockView.OnPatternListener
            public void onProgress(ArrayList<Integer> arrayList) {
            }

            @Override // com.itsxtt.patternlock.PatternLockView.OnPatternListener
            public boolean onComplete(ArrayList<Integer> arrayList) {
                if (PatternLock.this.initialPattern == null) {
                    PatternLock.this.initialPattern = arrayList;
                    PatternLock.this.patternText.setText(PatternLock.this.getString(R.string.re_enter_the_pattern));
                    return true;
                } else if (PatternLock.this.initialPattern.toString().equals(arrayList.toString())) {
                    LockData.setPattern(PatternLock.this, PatternLock.this.initialPattern.toString());
                    PatternLock.this.finish();
                    return true;
                } else {
                    PatternLock.this.patternText.setText(PatternLock.this.getString(R.string.pattern_pin_password_error));
                    return false;
                }
            }
        });
        findViewById(R.id.patternReset).setOnClickListener(new View.OnClickListener() { // from class: com.example.reminder.UserInterfaces.SettingsPage.LockSettings.PatternLock.PatternLock$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PatternLock.this.m105lambda$onCreate$0$comexamplereminderUserInterfacesSettingsPageLockSettingsPatternLockPatternLock(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-reminder-UserInterfaces-SettingsPage-LockSettings-PatternLock-PatternLock  reason: not valid java name */
    public /* synthetic */ void m105lambda$onCreate$0$comexamplereminderUserInterfacesSettingsPageLockSettingsPatternLockPatternLock(View v) {
        this.initialPattern = null;
        this.patternText.setText(getString(R.string.please_enter_the_pattern));
    }
}
