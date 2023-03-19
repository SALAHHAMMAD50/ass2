package com.salahprom.ass2

import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.salahprom.ass2.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var reqCode: Int = 100
    lateinit var pdfFile: File
    val storage = Firebase.storage
    val storageRef = storage.reference
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        binding.btDownloadFile.setOnClickListener {
            val folder = File(applicationContext.filesDir, "Ass2")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            showProgressDialog()

            val storageRef = Firebase.storage.reference
                .child("PDF")
            storageRef.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    val file = File(folder, item.name)
                    item.getFile(file).addOnSuccessListener {
                        // File downloaded successfully
                        hideProfressDialog()
                        Toast.makeText(applicationContext, "Download and save Success", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener { exception ->
                        // Handle download failure
                        hideProfressDialog()
                        Toast.makeText(applicationContext, "Save fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle listAll() failure
                hideProfressDialog()
                Toast.makeText(applicationContext, "Download fail", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"

            startActivityForResult(intent, reqCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == reqCode && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data!!.data!!
            showProgressDialog()
            storageRef.child("PDF").child(UUID.randomUUID().toString())
                .putFile(uri)

                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_SHORT).show()
                    hideProfressDialog()
                }.addOnFailureListener {
                    hideProfressDialog()
                    Toast.makeText(applicationContext, "Upload fail", Toast.LENGTH_SHORT).show()

                }
        }
    }

    fun showProgressDialog() {
        progressDialog.show()
    }

    fun hideProfressDialog() {
        progressDialog.dismiss()
    }
}