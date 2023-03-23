package io.github.sgpublic.xxpref;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

/**
 * @author Madray Haven
 * @date 2022/11/30 10:16
 */
public class SpEditorTest {
    @Test
    public void testEditor() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new SpEditor(appContext.getSharedPreferences("test", Context.MODE_PRIVATE).edit()).apply();
    }

    private static class SpEditor extends PrefEditor {
        private SpEditor(SharedPreferences.Editor editor) {
            super(editor);
        }
    }
}
