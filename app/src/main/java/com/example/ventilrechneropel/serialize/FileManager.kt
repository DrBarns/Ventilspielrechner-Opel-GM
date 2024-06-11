package com.example.ventilrechneropel.serialize

import com.example.ventilrechneropel.MyApp
import com.example.ventilrechneropel.model.generateRandomFileName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class FileEntry(
    val filePath: String,
    val name: String,
    val userName: String
)

class FileManager {

    companion object {
        private var listFileEntries: MutableList<FileEntry> = mutableListOf()

        fun initializeManager() {
            loadFileList()
        }

        private fun loadFileList() {
            var file = File(MyApp.getContext().filesDir, "file_list.json")
            // if nothing to load then return
            if (!file.exists()) file.createNewFile()
            var jsonString = file.readText()
            var json = Json { prettyPrint = true }
            if (jsonString.isNotEmpty()) {
                listFileEntries = json.decodeFromString<MutableList<FileEntry>>(jsonString)
            }
        }

        private fun safeFileList() {
            var file = File(MyApp.getContext().filesDir, "file_list.json")
            if (!file.exists()) file.createNewFile()
            var json = Json { prettyPrint = true }
            file.writeText(json.encodeToString<MutableList<FileEntry>>(listFileEntries))
        }

        fun getFileNameFromName(name: String, action: FileAction, user: String = ""): String {
            var fileEntry: FileEntry = getNewFileEntry(name, user)

            if (action == FileAction.Save) {
                if (listFileEntries.isEmpty()) {
                    fileEntry = getNewFileEntry(name, user)
                    saveEntryToList(fileEntry)
                } else {
                    fileEntry = getFileEntryFromList(name, user)
                }
            }

            if (action == FileAction.Load) {
                if (listFileEntries.isNotEmpty()) {
                    fileEntry = getFileEntryFromList(name, user)
                } else {
                    fileEntry = getNewFileEntry(name, user)
                }
            }
            return fileEntry.filePath
        }

        private fun saveEntryToList(fileEntry: FileEntry) {
            listFileEntries.add(fileEntry)
            safeFileList()
        }



        private fun getNewFileEntry(name: String, user: String): FileEntry {
            return FileEntry(
                filePath = generateNewFilePath(),
                name = name,
                userName = user
            )
        }

        private fun getFileEntryFromList(name: String, user: String): FileEntry {
            var fileEntryResult : FileEntry = getNewFileEntry(name, user)
            listFileEntries.forEach() { entry ->
                if (entry.name == name && entry.userName == user) {
                    // entry exists already
                    fileEntryResult = entry
                }
            }
            return fileEntryResult
        }

        private fun generateNewFilePath(): String {
            return generateRandomFileName(12, "json")
        }

        fun getFileList() : MutableList<String> {
            val list = mutableListOf<String>()
            listFileEntries.forEach() { entry ->
                list.add(entry.name)
            }
            return list
        }
    }

}
