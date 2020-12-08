package com.trallerd.permition

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val IMAGE_PICK = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendTextButton.setOnClickListener {
            if (!TextUtils.isEmpty(txtSend.text.toString())) {
                sendText(txtSend.text.toString())
            } else {
                Toast.makeText(applicationContext, R.string.intput_text_error, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        chooseGalleryButton.setOnClickListener {
            getPick()
        }

        buttonPrmission.setOnClickListener {
            permission()
        }
    }

    private fun permission() {
        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    showGranted()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    showDenied()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                   showDeniedPermanently()
                }

            }).check()
    }

    private fun sendText(text: String) {
        val txtIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(txtIntent)
    }

    fun getPick() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK) {
            val inputStream = contentResolver.openInputStream(data?.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            sender(bitmap)
        }
    }

    fun sender(bitmap: Bitmap?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            val path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null)
            val imageUri = Uri.parse(path)
            putExtra(Intent.EXTRA_STREAM, imageUri)
        }
        startActivity(Intent.createChooser(intent, "Select"))
    }

    fun showGranted() {
        val textContcts = findViewById<TextView>(R.id.txtPermission)
        textContcts.text = getString(R.string.granted)
        textContcts.setTextColor(ContextCompat.getColor(this, R.color.permission_granted))
    }

    fun showDenied() {
        val textContcts = findViewById<TextView>(R.id.txtPermission)
        textContcts.text = getString(R.string.denied)
        textContcts.setTextColor(ContextCompat.getColor(this, R.color.permission_denied))
    }

    fun showDeniedPermanently() {
        val textContcts = findViewById<TextView>(R.id.txtPermission)
        textContcts.text = getString(R.string.permanently)
        textContcts.setTextColor(ContextCompat.getColor(this, R.color.permission_denied_permanent))
    }

}