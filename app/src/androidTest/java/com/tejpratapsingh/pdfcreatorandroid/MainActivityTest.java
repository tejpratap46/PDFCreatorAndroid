package com.tejpratapsingh.pdfcreatorandroid;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

public class MainActivityTest extends ScreenRobot<MainActivityTest> {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void canOpenMainActivity() {
        withRobot(MainActivityTest.class)
                .checkIsDisplayed(R.id.layoutGeneratePdf, R.id.layoutGenerateHtmlPdf, R.id.layoutEditHtmlPdf);
    }

    @Test
    public void canCreatePdf() {
        ViewInteraction linearLayout = onView(
                withId(R.id.layoutGeneratePdf));
        linearLayout.perform(click());

        ViewInteraction textView = onView(withId(R.id.textViewPreviewPageNumber));
        textView.check(matches(withText("1 OF 2")));

        ViewInteraction imageButton = onView(withId(R.id.buttonNextPage));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                withId(R.id.buttonSendEmail));
        appCompatButton.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewPdfViewerPageNumber), withText("1 OF 2"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("1 OF 2")));
    }

    @Test
    public void canCreateHtmlPdf() {
        ViewInteraction linearLayout2 = onView(withId(R.id.layoutGenerateHtmlPdf));
        linearLayout2.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textViewPdfViewerPageNumber), withText("1 OF 1"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        textView3.check(matches(withText("1 OF 1")));
    }

    @Test
    public void canCreateWebViewPdf() {
        ViewInteraction linearLayout3 = onView(withId(R.id.layoutEditHtmlPdf));
        linearLayout3.perform(click());

        ViewInteraction actionMenuItemView = onView(anyOf(withId(R.id.menuPrintPdf)));
        actionMenuItemView.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.textViewPdfViewerPageNumber), withText("1 OF 1"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        textView4.check(matches(withText("1 OF 1")));
    }
}
