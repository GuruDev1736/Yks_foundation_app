package com.taskease.yksfoundation.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.firebase.storage.FirebaseStorage
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.CreatePostRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.CreatePostResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityCreatePostBinding
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Locale

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var imageAdapter: ImagePagerAdapter

    private val imageUris = mutableListOf<Uri>()
    private val uploadedUrls = mutableListOf<String>()

    private val tempUrisForCropping = mutableListOf<Uri>()
    private var currentCroppingIndex = 0

    private val LOCATION_PERMISSION_REQUEST = 1001


    private val launcher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) {
                tempUrisForCropping.clear()
                tempUrisForCropping.addAll(uris)
                currentCroppingIndex = 0
                startCrop(tempUrisForCropping[currentCroppingIndex])
            }
        }


    private fun startCrop(sourceUri: Uri) {
        val destinationFileName = "cropped_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, destinationFileName))

        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(70) // Adjust quality if needed
            setHideBottomControls(true) // Hides freestyle controls
            setFreeStyleCropEnabled(false)
        }

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f)

        cropImageLauncher.launch(uCrop.getIntent(this))
    }


    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    imageUris.add(it)
                    imageAdapter.notifyDataSetChanged()
                    setupDots()
                }

                currentCroppingIndex++
                if (currentCroppingIndex < tempUrisForCropping.size) {
                    startCrop(tempUrisForCropping[currentCroppingIndex])
                } else {
                    binding.postImages.visibility = View.GONE
                    binding.ViewpagerLayout.visibility = View.VISIBLE
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                Toast.makeText(this, "Crop error: ${error?.message}", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

       checkLocationEnabledAndRequest()

        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.actionBar.toolbarTitle.text = "Create Post"

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabDots)

        imageAdapter = ImagePagerAdapter(imageUris)
        viewPager.adapter = imageAdapter

        binding.postImages.setOnClickListener {
            launcher.launch("image/*")
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        binding.submit.setOnClickListener {

            val caption = binding.caption.text.toString()
            val location = binding.location.text.toString()

            if (valid(caption, location)) {
                uploadImagesToCloudinary(caption, location)
            }
        }
    }

    private fun setupDots() {
        tabLayout.removeAllTabs()
        for (i in imageUris.indices) {
            tabLayout.addTab(tabLayout.newTab())
        }
    }

    private fun uploadImagesToCloudinary(caption: String, location: String) {
        val progress = CustomProgressDialog(this)
        progress.show()

        val isSuperAdmin = intent.getBooleanExtra("isSuperAdmin", false)
        val base64Strings = mutableListOf<String>()

        if (imageUris.isEmpty()) {
            progress.dismiss()
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            imageUris.forEach { uri ->
                val base64 = Constant.uriToBase64(this, uri)
                if (!base64.isNullOrEmpty()) {
                    base64Strings.add(base64)
                }
            }

            val finalBase64String = base64Strings.joinToString(",")

            progress.dismiss()
            Log.d("Base64Result", finalBase64String)

            if (isSuperAdmin) {
                CreatePostBySuperAdmin(caption, location, finalBase64String)
            } else {
                CreatePostByUser(caption, location, finalBase64String)
            }

        } catch (e: Exception) {
            progress.dismiss()
            e.printStackTrace()
            Toast.makeText(this, "Failed to convert images: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun CreatePostBySuperAdmin(caption: String, location: String, finalUrls: String) {

        val progress = CustomProgressDialog(this)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {

            val model = CreatePostRequestModel(location, finalUrls, caption)
            RetrofitInstance.getHeaderInstance().createSuperAdminPost(userId, model)
                .enqueue(object :
                    Callback<CreatePostResponseModel> {
                    override fun onResponse(
                        call: retrofit2.Call<CreatePostResponseModel>,
                        response: Response<CreatePostResponseModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    Constant.success(this@CreatePostActivity, data.MSG)
                                    finish()
                                } else {
                                    Constant.error(this@CreatePostActivity, data.MSG)
                                }
                            } else {
                                Constant.error(this@CreatePostActivity, "No data received")
                            }
                        } else {
                            Constant.error(this@CreatePostActivity, "Response unsuccessful")
                            Log.e(
                                "SelectSocietyFragment",
                                "Error response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<CreatePostResponseModel>,
                        t: Throwable
                    ) {
                        progress.dismiss()
                        Constant.error(
                            this@CreatePostActivity,
                            "Something went wrong: ${t.message}"
                        )
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@CreatePostActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun CreatePostByUser(caption: String, location: String, finalUrls: String) {

        val progress = CustomProgressDialog(this)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)
        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        try {

            val model = CreatePostRequestModel(location, finalUrls, caption)
            RetrofitInstance.getHeaderInstance().createPost(userId, societyId, model)
                .enqueue(object :
                    Callback<CreatePostResponseModel> {
                    override fun onResponse(
                        call: retrofit2.Call<CreatePostResponseModel>,
                        response: Response<CreatePostResponseModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    Constant.success(this@CreatePostActivity, data.MSG)
                                    finish()
                                } else {
                                    Constant.error(this@CreatePostActivity, data.MSG)
                                }
                            } else {
                                Constant.error(this@CreatePostActivity, "No data received")
                            }
                        } else {
                            Constant.error(this@CreatePostActivity, "Response unsuccessful")
                            Log.e(
                                "SelectSocietyFragment",
                                "Error response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<CreatePostResponseModel>,
                        t: Throwable
                    ) {
                        progress.dismiss()
                        Constant.error(
                            this@CreatePostActivity,
                            "Something went wrong: ${t.message}"
                        )
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@CreatePostActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    fun valid(caption: String, location: String): Boolean {
        if (caption.isEmpty()) {
            Toast.makeText(this, "Please Enter Caption", Toast.LENGTH_SHORT).show()
            return false
        }
        if (location.isEmpty()) {
            Toast.makeText(this, "Please Enter Location", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Please Select Images", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkLocationEnabledAndRequest() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "Please enable location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            checkPermissionsAndFetchLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabledAndRequest()
    }

    private fun checkPermissionsAndFetchLocation() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST)
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            checkPermissionsAndFetchLocation()
        }
    }

    private fun getCurrentLocation() {
        val progress = CustomProgressDialog(this)
        progress.show()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            progress.dismiss()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                handleLocationSuccess(location,progress)
            } else {
                // fallback to request location updates
                val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                    priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                    numUpdates = 1
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                            val newLocation = result.lastLocation
                            if (newLocation != null) {
                                handleLocationSuccess(newLocation,progress)
                            } else {
                                progress.dismiss()
                                Toast.makeText(this@CreatePostActivity, "Unable to get location", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    null
                )
            }
        }.addOnFailureListener {
            progress.dismiss()
            Toast.makeText(this, "Location error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLocationSuccess(location: Location,progressDialog: CustomProgressDialog) {
        lifecycleScope.launch {
            Log.d("LOCATION", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            val address = getAddressFromCoordinates(location.latitude, location.longitude)
            if (address != null) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    binding.location.setText(address)
                    Log.d("LOCATION", "Address: $address")
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this@CreatePostActivity, "Address not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(5000) {
                try {
                    val geocoder = Geocoder(this@CreatePostActivity, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        addresses[0].getAddressLine(0)
                    } else null
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}


class ImagePagerAdapter(private val imageUris: List<Uri>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_viewpager, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = imageUris.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(imageUris[position]).into(holder.image)
    }
}
