package com.mad.task1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileItemAdapter
    private lateinit var footerTextView: TextView
    private lateinit var importButton: MaterialButton

    // Define the ActivityResultLauncher for opening the file picker
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.i("TAG", result.toString())
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                readJsonFromUri(uri)
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        footerTextView = findViewById(R.id.footerView)
        importButton = findViewById(R.id.impButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the import button click listener to check for permission and open the file picker
        importButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                openFilePicker()
            }
        }
    }

    // Function to open the file picker
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Filter to only show JSON files
        filePickerLauncher.launch(Intent.createChooser(intent, "Select JSON File"))

    }

    // Read JSON data from the selected file URI
    private fun readJsonFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.use { it.readText() }

            loadAndDisplayData(json)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to read file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Loads JSON data into RecyclerView
    private fun loadAndDisplayData(json: String) {
        if (json.isNotEmpty()) {
            val fileItems = parseJsonData(json)
            adapter = FileItemAdapter(fileItems)
            recyclerView.adapter = adapter

            val (totalItems, totalSize) = calculateTheSize(fileItems)
            footerTextView.text = "Number of Items: $totalItems\nTotal Size: $totalSize"
        } else {
            Toast.makeText(this, "Failed to load JSON file", Toast.LENGTH_SHORT).show()
        }
    }

    // Parses JSON data into a list of FileItem objects
    private fun parseJsonData(json: String): List<FileItem> {
        val gson = Gson()
        val listType = object : TypeToken<Map<String, List<FileItem>>>() {}.type
        val items = gson.fromJson<Map<String, List<FileItem>>>(json, listType)["items"]
        return items ?: emptyList()
    }

    // Calculates the number of items and total size in MB
    private fun calculateTheSize(items: List<FileItem>): Pair<Int, String> {
        val totalItems = items.size
        val totalSize = items.sumOf { it.size?.toIntOrNull() ?: 0 }
        val totalSizeInMB = totalSize / (1024 * 1024)
        return Pair(totalItems, "$totalSizeInMB MB")
    }
}
