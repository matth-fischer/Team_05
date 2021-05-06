package at.tugraz05.slimcat

import android.view.View
import android.widget.SeekBar
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import junit.framework.TestCase
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddcatActivityTest : TestCase() {
    private val txtids = arrayOf(R.id.txt_name, R.id.txt_race, R.id.txt_size, R.id.txt_weight)
    private val switchids = arrayOf(
            R.id.switch_obese, R.id.switch_overweight,
            R.id.switch_hospitalized, R.id.switch_neutered,
            R.id.switch_gestation, R.id.switch_lactation,
    )
    private val ids = arrayOf(R.id.seek_gender, R.id.btn_camera) + txtids + switchids
    private val rowids = arrayOf(R.id.row_gestation, R.id.row_lactation)

    @Test
    fun hasAllInputFields() {
        ActivityScenario.launch(AddcatActivity::class.java)
        ids.forEach { onView(withId(it)) }
    }

    @Test
    fun inputFieldsEditable() {
        ActivityScenario.launch(AddcatActivity::class.java)
        (txtids + switchids).forEach { onView(withId(it)).check(matches(isClickable())) }
    }

    @Test
    fun maleLabelClickHideFemale() {
        ActivityScenario.launch(AddcatActivity::class.java)
        onView(withId(R.id.label_gender_female)).perform(scrollTo()).perform(click())
        onView(withId(R.id.label_gender_male)).perform(scrollTo()).perform(click())
        rowids.forEach { id -> onView(withId(id)).perform(waitFor<View> { it.visibility == View.GONE }) }
    }

    @Test
    fun maleSeekerChangeHideFemale() {
        ActivityScenario.launch(AddcatActivity::class.java)
        onView(withId(R.id.seek_gender)).perform(scrollTo()).perform(callMethod<SeekBar> { it.progress = GenderSeeker.FEMALE })
        onView(withId(R.id.seek_gender)).perform(scrollTo()).perform(callMethod<SeekBar> { it.progress = GenderSeeker.MALE })
        rowids.forEach { id -> onView(withId(id)).perform(waitFor<View> { it.visibility == View.GONE }) }
    }

    @Test
    fun clickingGenderLabelsChangesSeeker() {
        ActivityScenario.launch(AddcatActivity::class.java)
        onView(withId(R.id.label_gender_female)).perform(scrollTo()).perform(click())
        onView(withId(R.id.label_gender_male)).perform(scrollTo()).perform(click())
        onView(withId(R.id.seek_gender)).perform(waitFor<SeekBar> { it.progress == GenderSeeker.MALE })
        onView(withId(R.id.label_gender_female)).perform(scrollTo()).perform(click())
        onView(withId(R.id.seek_gender)).perform(waitFor<SeekBar> { it.progress == GenderSeeker.FEMALE })
    }

    @Test
    fun btnDatepickerIsClickable() {
        ActivityScenario.launch(AddcatActivity::class.java)
        onView(withId(R.id.btn_dob)).perform(scrollTo()).perform(click())
    }

    @Test
    fun btnDatepickerIsDisplayed() {
        ActivityScenario.launch(AddcatActivity::class.java)
        onView(withId(R.id.btn_dob)).check(matches(isDisplayed()))
    }


}