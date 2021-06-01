package org.dhis2.common.filters

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.dhis2.R
import org.dhis2.common.BaseRobot
import org.dhis2.common.matchers.DatePickerMatchers.Companion.matchesDate
import org.dhis2.common.matchers.RecyclerviewMatchers.Companion.hasItem
import org.dhis2.usescases.orgunitselector.OrgUnitSelectorHolder
import org.dhis2.utils.filters.FilterHolder
import org.hamcrest.Matchers.allOf

fun filterRobotCommon(robotBody: FiltersRobot.() -> Unit) {
    FiltersRobot().apply {
        robotBody()
    }
}

class FiltersRobot : BaseRobot() {
    fun openFilterAtPosition(position: Int) {
        onView(withId(R.id.filterRecycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<FilterHolder>(position, click())
        )
    }
    
    fun clickOnFromToDateOption() {
        onView(withId(R.id.fromTo)).perform(click())
    }

    fun clickOnOrgUnitTree() {
        onView(withId(R.id.ouTreeButton)).perform(click())
    }

    fun returnToSearch() {
        onView(withId(R.id.menu)).perform(click())
    }

    fun selectDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        onView(withId(R.id.widget_datepicker)).perform(
            PickerActions.setDate(year, monthOfYear, dayOfMonth)
        )
    }

    fun typeOrgUnit(orgUnitName: String) {
        onView(withId(R.id.orgUnitSearchEditText)).perform(TypeTextAction(orgUnitName))
    }

    fun clickAddOrgUnit() {
        onView(withId(R.id.addButton)).perform(click())
    }

    fun selectTreeOrgUnit(orgUnitName: String) {
        onView(withId(R.id.orgUnitRecycler))
            .perform(RecyclerViewActions.actionOnItemAtPosition<OrgUnitSelectorHolder>(0, click()))
        onView(allOf(withId(R.id.checkBox), ViewMatchers.hasSibling(withText(orgUnitName))))
            .perform(click())
    }

    fun selectNotSyncedState() {
        onView(withId(R.id.stateNotSynced)).perform(click())
    }

    fun acceptDateSelected(){
        onView(withId(R.id.acceptButton)).perform(click())
    }

    fun checkDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        onView(withId(R.id.widget_datepicker)).check(matches(matchesDate(year, monthOfYear, dayOfMonth)))
    }


}