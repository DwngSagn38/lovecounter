package com.example.applove.ui.fragment

import ChosseBgBottomSheetDialog
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.applove.R
import com.example.applove.databinding.FragmentHomeBinding
import com.example.applove.roomdb.repository.PersonRepository
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.PersonModel
import com.example.applove.roomdb.repository.DateRepository
import com.example.applove.ui.dialog.GenderBottomSheetDialog
import com.example.applove.ui.setting.LanguageActivity
import com.example.applove.ui.setting.SettingActivity
import com.example.applove.ui.startdate.StartDateActivity
import com.example.applove.viewmodel.BackgroundViewModel
import com.example.applove.viewmodel.DateViewModel
import com.example.applove.viewmodel.PersonViewModel
import com.example.lovecounter.base.BaseFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var viewModel: PersonViewModel
    private lateinit var dateViewModel: DateViewModel
    private lateinit var backgroundViewModel: BackgroundViewModel

    override fun inflateViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        super.initView()

//        setTaptap()

        val db = DBHelper.getIntance(requireContext())
        viewModel = PersonViewModel(PersonRepository(db))
        dateViewModel = DateViewModel(DateRepository(db))

        viewModel.persons.observe(viewLifecycleOwner) { persons ->
            if (persons.isNullOrEmpty()) {
                Log.d("HomeFragment1", "Danh sách trống")
                lifecycleScope.launch {
                    listDefault(PersonRepository(db)) // Chỉ thêm dữ liệu mặc định khi danh sách thực sự rỗng
                }
            } else {
                for (person in persons) {
                    Log.d("List", "Person: $person")
                    getPersonById(person)
                }
            }
        }

        viewBinding.imgSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        viewBinding.btnStar.setOnClickListener{
            startActivity(Intent(requireContext(), StartDateActivity::class.java))
        }

        viewBinding.imgPicture.setOnClickListener {
            showChooseBgDialog()
        }

        lifecycleScope.launch {
            val days = dateViewModel.getCountDay()
            val dateText = "$days\n day"
            viewBinding.txtDay.text = dateText
        }

        // 🔥 Load ảnh đã lưu từ SharedPreferences
        val savedUri = getSavedImageUri()
        savedUri?.let {
            updateBackground(it)
        }

    }


    private fun showChooseBgDialog() {
        val bottomSheet = ChosseBgBottomSheetDialog()
        bottomSheet.show(childFragmentManager, "ChooseBackground")
    }

    fun updateBackground(imageRes: String) {
        val imageUri = Uri.parse(imageRes)
        Log.d("HomeFragment", "Cập nhật ảnh: $imageUri")

        if (imageRes.startsWith("android.resource://")) {
            try {
                val resId = imageUri.pathSegments.last().toInt()
                viewBinding.imgBackground.setImageResource(resId)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                viewBinding.imgBackground.setImageResource(R.drawable.img_home)
            }
        } else {
            // Chỉ cấp quyền nếu URI là content://
            if (imageRes.startsWith("content://")) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    requireContext().contentResolver.takePersistableUriPermission(imageUri, flag)
                } catch (e: SecurityException) {
                    Log.e("HomeFragment", "Không thể cấp quyền URI: $imageUri", e)
                }
            }
            viewBinding.imgBackground.setImageURI(imageUri)
        }
        saveImageUri(imageRes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lắng nghe kết quả cập nhật từ BottomSheetDialog
        requireActivity().supportFragmentManager.setFragmentResultListener("updatePerson", viewLifecycleOwner) { _, _ ->
            Log.d("HomeFragment", "🔄 Received updatePerson event")
            viewModel.refreshData()
        }

    }

    private fun showBottomSheetDialog(person: PersonModel) {
        val bottomSheet = GenderBottomSheetDialog.newInstance(person)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAge(birthday: String): Int {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val birthDate = LocalDate.parse(birthday, formatter)
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }

    private suspend fun listDefault(personRepository: PersonRepository){
        val person1 = PersonModel(0, "Sasuke", "Male", "10/01/2001", "")
        val person2 = PersonModel(1, "Naruto", "Female", "10/01/2004","")

        personRepository.insertPerson(person1)
        personRepository.insertPerson(person2)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPersonById(person: PersonModel)  {
        // 0 = left , 1 = right
        if (person.id == 0) {
            viewBinding.txtNameMale.text = person.name
            Log.d("HomeFragment", "🔄 Updating UI for: ${person}")
            if (!person.image.isNullOrEmpty()) {
                val imageFile = File(person.image) // Kiểm tra nếu ảnh là file path
                if (imageFile.exists()) {
                    viewBinding.imgMale.setImageURI(Uri.fromFile(imageFile)) // Hiển thị từ file
                } else {
                    // Kiểm tra xem imagePath là URI hay resource ID
                    person.image?.let { path ->
                        if (path.toIntOrNull() != null) {
                            viewBinding.imgMale.setImageResource(path.toInt()) // Hiển thị avatar mặc định
                        } else {
                            viewBinding.imgMale.setImageURI(Uri.parse(path)) // Hiển thị ảnh từ thư viện/camera
                        }
                    }
                }
            }

            when(person.gender){
                "Male" -> viewBinding.icMale.setImageResource(R.drawable.ic_male)
                "Female" -> viewBinding.icMale.setImageResource(R.drawable.ic_female)
                else -> viewBinding.icMale.setImageResource(R.drawable.ic_other)
            }

            viewBinding.txtAgeMale.text = person.birthday?.let { getAge(it).toString() }

            viewBinding.imgMale.setOnClickListener {
                showBottomSheetDialog(person)
            }
        }else{
            viewBinding.txtNameFemale.text = person.name
            if (!person.image.isNullOrEmpty()) {
                val imageFile = File(person.image) // Kiểm tra nếu ảnh là file path
                if (imageFile.exists()) {
                    viewBinding.imgFemale.setImageURI(Uri.fromFile(imageFile)) // Hiển thị từ file
                } else {
                    // Kiểm tra xem imagePath là URI hay resource ID
                    person.image?.let { path ->
                        if (path.toIntOrNull() != null) {
                            viewBinding.imgFemale.setImageResource(path.toInt()) // Hiển thị avatar mặc định
                        } else {
                            viewBinding.imgFemale.setImageURI(Uri.parse(path)) // Hiển thị ảnh từ thư viện/camera
                        }
                    }
                }
            }

            when(person.gender){
                "Male" -> viewBinding.icFemale.setImageResource(R.drawable.ic_male)
                "Female" -> viewBinding.icFemale.setImageResource(R.drawable.ic_female)
                else -> viewBinding.icFemale.setImageResource(R.drawable.ic_other)
            }

            viewBinding.txtAgeFemale.text = person.birthday?.let { getAge(it).toString() }

            viewBinding.imgFemale.setOnClickListener {
                showBottomSheetDialog(person)
            }

        }
    }

    private fun saveImageUri(imageUri: String) {
        val sharedPref = requireContext().getSharedPreferences("AppPrefs", Activity.MODE_PRIVATE)
        sharedPref.edit().putString("saved_image_uri", imageUri).apply()
    }

    private fun getSavedImageUri(): String? {
        val sharedPref = requireContext().getSharedPreferences("AppPrefs", Activity.MODE_PRIVATE)
        return sharedPref.getString("saved_image_uri", null)
    }

    private fun setTaptap(){
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val taptap = remoteConfig.getString("taptap")
                    viewBinding.txtTap.text = taptap
                    Log.d("RemoteConfig", "Giá trị từ Firebase: $taptap")
                    } else {
                    Log.e("RemoteConfig", "Lỗi khi lấy dữ liệu từ Firebase")
                }
            }
    }
}