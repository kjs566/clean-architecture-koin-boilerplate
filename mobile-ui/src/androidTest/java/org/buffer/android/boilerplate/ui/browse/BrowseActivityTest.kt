package org.buffer.android.boilerplate.ui.browse

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.buffer.android.boilerplate.data.browse.Bufferoo
import org.buffer.android.boilerplate.data.repository.BufferooRepository
import org.buffer.android.boilerplate.ui.R
import org.buffer.android.boilerplate.ui.di.applicationModule
import org.buffer.android.boilerplate.ui.di.browseModule
import org.buffer.android.boilerplate.ui.test.util.BufferooFactory
import org.buffer.android.boilerplate.ui.test.util.RecyclerViewMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock

@RunWith(AndroidJUnit4::class)
class BrowseActivityTest: KoinTest {

    val mockBufferooRepository: BufferooRepository by inject()

    @Rule @JvmField
    val activity = ActivityTestRule<BrowseActivity>(BrowseActivity::class.java, false, false)

    @Before
    fun setUp() {
        loadKoinModules(applicationModule, browseModule)
        declareMock<BufferooRepository>()
    }

    @Test
    fun activityLaunches() {
        stubBufferooRepositoryGetBufferoos(Flowable.just(BufferooFactory.makeBufferooList(2)))
        activity.launchActivity(null)
    }

    @Test
    fun bufferoosDisplay() {
        val bufferoos = BufferooFactory.makeBufferooList(1)
        stubBufferooRepositoryGetBufferoos(Flowable.just(bufferoos))
        activity.launchActivity(null)

        checkBufferooDetailsDisplay(bufferoos[0], 0)
    }

    @Test
    fun bufferoosAreScrollable() {
        val bufferoos = BufferooFactory.makeBufferooList(20)
        stubBufferooRepositoryGetBufferoos(Flowable.just(bufferoos))
        activity.launchActivity(null)

        bufferoos.forEachIndexed { index, bufferoo ->
            onView(withId(R.id.recycler_browse)).perform(RecyclerViewActions.
                    scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(index))
            checkBufferooDetailsDisplay(bufferoo, index) }
    }

    private fun checkBufferooDetailsDisplay(bufferoo: Bufferoo, position: Int) {
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_browse).atPosition(position))
                .check(matches(hasDescendant(withText(bufferoo.name))))
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_browse).atPosition(position))
                .check(matches(hasDescendant(withText(bufferoo.title))))
    }

    private fun stubBufferooRepositoryGetBufferoos(single: Flowable<List<Bufferoo>>) {
        whenever(mockBufferooRepository.getBufferoos())
                .thenReturn(single)
    }

}