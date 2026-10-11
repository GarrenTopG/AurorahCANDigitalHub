package com.aurorah.can

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aurorah.can.ui.auth.LoginActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun invalidLogin_DisplaysErrorMessage() {
        onView(withId(R.id.etEmail)).perform(typeText("bademail"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.tilEmail)).check(matches(hasDescendantMatching(withText("Invalid Email Address"))))
    }

    private fun hasDescendantMatching(matcher: Matcher<View>): Matcher<View> {
        return hasDescendant(matcher)
    }
}
