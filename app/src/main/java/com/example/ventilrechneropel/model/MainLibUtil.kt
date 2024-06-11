package com.example.ventilrechneropel.model

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ventilrechneropel.MyApp
import com.example.ventilrechneropel.R
import com.example.xmlserial.getResourceDirectory
import com.example.xmlserial.loadTextFromResource
import com.example.xmlserial.loadXMLData
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import java.io.File
import java.security.SecureRandom
import java.util.UUID


@Serializable
data class KZValue (
    @XmlElement(true) val oem: String,
    @XmlElement(true) val opel: String,
    @XmlElement(true) val bez: String,
    @XmlElement(true) val ds: Float,
    @XmlElement(true) val de: Float,
    @XmlElement(true) val kz: String
)

/**
 * Extension function, checks if the float value given is in  [min] [max] range
 * @param min [Float] Minimum number (included)
 * @param max [Float] Maximum number (included)
 */
private fun Float.isInRange(min: Float, max: Float): Boolean {
    return this in min..max
}

/**
 * Loads the KZ values from XML resource file given in [filePath]
 * @param filePath [String] File path (only resource part + file.name),
 *        that is filesystem under .../res/
 * @return [MutableList] of [KZValue] containing all KZ values
 */
fun loadKZValues(filePath: String): MutableList<KZValue> {
    val file: File = File(getResourceDirectory(filePath))
    return loadXMLData(file)
}

/**
 * Loads the KZ values from XML resource directory
 * @param context [Context] The context to load from
 *
 * @return [MutableList] of [KZValue] containing all KZ values
 */
fun loadKZValues(context: Context): MutableList<KZValue> {
    return XML.decodeFromString(loadTextFromResource(context, R.raw.kzvalues))
}

/**
 * Looks the KZ value for the given height in h. If the exact KZ value cant be found
 * the next lower KZ value will be returned.
 *
 * @param kzValues [MutableList<KZValue>]: This is the list of [KZValue] objects,
 *        eg. loaded with [loadKZValues()]
 * @param  [x]: This value is the calculated valve height
 * @return [KZValue]: The KZ value for parameter h
 */
fun lookupKZ(kzValues: MutableList<KZValue>, x: Float): KZValue {

    // if x is already below the first kz value possible then return the first kzValue
    // no need to calculate further
    if (x < kzValues[0].ds) return kzValues[0]

    var foundKZValue: KZValue? = null

    // find the best match possible
    for (kzValue in kzValues) {
        if (x.isInRange(kzValue.ds, kzValue.de)) {
            foundKZValue =  kzValue
            break
        }
    }

    // if the best match failed, use next lower KZ
    var lastKZValue: KZValue = kzValues[0]

    if (foundKZValue == null) {
        for (kzValue in kzValues) {
            // h is between first nearest KZ values, regardless being in a KZ range or not
            if (x.isInRange(lastKZValue.ds, kzValue.ds)) {
                foundKZValue = lastKZValue
                break
            }
            lastKZValue = kzValue
        }
    }

    // if no KZ value for h is found, than lastKZValue.
    foundKZValue = foundKZValue ?: lastKZValue

    // return the foundKZValue
    return foundKZValue
}

fun calcNewHeight(oldH: Float, oldX: Float, newX: Float): Float {
    var result: Float = 0f
    result = oldH + oldX - newX
    return result
}

fun calcNewHeight(oldH: KZValue, oldX: Float, newX: Float): Float {
    var result: Float = 0f
    result = calcNewHeight(oldH.ds, oldX, newX)
    return result
}

fun Float.formatString(decimalPlaces: Int): String {
    return String.format("%.${decimalPlaces}f", this)
}

fun String.safeStringToFloat(): Float? {
    return this.toFloatOrNull()
}

fun getResourceColor(id: Int): Color {
    return Color(ContextCompat.getColor(MyApp.getContext(),id))
}

fun <T> MutableList<T>.push(element: T) {
    add(element)
}

fun <T> MutableList<T>.pop(): T? {
    return if (isEmpty()) {
        null
    } else {
        removeAt(size - 1)
    }
}

fun getResourceString(id:Int): String {
    return MyApp.getContext().getString(id)
}

@Composable
fun horzLine() {
    Divider(
        color = Color.Blue,
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )
}

fun generateUUIDFileName(extension: String = ""): String {
    val uuid = UUID.randomUUID().toString()
    return if (extension.isNotEmpty()) {
        "$uuid.$extension"
    } else {
        uuid
    }
}

fun generateRandomFileName(length: Int = 12, extension: String = ""): String {
    val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val randomString = (1..length)
        .map { allowedChars.random() }
        .joinToString("")
    return if (extension.isNotEmpty()) {
        "$randomString.$extension"
    } else {
        randomString
    }
}

fun generateSecureFileName(length: Int = 12, extension: String = ""): String {
    val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val secureRandom = SecureRandom()
    val randomString = (1..length)
        .map { allowedChars[secureRandom.nextInt(allowedChars.length)] }
        .joinToString("")
    return if (extension.isNotEmpty()) {
        "$randomString.$extension"
    } else {
        randomString
    }
}

