package com.lokavo.ui.detailAnalysis

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.android.gms.maps.model.LatLng
import com.lokavo.R
import com.lokavo.data.remote.response.ClusterProportion
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailAnalysisActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(DetailAnalysisActivity::class.java, true, false)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testDetailAnalysisDisplay() {
        val result = ModelingResultsResponse(
            summaryHeader = "highly competitive",
            longInterpretation = "area ini tingkat kompetitifnya cukup tinggi",
            clusterProportion = ClusterProportion(a = 40, b = 30, c = 30),
            latLng = LatLng(-7.5573536, 110.7943764),
            top = listOf()
        )

        val intent = Intent().apply {
            putExtra(DetailAnalysisActivity.RESULT, result)
        }
        activityRule.launchActivity(intent)

        onView(withId(R.id.txtSentimentCategory))
            .check(matches(withText("highly competitive")))

        onView(withId(R.id.detailAnalysis))
            .check(matches(withText("area ini tingkat kompetitifnya cukup tinggi")))
        onView(withId(R.id.any_chart_view)).check(matches(isDisplayed()))
    }
}
