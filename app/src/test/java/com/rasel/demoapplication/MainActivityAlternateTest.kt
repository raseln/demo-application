package com.rasel.demoapplication

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.rasel.demoapplication.ui.FullImageActivity
import com.rasel.demoapplication.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainActivityAlternateTest {

//    @Test
//    fun itemWithText_doesNotExist() {
//        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
//        Thread.sleep(3000)
//        Robolectric.flushForegroundThreadScheduler()
////        assertTrue(activity.findViewById<RecyclerView>(R.id.recycler_view).childCount, 20)
////        assertEquals(activity.findViewById<RecyclerView>(R.id.recycler_view).childCount, 20)
//        println("Count: " +activity.findViewById<RecyclerView>(R.id.recycler_view).childCount)
//    }
//
//    @Test
//    fun isFullImageActivityShown() {
//        val intent = Intent(ApplicationProvider.getApplicationContext(), FullImageActivity::class.java)
//        intent.putExtra(FullImageActivity.PHOTO_ID, 15286)
////        val activity = Robolectric.buildActivity(FullImageActivity::class.java, intent).create().get()
////        Robolectric.flushForegroundThreadScheduler()
////        activity.findViewById<ImageView>(R.id.image_view)
////        activity.create()
//    }

    @Test
    fun shouldStartFullImageActivity() {
        val mainActivity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        val expectedIntent = Intent(ApplicationProvider.getApplicationContext(), FullImageActivity::class.java)
        expectedIntent.putExtra(FullImageActivity.PHOTO_ID, 0)
        mainActivity.startActivity(expectedIntent)

        val actual = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(expectedIntent.component, actual.component)
    }
}