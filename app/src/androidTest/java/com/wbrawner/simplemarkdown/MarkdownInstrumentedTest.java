package com.wbrawner.simplemarkdown;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.commonsware.cwac.anddown.AndDown;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MarkdownInstrumentedTest {
    private AndDown andDown;
    private int HOEDOWN_FLAGS =
            AndDown.HOEDOWN_EXT_STRIKETHROUGH | AndDown.HOEDOWN_EXT_TABLES |
                    AndDown.HOEDOWN_EXT_UNDERLINE | AndDown.HOEDOWN_EXT_SUPERSCRIPT |
                    AndDown.HOEDOWN_EXT_FENCED_CODE;

    @Before
    public void setup() {
        this.andDown = new AndDown();
    }

    @Test
    public void italics_areCorrect() throws Exception {
        String markdown = "*italics*";
        String html = andDown.markdownToHtml(markdown, HOEDOWN_FLAGS, 0);
        Log.d("TAG_HERE", html);
        assertEquals("Italics", "<p><em>italics</em></p>\n", html);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wbrawner.simplemarkdown", appContext.getPackageName());
    }
}
