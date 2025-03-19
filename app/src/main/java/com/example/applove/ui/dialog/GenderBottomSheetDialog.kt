package com.example.applove.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applove.R
import com.example.applove.adpater.AvatarAdapter
import com.example.applove.databinding.DialogInfomationBinding
import com.example.applove.roomdb.repository.PersonRepository
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.PersonModel
import com.example.applove.ui.main.MainActivity
import com.example.applove.viewmodel.PersonViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class GenderBottomSheetDialog() : BottomSheetDialogFragment() {

    private var _binding: DialogInfomationBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_PICK = 100

    private lateinit var personViewModel: PersonViewModel

    private var person: PersonModel? = null

    private var uriImage: Uri? = null
    private var selectedAvatar: Int? = null

    private val avatarList = listOf(
        R.drawable.img_male,
        R.drawable.img_female,
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4,
        R.drawable.avatar5,
        R.drawable.avatar6,
        R.drawable.avatar7,
        R.drawable.avatar8,
        R.drawable.avatar9,
        R.drawable.avatar10,
        R.drawable.avatar11,
        R.drawable.avatar12,
        R.drawable.avatar13,
        R.drawable.avatar14,
    )


    // Launcher cho Android 13+
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imgGender.setImageURI(it)
            binding.imgGender.tag = it.toString()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            binding.imgGender.setImageBitmap(it)
            uriImage = Uri.parse(MediaStore.Images.Media.insertImage(requireContext().contentResolver, it, "Title", null))
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomDialogTheme // Áp dụng theme từ styles.xml
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            person = it.getParcelable("person") // Nhận dữ liệu
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogInfomationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPerson()
        setViewModel()

        binding.edtName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.imgSelectGender.requestFocus() // Chuyển focus sang EditText tiếp theo
                return@setOnEditorActionListener true
            }
            false
        }

        binding.imgSelectGender.setOnClickListener { view ->
            showCouponMenu(view)
        }

        binding.imgCalendar.setOnClickListener {
            showDatePickerDialog()
        }

        binding.imgGender.setOnClickListener {
            DialogSelect()
        }

        binding.btnSave.setOnClickListener {
            savePerson()
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    private fun savePerson(){
        val name = if (binding.edtName.text.isNullOrBlank()) person?.name ?: "" else binding.edtName.text.toString()
        val gender = if (binding.edtGender.text.isNullOrBlank()) person?.gender ?: "" else binding.edtGender.text.toString()
        val birthday = if (binding.edtBirthday.text.isNullOrBlank()) person?.birthday ?: "" else binding.edtBirthday.text.toString()
        // Xác định ảnh được chọn
        val imagePath = when {
            uriImage != null -> uriImage.toString() // Nếu chọn ảnh từ thư viện/camera
            selectedAvatar != null -> selectedAvatar.toString() // Nếu chọn avatar mặc định
            else -> person?.image ?: "" // Nếu không chọn ảnh nào, giữ ảnh cũ
        }

        val newPerson = PersonModel(person!!.id, name, gender, birthday, imagePath);
        // Thực hiện cập nhật vào database (nếu cần)
        lifecycleScope.launch {
//                repository.updatePerson(newPerson)
            val result = personViewModel.updatePerson(person!!.id, name, gender, birthday, imagePath) // Nếu có cập nhật DB
            if (result.equals("Success")){
                requireActivity().supportFragmentManager.setFragmentResult("updatePerson", Bundle())
//                personViewModel.refreshData()
                delay(300)
                Toast.makeText(requireContext(), R.string.save_person, Toast.LENGTH_SHORT).show()
                dismiss()
            }else{
                Toast.makeText(requireContext(), R.string.save_person_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setViewModel(){
        val db = DBHelper.getIntance(requireContext())
        val repository = PersonRepository(db)
        personViewModel =  PersonViewModel(repository)
    }

    private fun getPerson(){
        // Hiển thị dữ liệu của `person`
        person?.let {
            binding.edtName.setHint(it.name)
            binding.edtGender.setHint(it.gender)
            binding.edtBirthday.setHint(it.birthday)
            val imageFile = it.image?.let { it1 -> File(it1) }
            if (imageFile!!.exists()) {
                binding.imgGender.setImageURI(Uri.fromFile(imageFile)) // Hiển thị từ file
            } else {
                person?.image?.let { path ->
                    if (path.toIntOrNull() != null) {
                        binding.imgGender.setImageResource(path.toInt()) // Hiển thị avatar mặc định
                    } else {
                        binding.imgGender.setImageURI(Uri.parse(path)) // Hiển thị ảnh từ thư viện/camera
                    }
                }
            }

            if (it.image.equals("")){
                if (it.id == 0) {
                    binding.imgGender.setImageResource(R.drawable.img_male)
                } else {
                    binding.imgGender.setImageResource(R.drawable.img_female)
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(person: PersonModel): GenderBottomSheetDialog {
            val fragment = GenderBottomSheetDialog()
            val args = Bundle()
            args.putParcelable("person", person) // Truyền đối tượng PersonModel
            fragment.arguments = args
            return fragment
        }
    }
    private fun showCouponMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)

        val menu = popupMenu.menu
        menu.add(0, 0, 0, "Male")
        menu.add(0, 1, 1, "Female")
        menu.add(0, 2, 2, "Other")

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> binding.edtGender.setText("Male")
                1 -> binding.edtGender.setText("Female")
                2 -> binding.edtGender.setText("Other")
            }
            true
        }

        popupMenu.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Định dạng lại ngày thành dạng "dd/MM/yyyy"
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.edtBirthday.setText(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // Xử lý kết quả chọn ảnh cho Android 12 trở xuống
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            uriImage = imageUri
            binding.imgGender.setImageURI(imageUri)

            // Lấy đường dẫn thực tế của ảnh
            val realPath = imageUri?.let { getRealPathFromURI(requireContext(), it) }
            uriImage = if (realPath != null) Uri.parse(realPath) else imageUri
        }
    }


    private fun DialogSelect(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_choose_image)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)

        val btnCamera = dialog.findViewById<LinearLayout>(R.id.llCamera)
        val btnGallery = dialog.findViewById<LinearLayout>(R.id.llGallery)
        val btnAvatar = dialog.findViewById<LinearLayout>(R.id.llDefault)

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
            dialog.dismiss()
        }

        btnGallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ (Không cần quyền READ_EXTERNAL_STORAGE)
                pickImageLauncher.launch("image/*")
                dialog.dismiss()
            } else {
                // Android 12 trở xuống
                pickImageFromGallery()
                dialog.dismiss()
            }
        }

        btnAvatar.setOnClickListener {
            showAvatarBottomSheet()
            dialog.dismiss()
        }
    }
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), R.string.permission_camera, Toast.LENGTH_SHORT).show()
        }
    }
    private fun openCamera() {
        takePictureLauncher.launch(null)
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_data")
                if (columnIndex != -1) {
                    filePath = it.getString(columnIndex)
                }
            }
        }
        return filePath ?: uri.path // Nếu không có _data, trả về path mặc định
    }

    private fun showAvatarBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_choose_backgound, null)
        bottomSheetDialog.setContentView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rcvBackground)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Hiển thị 3 cột
        recyclerView.adapter = AvatarAdapter(avatarList) { selectedAvatarRes ->
            selectedAvatar = selectedAvatarRes // Lưu ID ảnh avatar
            uriImage = null
            binding.imgGender.setImageResource(selectedAvatarRes) // Đặt avatar cho ImageView
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}
