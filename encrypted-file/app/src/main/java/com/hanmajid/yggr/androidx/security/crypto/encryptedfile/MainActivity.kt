package com.hanmajid.yggr.androidx.security.crypto.encryptedfile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    //    private val textFilename = "saved_text.txt"
    private val imageFilename = "saved_image.jpeg"
    private val unencryptedImageFilename = "unencrypted_saved_image.jpeg"
    private lateinit var masterKey: MasterKey
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MasterKey
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        masterKey = MasterKey.Builder(applicationContext)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()

        // Setup image picker launcher
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                imageUri?.let {
                    saveEncryptedImage(imageUri)
                    saveUnencryptedImage(imageUri)
                }
            }

//        findViewById<Button>(R.id.button_save).setOnClickListener {
//            val file = File(filesDir, textFilename)
//
//            if (file.exists()) {
//                file.delete()
//            }
//
//            val encryptedFile = EncryptedFile.Builder(
//                applicationContext,
//                file,
//                masterKey,
//                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//            ).build()
//
//            val inputText = findViewById<TextInputEditText>(R.id.input_text).text.toString()
//            Log.wtf("INPUT_TEXT", inputText)
//            val fileContent = inputText
//                .toByteArray(StandardCharsets.UTF_8)
//
//            encryptedFile.openFileOutput().apply {
//                write(fileContent)
//                flush()
//                close()
//            }
//        }
//        findViewById<Button>(R.id.button_read_from_file).setOnClickListener {
//            val file = File(filesDir, textFilename)
//
//            if (file.exists()) {
//                val encryptedFile = EncryptedFile.Builder(
//                    applicationContext,
//                    file,
//                    masterKey,
//                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//                ).build()
//
//                val inputStream = encryptedFile.openFileInput()
//
//                var plaintext = ""
//                inputStream.bufferedReader(StandardCharsets.UTF_8).useLines { lines ->
//                    plaintext += lines.fold("") { some, text ->
//                        "$some\n$text"
//                    }
//                }
//
//                Log.wtf("PLAIN_TEXT", plaintext)
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "File Content: $plaintext",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            } else {
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "Input has not been saved before",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
//        }

        // Setup SELECT IMAGE button
        findViewById<Button>(R.id.button_select_image).setOnClickListener {
            pickImageLauncher.launch("image/jpeg")
        }

        // Setup READ IMAGE button
        findViewById<Button>(R.id.button_read_image).setOnClickListener {
            showSavedImage()
        }
    }

    /**
     * Show saved images (unencrypted & encrypted) to an [ImageView].
     */
    private fun showSavedImage() {
        val file = File(filesDir, imageFilename)
        val unencryptedFile = File(filesDir, unencryptedImageFilename)

        if (file.exists() && unencryptedFile.exists()) {
            // Show the saved encrypted image in ImageView
            val encryptedFile = EncryptedFile.Builder(
                applicationContext,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val inputStream = encryptedFile.openFileInput()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            findViewById<ImageView>(R.id.image_view_encrypted).setImageBitmap(bitmap)

            // Show the saved normal image in ImageView
            val unencryptedBitmap = BitmapFactory.decodeStream(unencryptedFile.inputStream())
            findViewById<ImageView>(R.id.image_view_unencrypted).setImageBitmap(
                unencryptedBitmap
            )
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Please SELECT IMAGE first",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Save image as normal (unencrypted) file from [imageUri].
     */
    private fun saveUnencryptedImage(imageUri: Uri) {
        deleteFileIfExist(unencryptedImageFilename)

        val imageInputStream = contentResolver.openInputStream(imageUri)

        writeFile(openFileOutput(unencryptedImageFilename, Context.MODE_PRIVATE), imageInputStream)
    }

    /**
     * Save image as encrypted file from [imageUri].
     */
    private fun saveEncryptedImage(imageUri: Uri) {
        deleteFileIfExist(imageFilename)

        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            File(filesDir, imageFilename),
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val imageInputStream = contentResolver.openInputStream(imageUri)
        writeFile(encryptedFile.openFileOutput(), imageInputStream)
    }

    /**
     * Delete file with name [filename] in internal storage if exist.
     */
    private fun deleteFileIfExist(filename: String) {
        val file = File(filesDir, filename)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Write [inputStream] into [outputStream].
     */
    private fun writeFile(outputStream: FileOutputStream, inputStream: InputStream?) {
        outputStream.use { output ->
            inputStream.use { input ->
                input?.let {
                    val buffer =
                        ByteArray(4 * 1024) // buffer size
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                }
            }
        }
    }
}