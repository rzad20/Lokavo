package com.lokavo.ui.result

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.android.gms.maps.model.LatLng
import com.lokavo.R
import com.lokavo.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(ResultActivity::class.java, true, false)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testResultDisplay() {
        val intent = Intent().apply {
            putExtra(ResultActivity.LOCATION, LatLng(-7.5573536, 110.7943764)) // Example coordinates
        }
        activityRule.launchActivity(intent)
        onView(withId(R.id.txtSentimentCategory))
            .check(matches(withText("highly competitive")))
    }
}
