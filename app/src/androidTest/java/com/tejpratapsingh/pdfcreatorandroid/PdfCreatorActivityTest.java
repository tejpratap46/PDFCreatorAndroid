package com.tejpratapsingh.pdfcreatorandroid;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;


public class PdfCreatorActivityTest extends ScreenRobot<PdfCreatorActivityTest> {

    @Rule
    public ActivityScenarioRule<PdfCreatorActivity> activityRule =
            new ActivityScenarioRule<>( PdfCreatorActivity.class );

    @Test
    public void canGeneratePdf() {

    }
}
