package com.example.freeimagefeed

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.freeimagefeed.feature.image_feed_platform.adapter.UserCardListAdapter
import com.example.freeimagefeed.feature.image_feed_platform.viewholder.UserCardVH
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserListFragmentTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testRecyclerViewAdapter_LoadingState() {
        // Check if the ImageView in the first item of the RecyclerView is displayed.
        onView(withId(R.id.user_rv))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserCardVH>(0, ViewActions.scrollTo()))
        onView(withRecyclerView(R.id.user_rv).atPositionOnView(0, R.id.shimmer_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewAdapter_LoadingStatePagination() {
        // Wait for 4000 milliseconds (4 seconds).
        Thread.sleep(2000)

        // Scroll to the 20th position in the RecyclerView and wait for 500 milliseconds.
        onView(withId(R.id.user_rv)).perform(RecyclerViewActions.scrollToPosition<UserCardVH>(19))
        Thread.sleep(200)

        // Scroll to the 23rd position in the RecyclerView and wait for 500 milliseconds.
        onView(withId(R.id.user_rv)).perform(RecyclerViewActions.scrollToPosition<UserCardVH>(22))
        Thread.sleep(500)

        // Scroll to the 25th position in the RecyclerView.
        onView(withId(R.id.user_rv)).perform(RecyclerViewActions.scrollToPosition<UserCardVH>(28))
        Thread.sleep(500)


        onView(withId(R.id.user_rv)).perform(RecyclerViewActions.scrollToPosition<UserCardVH>(35))
        Thread.sleep(500)
    }

    @Test
    fun testRecyclerViewAdapter() {
        Thread.sleep(4000)

        // Check if the ImageView in the first item of the RecyclerView is displayed.
        onView(withId(R.id.user_rv))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserCardVH>(0, ViewActions.scrollTo()))
        onView(withRecyclerView(R.id.user_rv).atPositionOnView(0, R.id.user_iv))
            .check(matches(isDisplayed()))
    }

    // Helper function to get a RecyclerViewMatcher.
    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }
}

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    idDescription = try {
                        this.resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format("%s (resource name not found)", recyclerViewId)
                    }
                }
                description.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View): Boolean {
                this.resources = view.resources
                if (childView == null) {
                    val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                    childView = if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                    } else {
                        return false
                    }
                }
                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView?.findViewById<View>(targetViewId)
                    view === targetView
                }
            }
        }
    }
}