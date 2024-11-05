package com.mad.task1

data class FileItem (
    val type: String, // Type of the item (Folder, file)
    val name: String, // Name of the file or folder
    val parent: String, // Parent directory path
    val size: String? // Size of the file in bytes (null or empty for folders)

){
    fun getPath(): String = "$parent/$name"


    fun getSizeOrDefault(): String = size ?: "0"
}