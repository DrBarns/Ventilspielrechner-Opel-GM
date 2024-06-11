package com.example.xmlserial

import android.content.Context
import nl.adaptivity.xmlutil.serialization.XML
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Paths


/**
 * Loads xml data into a [MutableList]
 * NOTE: The type T must be annotated with @Serializable and should resemble
 *       the XML structure to load.
 * NOTE ALSO: This function must be reified, because T needs to be known
 * @param file [File] The initialized file object pointing to XML file to load.
 * @return [MutableList] of type [T] filled with XML data, given in [file]
 */
inline fun <reified T> loadXMLData(file: File): MutableList<T> {
    val xmlData: String = file.readText()
    return XML.decodeFromString<MutableList<T>>(xmlData)
}

/**
 * Loads the text resource given in [resourceId] as string.
 *
 * This function is for cases when you want to load the entire text data, instead of having portions
 * of it, eg. when using build in resource functions, returning the data only, but not the data
 * structure all together.
 *
 * NOTE: The resource must be in folder "res/raw" in order to load the text correctly, or at least
 *       not in the "res/values" folder, depending on the merit of google.
 * @param context The [Context] object. It can be made available via Application if no other context
 *                is accessible. To have access to Application simply create a class and derive it
 *                from Application using singleton pattern. This [Application] class will be
 *                miraculously instantiated, and can be used here.
 * @param resourceId The resource id automatically generated for the xml file to load, in case
 *                the file is "test_file.xml" and be under "res/raw" the id is
 *                stored in "R.raw.test_file"
 * @return [String] The complete xml file als text
 */
fun loadTextFromResource(context: Context, resourceId: Int):String {
    val inputStream = context.resources.openRawResource(resourceId)
    val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
    val stringBuilder = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        stringBuilder.append(line)
    }
    inputStream.close()
    return stringBuilder.toString()
}

/**
 * Loads and deserialize the XML resource given in [resourceId] as MutableList of type T.
 *
 * This function uses the serializer mechanism to load xml data directly in to objects.
 *
 * The underlying XML data must have the following structure:
 * <code>
 *   <?xml version="1.0" encoding="utf-8"?>
 *    <persons>
 *       <person>
 *         <name></name>
 *         <age></age>
 *         <gender></gender>
 *      </person>
 *      <person>
 *         <name></name>
 *         <age></age>
 *         <gender></gender>
 *      </person>
 *    </persons>
 * </code>
 *
 * This xml structure requires a serialized data class:
 *
 * <code>
 * @Serialize
 * data class SomeClass(
 *      @XmlElement(true) val name : String,
 *      @XmlElement(true) val age  : Int,
 *      @XmlElement(true) val gender : String
 * )
 * </code>
 *
 * After loading the XML file the number of person elements in XML file given are equal the
 * number of list objects returned.
 *
 * NOTE: The resource must be in folder "res/raw" in order to load the text correctly, or at least
 *       not in the "res/values" folder, depending on the merit of google.
 * @param context The [Context] object. It can be made available via Application if no other context
 *                is accessible. To have access to Application simply create a class and derive it
 *                from Application using singleton pattern. This [Application] class will be
 *                miraculously instantiated indeed, and can be used here.
 * @param resourceId The resource id automatically generated for the xml file to load, in case
 *                the file is "test_file.xml" and be under "res/raw" the id is
 *                stored in "R.raw.test_file"
 * @return [MutableList] of type [T] All serialized objects in given XML file
 */
inline fun <reified T>loadXMLFromResource(context: Context, resourceId: Int):MutableList<T> {
    return XML.decodeFromString<MutableList<T>>(loadTextFromResource(context,resourceId))
}

/**
 * This function loads XML elements as string, together with the XML itself.
 *
 * The underlying XML data must have the following structure, note the utf-8 encoding too:
 * <code>
 *   <?xml version="1.0" encoding="utf-8"?>
 *    <persons>
 *       <person>
 *         <name></name>
 *         <age></age>
 *         <gender></gender>
 *      </person>
 *      <person>
 *         <name></name>
 *         <age></age>
 *         <gender></gender>
 *      </person>
 *    </persons>
 * </code>
 *
 *
 * @param context The [Context] object. It can be made available via Application if no other context
 *                is accessible. To have access to Application simply create a class and derive it
 *                from Application using singleton pattern. This [Application] class will be
 *                miraculously instantiated indeed, and can be used here.
 * @param resourceId The resource id automatically generated for the xml file to load, in case
 *                the file is "test_file.xml" and be under "res/raw" the id is
 *                stored in "R.raw.test_file"
 * @param elementName The element to collect.
 * @return [String] The entire "persons" element and sub-elements as string, if [elementName] is
 *                "persons".
 */
fun loadXmlElementsAsString(context: Context, resourceId: Int, elementName: String): String {

    val xmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
    val inputStream = context.resources.openRawResource(resourceId)
    xmlPullParser.setInput(inputStream, "UTF-8")

    val stringBuilder = StringBuilder()

    var eventType = xmlPullParser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && xmlPullParser.name == elementName) {
            val personXml = readXMLElementAsString(xmlPullParser, elementName)
            stringBuilder.append(personXml).append("\n")
        }
        eventType = xmlPullParser.next()
    }
    inputStream.close()
    return stringBuilder.toString()
}

/**
 * Used by loadXmlElementsAsString(context: Context, resourceId: Int, elementName: String), to
 * fetch string data form xml file.
 *
 * @param xmlPullParser Initialized and open xmlPullParser (from XmlPullParserFactory), pointing
 *        to the
 * @return [string] The the elements fetched by [elementName]
 */
private fun readXMLElementAsString(xmlPullParser: XmlPullParser, elementName: String): String {
    val stringBuilder = StringBuilder()

    while (!(xmlPullParser.eventType == XmlPullParser.END_TAG && xmlPullParser.name == elementName)) {
        when (xmlPullParser.eventType) {
            XmlPullParser.START_TAG -> {
                stringBuilder.append("<${xmlPullParser.name}>")
            }
            XmlPullParser.TEXT -> {
                stringBuilder.append(xmlPullParser.text)
            }
            XmlPullParser.END_TAG -> {
                stringBuilder.append("</${xmlPullParser.name}>")
            }
        }
        xmlPullParser.next()
    }
    return stringBuilder.toString()
}


/**
 * Returns the resource directory, this is only valid for local tests, that
 * is not for instrumentation testing.
 *
 * @param append [String] The path to append to the resource directory.
 *        NOTE: The path in append must be a valid file path
 * @return The full file path to the resource directory for this application,
 *         usually in "...src/main/res"
 * @throws FileNotFoundException If the path in [append] is not found.
 */
fun getResourceDirectory(append: String = ""): String {

    val currentDirectory = System.getProperty("user.dir")
    val mainResourcePath = "/src/main/res"
    val mainResourcePaths =
        listOf(".$mainResourcePath", mainResourcePath, "$currentDirectory$mainResourcePath")

    for (path in mainResourcePaths) {
        val directory = File(path)
        if (directory.exists() && directory.isDirectory) {
            return Paths.get(path, append).toString()
        }
    }
    val message = "Can't find the resource directory in ($mainResourcePath), or ($currentDirectory$mainResourcePath, using append ($append) !"
    // println("Cant find the resource directory in ($mainResourcePath), or ($currentDirectory$mainResourcePath)")

    throw FileNotFoundException(message)
}






