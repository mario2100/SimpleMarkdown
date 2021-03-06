package com.wbrawner.simplemarkdown

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.KeyEvent
import androidx.core.content.FileProvider
import androidx.test.InstrumentationRegistry
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.wbrawner.simplemarkdown.view.activity.MainActivity
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.Reader

class MarkdownTests {

    @get:Rule
    var activityRule = IntentsTestRule(MainActivity::class.java, false, false)

    lateinit var file: File

    @Before
    fun setup() {
        file = File(getApplicationContext<Context>().filesDir.absolutePath + "/tmp", "temp.md")
        file.parentFile?.mkdirs()
        file.delete()
        activityRule.launchActivity(null)
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @Test
    @Throws(Exception::class)
    fun openAppTest() {
        val mDevice = UiDevice.getInstance(getInstrumentation())
        mDevice.pressHome()
        // Bring up the default launcher by searching for a UI component
        // that matches the content description for the launcher button.
        val allAppsButton = mDevice
                .findObject(UiSelector().description("Apps"))

        // Perform a click on the button to load the launcher.
        allAppsButton.clickAndWaitForNewWindow()
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.wbrawner.simplemarkdown", appContext.packageName)
        val appView = UiScrollable(UiSelector().scrollable(true))
        val simpleMarkdownSelector = UiSelector()
                .text(getApplicationContext<Context>().getString(R.string.app_name_short))
        appView.scrollIntoView(simpleMarkdownSelector)
        mDevice.findObject(simpleMarkdownSelector).clickAndWaitForNewWindow()
    }

    @Test
    fun editAndPreviewMarkdownTest() {
        onView(withId(R.id.markdown_edit)).perform(typeText("# Header test"))
        onView(withText(R.string.action_preview)).perform(click())
        onWebView(withId(R.id.markdown_view)).forceJavascriptEnabled()
                .withElement(findElement(Locator.TAG_NAME, "h1"))
                .check(webMatches(getText(), containsString("Header test")))
    }

    @Test
    fun newMarkdownTest() {
        onView(withId(R.id.markdown_edit))
                .perform(typeText("# UI Testing\n\nThe quick brown fox jumped over the lazy dog."))
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_new)).perform(click())
        onView(withText(R.string.action_discard)).perform(click())
        onView(withId(R.id.markdown_edit)).check(matches(withText("")))
    }

    @Test
    fun saveMarkdownWithFileUriTest() {
        val markdownText = "# UI Testing\n\nThe quick brown fox jumped over the lazy dog."
        onView(withId(R.id.markdown_edit)).perform(typeText(markdownText))
        val activityResult = Instrumentation.ActivityResult(RESULT_OK, Intent().apply {
            data = Uri.fromFile(file)
        })
        intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(activityResult)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_save_as)).perform(click())
        Thread.sleep(500)
        assertEquals(markdownText, file.inputStream().reader().use(Reader::readText))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
    }

    @Test
    fun saveMarkdownWithContentUriTest() {
        val markdownText = "# UI Testing\n\nThe quick brown fox jumped over the lazy dog."
        onView(withId(R.id.markdown_edit)).perform(typeText(markdownText))
        val activityResult = Instrumentation.ActivityResult(RESULT_OK, Intent().apply {
            data = FileProvider.getUriForFile(getApplicationContext(), "com.wbrawner.simplemarkdown.fileprovider", file)
        })
        intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(activityResult)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_save_as)).perform(click())
        Thread.sleep(500)
        assertEquals(markdownText, file.inputStream().reader().use(Reader::readText))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
    }

    @Test
    fun loadMarkdownWithFileUriTest() {
        val markdownText = "# UI Testing\n\nThe quick brown fox jumped over the lazy dog."
        file.outputStream().writer().use { it.write(markdownText) }
        val activityResult = Instrumentation.ActivityResult(RESULT_OK, Intent().apply {
            data = Uri.fromFile(file)
        })
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(activityResult)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_open)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.markdown_edit)).check(matches(withText(markdownText)))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
    }

    @Test
    fun loadMarkdownWithContentUriTest() {
        val markdownText = "# UI Testing\n\nThe quick brown fox jumped over the lazy dog."
        file.outputStream().writer().use { it.write(markdownText) }
        val activityResult = Instrumentation.ActivityResult(RESULT_OK, Intent().apply {
            data = FileProvider.getUriForFile(getApplicationContext(), "com.wbrawner.simplemarkdown.fileprovider", file)
        })
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(activityResult)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_open)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.markdown_edit)).check(matches(withText(markdownText)))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
    }


    @Test
    fun openEditAndSaveMarkdownTest() {
        val markdownText = "# UI Testing\n\nThe quick brown fox jumped over the lazy dog."
        file.outputStream().writer().use { it.write(markdownText) }
        val activityResult = Instrumentation.ActivityResult(RESULT_OK, Intent().apply {
            data = Uri.fromFile(file)
        })
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(activityResult)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_open)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.markdown_edit)).check(matches(withText(markdownText)))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
        val additionalText = "# More info\n\nThis is some additional text"
        onView(withId(R.id.markdown_edit)).perform(
                clearText(),
                typeText(additionalText)
        )
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.action_save)).perform(click())
        Thread.sleep(500)
        onView(withText(getApplicationContext<Context>().getString(R.string.file_saved, "temp.md")))
        assertEquals(additionalText, file.inputStream().reader().use(Reader::readText))
        onView(withText("temp.md")).check(matches(withParent(withId(R.id.toolbar))))
    }
}
