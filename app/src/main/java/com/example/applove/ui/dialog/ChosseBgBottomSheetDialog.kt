import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applove.R
import com.example.applove.adpater.ChooseBgAdapter
import com.example.applove.databinding.DialogChooseBackgoundBinding
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.BackgroundModel
import com.example.applove.roomdb.repository.BackgroundRepository
import com.example.applove.ui.dialog.GenderBottomSheetDialog
import com.example.applove.ui.fragment.HomeFragment
import com.example.applove.viewmodel.BackgroundViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay

class ChosseBgBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogChooseBackgoundBinding
    private lateinit var adapter: ChooseBgAdapter
    private lateinit var backgroundViewModel: BackgroundViewModel

    // Launcher cho Android 13+
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            backgroundViewModel.saveBackground(it.toString())
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uriImage = Uri.parse(MediaStore.Images.Media.insertImage(requireContext().contentResolver, it, "Title", null))
            backgroundViewModel.saveBackground(uriImage.toString())
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomDialogTheme // Áp dụng theme từ styles.xml
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogChooseBackgoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = DBHelper.getIntance(requireContext())
        val repository = BackgroundRepository(db)
        backgroundViewModel = BackgroundViewModel(repository)

        adapter = ChooseBgAdapter(
            onClick = { background ->
                Log.d("BottomSheet", "Đang gửi URI: ${background.imageUri}")
                val homeFragment = parentFragment as? HomeFragment
                homeFragment?.updateBackground(background.imageUri)
                dismiss()
            },
            onChooseFromGallery = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+ (Không cần quyền READ_EXTERNAL_STORAGE)
                    pickImageLauncher.launch("image/*")
                } else {
                    // Android 12 trở xuống
                    openGallery()
                }
            }
        )

        binding.rcvBackground.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvBackground.adapter = adapter

        backgroundViewModel.backgrounds.observe(viewLifecycleOwner) { backgrounds ->
            if (backgrounds.isNullOrEmpty()) {
                backgroundViewModel.saveBackground("android.resource://${requireContext().packageName}/${R.drawable.bg_backround1}")
                backgroundViewModel.saveBackground("android.resource://${requireContext().packageName}/${R.drawable.bg_default}")
                backgroundViewModel.saveBackground("android.resource://${requireContext().packageName}/${R.drawable.bg_default1}")
            } else {
                adapter.submitList(backgrounds)
                adapter.notifyDataSetChanged() // Cập nhật RecyclerView ngay lập tức
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return

            val realPath = getRealPathFromURI(imageUri)
            val uriToSave = realPath?.let { Uri.parse(it) } ?: imageUri

            Log.d("Gallery", "Đường dẫn thật: $realPath")
            backgroundViewModel.saveBackground(uriToSave.toString())
        }
    }


    private fun getRealPathFromURI(uri: Uri): String? {
        var result: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (index >= 0) {
                    result = it.getString(index)
                }
            }
        }
        return result
    }



    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }


}
