package io.github.sgpublic.exsp.demo.java;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.github.sgpublic.exsp.demo.databinding.ActivityMainBinding;
import kotlin.Lazy;
import kotlin.LazyKt;

public class JavaActivity extends AppCompatActivity {
    private final Lazy<ActivityMainBinding> ViewBinding = LazyKt.lazy(() ->
            ActivityMainBinding.inflate(getLayoutInflater()));
    
    private ActivityMainBinding ViewBinding() {
        return ViewBinding.getValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ViewBinding().getRoot());
        onViewSetup();

        JavaPreference.getTestDateObserver();

        ViewBinding().msave.setOnClickListener((v) -> {
            try {
                JavaPreference.edit()
                        .setTestString(ViewBinding().mstring.getText().toString())
                        .setTestInt(Integer.parseInt(ViewBinding().minteger.getText().toString()))
                        .setTestFloat(Float.parseFloat(ViewBinding().mfloat.getText().toString()))
                        .setTestLong(Long.parseLong(ViewBinding().mlong.getText().toString()))
                        .setTestBool(ViewBinding().mbool.isChecked())
                        .setTestDate(new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
                                .parse(ViewBinding().mdate.getText().toString()))
                        .apply();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JavaPreference.setTestString(ViewBinding().mstring.getText().toString());
            if (ViewBinding().mtypea.isChecked()) {
                JavaPreference.setTestEnum(JavaPreference.Type.TYPE_A);
            } else if (ViewBinding().mtypeb.isChecked()) {
                JavaPreference.setTestEnum(JavaPreference.Type.TYPE_B);
            }
        });
    }

    private void onViewSetup() {
        ViewBinding().mstring.setText(JavaPreference.getTestString());
        ViewBinding().minteger.setText(String.valueOf(JavaPreference.getTestInt()));
        ViewBinding().mlong.setText(String.valueOf(JavaPreference.getTestLong()));
        ViewBinding().mfloat.setText(String.valueOf(JavaPreference.getTestFloat()));
        ViewBinding().mbool.setChecked(JavaPreference.isTestBool());
        ViewBinding().mdate.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
                .format(JavaPreference.getTestDate()));
        JavaPreference.Type testEnum = JavaPreference.getTestEnum();
        if (testEnum == JavaPreference.Type.TYPE_A) {
            ViewBinding().mtypea.setChecked(true);
        } else if (testEnum == JavaPreference.Type.TYPE_B) {
            ViewBinding().mtypeb.setChecked(true);
        }
    }
}
