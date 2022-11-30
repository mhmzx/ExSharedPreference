package io.github.sgpublic.exsp;

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
        new Editor(appContext.getSharedPreferences("test", Context.MODE_PRIVATE).edit()).apply();
    }

    private static class Editor extends SpEditor {
        private Editor(SharedPreferences.Editor editor) {
            super(editor);
        }
    }
}
