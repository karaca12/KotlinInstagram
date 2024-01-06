package com.karacamehmet.kotlininstagram.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.karacamehmet.kotlininstagram.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLauncher()
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.buttonUpload.setOnClickListener { v ->
            //universal unique id (basically it makes up an id)
            val uuid = UUID.randomUUID()
            val imageName = "${uuid}.jpg"
            val reference = storage.reference
            val imageReference = reference.child("images").child(imageName)
            if (selectedPicture != null) {
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    //download url -> firestore
                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        if (auth.currentUser != null) {
                            val postMap = hashMapOf<String, Any>()
                            postMap["downloadUrl"] =
                                downloadUrl//this is same as "postMap.put("downloadUrl",downloadUrl)"
                            postMap["userEmail"] = auth.currentUser!!.email!!
                            postMap["explanation"] = binding.editTextPostExplanation.text.toString()
                            postMap["timeStamp"] = Timestamp.now()

                            firestore.collection("Post").add(postMap).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                Snackbar.make(
                                    v,
                                    it.localizedMessage as CharSequence,
                                    Snackbar.LENGTH_LONG
                                )
                                    .show()
                            }
                        }


                    }

                }.addOnFailureListener {
                    Snackbar.make(v, it.localizedMessage as CharSequence, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.imageViewSelectImage.setOnClickListener {
            if (Build.VERSION.SDK_INT <= 32) {//SDK<=32---------------------------------------------
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //Permission Not Granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        //If returns true we should show rationale
                        Snackbar.make(
                            it, "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction("Give Permission") {
                                //request permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                            .show()
                    } else {
                        //we don't need to show rationale
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    //Permission Granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }

            } else {//SDK>=33-----------------------------------------------------------------------
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //Permission Not Granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_MEDIA_IMAGES
                        )
                    ) {
                        //If returns true we should show rationale
                        Snackbar.make(
                            it, "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction("Give Permission") {
                                //request permission
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                            .show()
                    } else {
                        //we don't need to show rationale
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }

                } else {
                    //Permission Granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }
        }


    }

    private fun registerLauncher() {
        activityResultLauncher =//for redirecting to gallery
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.imageViewSelectImage.setImageURI(it)
                        }
                    }
                }
            }
        permissionLauncher =//for permission
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Snackbar.make(
                        binding.root, "Permission needed for gallery!",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }

    }


}