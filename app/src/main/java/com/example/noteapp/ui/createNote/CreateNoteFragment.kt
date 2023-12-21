package com.example.noteapp.ui.createNote

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.noteapp.R
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.local.entities.NotifEntity
import com.example.noteapp.databinding.FragmentCreateNoteBinding
import com.example.noteapp.util.extensions.EMPTY_STRING
import com.example.noteapp.util.extensions.makeGone
import com.example.noteapp.util.extensions.makeVisible
import com.example.noteapp.util.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
@AndroidEntryPoint
class CreateNoteFragment :
    Fragment(R.layout.fragment_create_note),
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val binding by viewBinding(FragmentCreateNoteBinding::bind)
    private val viewModel by viewModels<CreateNoteViewModel>()

//    var selectedColor = "#FFC3A1"
    var selectedColor = "#C6E89362"

    private var currentTime: String? = null
    private var currentTime2: String? = null
    private var currentTime3: String? = null


    // Permission Private Read & Write
    private var readStoragePerm = 123
    private var requestCodeImage = 456
//    private var GALLERY_REQ_CODE = 1000

    private var link = EMPTY_STRING
    private var selectedImagePath = EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getInt(getString(R.string.noteID), -1).also {
            if (it != -1) viewModel.setNoteId(it)

        }
    }

    companion object {

        const val NOTE_BOTTOM_SHEET_TAG = "Note Bottom Sheet Fragment"
//        const val SELECTED_COLOR = "selectedColor"

//        @JvmStatic
//        fun newInstance() =
//            CreateNoteFragment().apply {
//                arguments = Bundle().apply {
//                }
//            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        collectNotes()

    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.note.collectLatest {
            it?.let(this@CreateNoteFragment::setNoteDataInUI)
        }
    }

    private fun setNoteDataInUI(note: NoteEntity) = binding.apply {
        colorView.setBackgroundColor(Color.parseColor(note.color))
        etNoteTitle.setText(note.title)
        etNoteDesc.setText(note.noteText)

        if (note.imgPath != EMPTY_STRING) {
            selectedImagePath = note.imgPath.orEmpty()
            imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
            makeVisible(layoutImage,binding.imgNote, binding.imgDelete)
        } else {
            makeGone(layoutImage,binding.imgNote, binding.imgDelete)
        }

        if (note.webLink != EMPTY_STRING) {
            link = note.webLink.orEmpty()
            tvWebLink.text = note.webLink
            makeVisible(layoutWebUrl,imgUrlDelete)
            etWebLink.setText(note.webLink)
        } else {
            makeGone(imgUrlDelete,layoutWebUrl)
        }
    }

    private fun initViews() = binding.apply {
        // Register & Unregister broadcast receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver, IntentFilter(getString(R.string.intent_filter))
        )

        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        //Word count

        // Date & Time
        val sdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
        val sdf2 = SimpleDateFormat("dd MMMM yyyy | hh:mm a", Locale.ENGLISH)
//        val sdf2 = SimpleDateFormat("MMMM d HH:mm EEEE", Locale.ENGLISH)
        val sdf3 = SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.ENGLISH)

        currentTime = sdf.format(Date())
        currentTime2 = sdf2.format(Date())
        currentTime3 = sdf3.format(Date())


        tvDateTime.text = currentTime2


        // Done
        doneImg.setOnClickListener {
            viewModel.note.value?.let { updateNote(it) } ?: saveNote()
        }

        // Back Button
        backImg.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Show More Button
        imgMore.setOnClickListener {
            val noteBottomSheetFragment = NoteBottomFragment.newInstance(viewModel.noteId.value)
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                NOTE_BOTTOM_SHEET_TAG
            )
        }

        // Delete Image
        imgDelete.setOnClickListener {
            selectedImagePath = EMPTY_STRING
            layoutImage.visibility = View.GONE
        }

        btnOk.setOnClickListener {
            if (etWebLink.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(requireContext(), getString(R.string.url_require), Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            if (viewModel.noteId.value != null) {
                tvWebLink.makeVisible()
                layoutWebUrl.makeGone()
            } else {
                layoutWebUrl.makeGone()
            }
        }

        imgUrlDelete.setOnClickListener {
            link = EMPTY_STRING
            makeGone(tvWebLink, imgUrlDelete, layoutWebUrl)
        }

        tvWebLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onDestroyView()
    }
    private fun updateNote(note: NoteEntity) = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        val notify = NotifEntity().apply {
            action = "UPDATE"
            massage = "You have been update note ${note.title}"
//            massage = "You have been update note \"" + note.title + " \""
//            + note.title
            dataTime = currentTime3
        }
        note.apply {
            title = binding.etNoteTitle.text.toString()
            noteText = binding.etNoteDesc.text.toString()
            dateTime = currentTime
            color = selectedColor
            imgPath = selectedImagePath
            webLink = link
        }.also {
            viewModel.updateNote(it, notify)
        }
        binding.etNoteTitle.setText(EMPTY_STRING)
        binding.etNoteDesc.setText(EMPTY_STRING)
        makeGone(
            with(binding) {
                layoutImage
                imgNote
                tvWebLink
            }
        )
        requireActivity().supportFragmentManager.popBackStack()
    }


    private fun saveNote() {

        val etNoteTitle = view?.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view?.findViewById<EditText>(R.id.etNoteDesc)

        when {
            etNoteTitle?.text.isNullOrEmpty() -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.title_require),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snackbarok)) {

                    }.show()
            }

            etNoteDesc?.text.isNullOrEmpty() -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.empty_note_description_warning),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.snackbarok)) {

                }.show()
            }

            else -> {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    val notify = NotifEntity().apply {
                        action = "CREATE"
                        massage = "You have been creating new notes with the name ${etNoteTitle?.text.toString()}"
//                        massage = "You have been creating new notes with the name  \"" + etNoteTitle?.text.toString() + "\""
//                        + etNoteTitle?.text.toString()
                        dataTime = currentTime3
                    }
                    NoteEntity().apply {
                        title = etNoteTitle?.text.toString()
                        noteText = etNoteDesc?.text.toString()
                        dateTime = currentTime
                        color = selectedColor
                        imgPath = selectedImagePath
                        webLink = link
                    }.also {
                        viewModel.saveNote(it, notify)
                    }
                    etNoteTitle?.setText(EMPTY_STRING)
                    etNoteDesc?.setText(EMPTY_STRING)
                    makeGone(with(binding) {
                        layoutImage
                        imgNote
                        tvWebLink
                    })
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun deleteNote() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        val notify = NotifEntity().apply {
            action = "DELETE"
            massage = "You have been deleted note ${viewModel.note.value?.title}"
//            massage = "You have been deleted note \"" + viewModel.note.value?.title + "\""
//            + viewModel.note.value?.title
            dataTime = currentTime3
        }
        viewModel.deleteNote(notify)
        requireActivity().supportFragmentManager.popBackStack()
//        val isNoteDeleted = viewModel.deleteNote(notify)

        Log.d("error-del", "running line 290")
//
//        if (isNoteDeleted) {
//
//        } else {
//            Log.e("Deleting note", "Failed to delete note")
//        }
    }

    private fun checkWebUrl() {
        if (Patterns.WEB_URL.matcher(binding.etWebLink.text.toString()).matches()) {
            binding.layoutWebUrl.makeGone()
            binding.etWebLink.isEnabled = false
            link = binding.etWebLink.text.toString()
            binding.tvWebLink.makeVisible()
            binding.tvWebLink.text = binding.etWebLink.text.toString()
        } else {
            Toast.makeText(requireContext(), getString(R.string.url_validation), Toast.LENGTH_SHORT).show()
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            if (p1 == null)
                return

            val actionColor = p1.getStringExtra(getString(R.string.action))

            binding.apply {
                when (actionColor) {

                    getString(R.string.blue) -> {
                        selectedColor = "#9BB8CD"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.orange) -> {
                        selectedColor = "#b55104"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.purple) -> {
                        selectedColor = "#72148c"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.red) -> {
                        selectedColor = "#8c2014"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.yellow) -> {
                        selectedColor = "#bfb452"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.brown) -> {
                        selectedColor = "#3e2723"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.indigo) -> {
                        selectedColor = "#1a237e"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.defaultColor) -> {
                        selectedColor = "#C6E89362"
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.image) -> {
                        readStorageTask()
                        binding.layoutWebUrl.makeGone()
                    }

                    getString(R.string.webUrl) -> {
                        binding.layoutWebUrl.visibility = View.VISIBLE
                    }

                    getString(R.string.deleteNote) -> {
                        deleteNote()
                    }

                    else -> {
                        binding.layoutImage.visibility = View.GONE
                        imgNote.visibility = View.GONE
                        binding.layoutWebUrl.visibility = View.GONE
                        makeGone(with(binding) {
                            layoutImage
                            imgNote
                            layoutWebUrl
                        })
                        colorView.setBackgroundColor(Color.parseColor(selectedColor))
                  }
                    }
                }
            }
        }

        private fun hasReadStoragePerm(): Boolean {
            return EasyPermissions.hasPermissions(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        private fun readStorageTask() {
            if (hasReadStoragePerm()) {

                pickImageFromGallery()
            } else {
                EasyPermissions.requestPermissions(
                    requireActivity(), getString(R.string.storage_permission_text),
                    readStoragePerm, android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, requestCodeImage)
        }
    }

        private fun getPathFromUri(contentUri: Uri): String? {
            val filePath: String?
            val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
            if (cursor == null) {
                filePath = contentUri.path
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex("_data")
                filePath = cursor.getString(index)
                cursor.close()
            }
            return filePath
        }

        // Setup About Image
        @Deprecated("Use registerForActivityResult with ActivityResultContracts.StartActivityForResult", level = DeprecationLevel.WARNING)
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == requestCodeImage && resultCode == RESULT_OK) {
                if (data != null) {

                    val selectedImageUrl = data.data

                    if (selectedImageUrl != null) {
                        try {

                            val inputStream =
                                requireActivity().contentResolver.openInputStream(selectedImageUrl)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imgNote.setImageBitmap(bitmap)
                        makeVisible(with(binding) {
                            imgNote
                            layoutImage
                        })
                            selectedImagePath = getPathFromUri(selectedImageUrl).orEmpty()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                requireActivity()
            )
        }

        override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        }

        override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
            if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
                AppSettingsDialog.Builder(requireActivity()).build().show()
            }
        }

        override fun onRationaleAccepted(requestCode: Int) {
        }

        override fun onRationaleDenied(requestCode: Int) {
        }
    }
