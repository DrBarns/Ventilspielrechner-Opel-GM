package com.example.ventilrechneropel.model

import com.example.xmlserial.getResourceDirectory
import com.example.xmlserial.loadXMLData
import org.junit.Test
import java.io.File

class LibViewModelTest {

    @Test
    fun getResourceDirectoryTest() {
        val fullPath: String = getResourceDirectory(ValveConfig.FILE_PATH_KZVALUE_XML)
        val file : File = File(fullPath)
        assert(file.exists()) { println("[Filepath used: $fullPath]") }
        assert(fullPath.contains(ValveConfig.FILE_PATH_KZVALUE_XML)){ println("[Filepath used: $fullPath]") }
    }

    @Test
    fun loadXMLTest() {
        val filePath = getResourceDirectory(ValveConfig.FILE_PATH_KZVALUE_XML)
        val file = File(filePath)
        assert(file.isFile) { println("The path to xml file is not valid [$filePath]") }
        assert(file.readText().isNotEmpty()) { println("The xml file is empty [$filePath]") }
        val kzValueList: MutableList<KZValue> = loadXMLData(file)
        println(kzValueList.toString())
        assert(kzValueList.isNotEmpty())
    }

    @Test
    fun loadKZValuesTest() {
        val kzValueList: MutableList<KZValue> = loadKZValues(ValveConfig.FILE_PATH_KZVALUE_XML)
        assert(kzValueList.isNotEmpty())
    }

    @Test
    fun lookupKZTest() {
        val kzValue = lookupKZ(loadKZValues(ValveConfig.FILE_PATH_KZVALUE_XML), 3.15f)
        assert(kzValue.kz == "KZ 16")
    }

    @Test
    fun calcNewHeightTest() {
        val oldH = 3.25f
        val oldX = 0.20f
        val newX = 0.26f
        val testResult = calcNewHeight(oldH, oldX, newX)
        assert(testResult + newX == oldH + oldX) {println("Result not plausable!")}
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
        assert(testResult + newX == kzValue.ds + oldX) {println("Result not plausable!")}
    }


}