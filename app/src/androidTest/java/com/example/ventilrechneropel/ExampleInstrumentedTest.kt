package com.example.ventilrechneropel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ventilrechneropel.MyApp.Companion.hasAppContext
import com.example.ventilrechneropel.model.KZValue
import com.example.ventilrechneropel.model.calcNewHeight
import com.example.ventilrechneropel.model.loadKZValues
import com.example.ventilrechneropel.model.lookupKZ
import com.example.xmlserial.loadTextFromResource
import com.example.xmlserial.loadXMLFromResource
import org.junit.Assert.assertEquals
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
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.ventilrechneropel", appContext.packageName)
    }

    @Test
    fun hasAppContextTest() {
        assert(hasAppContext()) {"We don't have app context! Test results not valid."}
    }

    @Test
    fun loadXMLStringTest() {
        val STRING_LENGHT_TO_TEST = 10

        var result: String = loadTextFromResource(MyApp.getContext(), R.raw.kzvalues)
        assert(result.isNotEmpty() && result.length > STRING_LENGHT_TO_TEST){"Test succeeded"}
    }

    @Test
    fun loadXMLDataTest() {
        val kzValueList: MutableList<KZValue> = loadXMLFromResource(MyApp.getContext(), R.raw.kzvalues)
        println(kzValueList.toString())
        assert(kzValueList.isNotEmpty())
    }

    @Test
    fun loadKZValuesTest() {
        val kzValueList: MutableList<KZValue> = loadKZValues(MyApp.getContext())
        assert(kzValueList.isNotEmpty())
    }

    @Test
    fun lookupKZTest() {
        val kzValue = lookupKZ(loadKZValues(MyApp.getContext()), 3.15f)
        assert(kzValue.kz == "KZ 16")
    }

    @Test
    fun calcNewHeightTest() {
        val oldH = 3.25f
        val oldX = 0.20f
        val newX = 0.26f
        val testResult = calcNewHeight(oldH, oldX, newX)
        assert(testResult + newX == oldH + oldX) {println("Result not plausible!")}
    }

    @Test
    fun testCalcNewHeightKZTest() {
        val kzValue = KZValue(
            "",
            "",
            "",
            3.25f,
            3.30f,
            "KZTEST"
        )
        val oldX = 0.20f
        val newX = 0.30f
        val testResult = calcNewHeight(kzValue, oldX, newX)
        assert(testResult + newX == kzValue.ds + oldX) {println("Result not plausible!")}
    }
}