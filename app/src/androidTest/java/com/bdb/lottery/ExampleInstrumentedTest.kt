package com.bdb.lottery

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bdb.lottery.utils.Apps
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.bdb.bdb", appContext.packageName)
        val app = Apps.getApp()
        println(app)
    }
}