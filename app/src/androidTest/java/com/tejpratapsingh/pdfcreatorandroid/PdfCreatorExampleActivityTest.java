package com.tejpratapsingh.pdfcreatorandroid;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;


public class PdfCreatorExampleActivityTest extends ScreenRobot<PdfCreatorExampleActivityTest> {

    @Rule
    public ActivityScenarioRule<PdfCreatorExampleActivity> activityRule =
            new ActivityScenarioRule<>(PdfCreatorExampleActivity.class);

    @Test
    public void canGeneratePdf() {

    }
}
