package com.tejpratapsingh.pdfcreatorandroid;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.app.Activity;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

// URL: https://gist.github.com/adavis/f35c12bdafbf2e20f66485235f9d423f

public abstract class ScreenRobot<T extends ScreenRobot> {
    private Activity activityContext; // Only required for some calls

    public static <T extends ScreenRobot> T withRobot(Class<T> screenRobotClass) {
        if (screenRobotClass == null) {
            throw new IllegalArgumentException("instance class == null");
        }

        try {
            return screenRobotClass.newInstance();
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException", iae);
        } catch (InstantiationException ie) {
            throw new RuntimeException("InstantiationException", ie);
        }
    }

    public T checkIsDisplayed(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            onView(withId(viewId)).check(matches(isDisplayed()));
        }
        return (T) this;
    }

    public T checkIsHidden(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            onView(withId(viewId)).check(matches(not(isDisplayed())));
        }
        return (T) this;
    }

    public T checkViewHasText(@IdRes int viewId, String expected) {
        onView(withId(viewId)).check(matches(withText(expected)));
        return (T) this;
    }

    public T checkViewHasText(@IdRes int viewId, @StringRes int messageResId) {
        onView(withId(viewId)).check(matches(withText(messageResId)));
        return (T) this;
    }

    public T checkViewHasHint(@IdRes int viewId, @StringRes int messageResId) {
        onView(withId(viewId)).check(matches(withHint(messageResId)));
        return (T) this;
    }

    public T clickOkOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(click());
        return (T) this;
    }

    public T enterTextIntoView(@IdRes int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
        return (T) this;
    }

    public T provideActivityContext(Activity activityContext) {
        this.activityContext = activityContext;
        return (T) this;
    }

    public T checkDialogWithTextIsDisplayed(@StringRes int messageResId) {
        onView(withText(messageResId))
                .inRoot(withDecorView(not(activityContext.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        return (T) this;
    }

    public T swipeLeftOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(swipeLeft());
        return (T) this;
    }

    public T goBack() {
        pressBack();
        return (T) this;
    }
}
