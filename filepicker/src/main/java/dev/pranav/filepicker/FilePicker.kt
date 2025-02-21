package dev.pranav.filepicker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialFadeThrough
import dev.pranav.filepicker.databinding.FilePickerBinding
import dev.pranav.filepicker.databinding.ItemFileBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FilePickerOptions {
    var selectFolder = false
    var extensions = emptyArray<String>()
}

open class FilePickerCallback {
    open fun onFileSelected(f: File) = Unit
    open fun onFileSelectionCancelled() = true
}

class FilePickerDialogFragment(
    val options: FilePickerOptions,
    val callback: FilePickerCallback = FilePickerCallback()
) : DialogFragment() {
    private lateinit var binding: FilePickerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FilePickerBinding.inflate(inflater, container, false)

        if (options.selectFolder) {
            binding.select.text = getString(R.string.select_folder)
        } else {
            binding.select.text = getString(R.string.select_file)
        }

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()

        requestManageAllFilesPermission()

        binding.toolbar.setNavigationOnClickListener {
            if (callback.onFileSelectionCancelled()) {
                dismiss()
            }
        }

        binding.cancel.setOnClickListener {
            if (callback.onFileSelectionCancelled()) {
                dismiss()
            }
        }

        binding.toolbar.title =
            if (options.selectFolder) getString(R.string.select_folder) else getString(R.string.select_file)

        binding.select.setOnClickListener {
            val selectedFile = (binding.files.adapter as FileAdapter).getSelectedFile()
            if (selectedFile == null) {
                Toast.makeText(
                    requireContext(),
                    if (options.selectFolder) getString(R.string.no_folder_selected) else getString(
                        R.string.no_file_selected
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val data = Intent()
            data.data = Uri.fromFile(selectedFile)
            dismiss()
            callback.onFileSelected(selectedFile)
        }

        val files = listFiles(Environment.getExternalStorageDirectory())
        val adapter = FileAdapter()

        binding.files.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.files.adapter = adapter
        adapter.setFiles(Environment.getExternalStorageDirectory(), files)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FilePicker_Dialog)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // For edge-to-edge display, make the dialog fullscreen and transparent
        WindowCompat.setDecorFitsSystemWindows(dialog?.window!!, false)

        val windowController =
            WindowCompat.getInsetsController(dialog?.window!!, dialog?.window!!.decorView)
        windowController.isAppearanceLightStatusBars = false
        windowController.isAppearanceLightNavigationBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.root.updatePadding(
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        val files = listFiles(Environment.getExternalStorageDirectory())
        val adapter = binding.files.adapter as FileAdapter
        adapter.setFiles(Environment.getExternalStorageDirectory(), files)
    }

    private fun requestManageAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) return
            try {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:${requireContext().packageName}")
                    )
                )
            } catch (_: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) return
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun listFiles(root: File): List<File> {
        return (root.listFiles()?.toList() ?: emptyList()).filter { file ->
            if (options.selectFolder) {
                file.isDirectory
            } else {
                if (options.extensions.isEmpty()) true else options.extensions.any { file.extension == it }
            }
        }.sortedBy { it.name }.sortedByDescending { it.isDirectory }
    }

    private inner class FileAdapter : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
        private val files = mutableListOf<File>()
        private var selectedFile: File? = null

        // Keep track of bound ViewHolders
        private val boundViewHolders = mutableSetOf<FileViewHolder>()

        @SuppressLint("NotifyDataSetChanged")
        fun setFiles(root: File, files: List<File>) {
            this.files.clear()
            this.files.add(root)
            this.files.addAll(files)
            selectedFile = null
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
            val binding =
                ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FileViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
            boundViewHolders.add(holder)
            if (position == 0) {
                holder.bind(files[0], true)
            } else holder.bind(files[position])
        }

        override fun onViewRecycled(holder: FileViewHolder) {
            super.onViewRecycled(holder)
            boundViewHolders.remove(holder)
        }

        override fun getItemCount(): Int = files.size

        inner class FileViewHolder(private val binding: ItemFileBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private var currentFile: File? = null

            @SuppressLint("SetTextI18n")
            fun bind(file: File, up: Boolean = false) {
                currentFile = file

                if (up) {
                    binding.icon.setImageResource(R.drawable.outline_folder_24)
                    binding.name.text = ".."
                    binding.details.text = "Parent Directory"
                    binding.icon.imageTintList = ColorStateList.valueOf(
                        MaterialColors.getColor(
                            binding.root,
                            androidx.appcompat.R.attr.colorControlNormal
                        )
                    )
                    binding.checkbox.visibility = View.GONE
                    binding.root.setOnClickListener {
                        if (!file.canRead()) {
                            Toast.makeText(
                                binding.root.context,
                                "Cannot read parent directory",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        setFiles(file.parentFile!!, listFiles(file))
                    }
                } else {
                    binding.icon.setImageResource(if (file.isDirectory) R.drawable.outline_folder_24 else R.drawable.outline_insert_drive_file_24)
                    binding.icon.imageTintList = ColorStateList.valueOf(
                        MaterialColors.getColor(
                            binding.root,
                            androidx.appcompat.R.attr.colorPrimary
                        )
                    )
                    binding.name.text = file.name
                    binding.details.text =
                        SimpleDateFormat(
                            "dd-mm-yyyy",
                            Locale.getDefault()
                        ).format(Date(file.lastModified())) + " | " + getSize(
                            file
                        )

                    if (options.selectFolder) {
                        binding.checkbox.visibility =
                            if (file.isDirectory) View.VISIBLE else View.GONE
                    } else {
                        binding.checkbox.visibility =
                            if (!file.isDirectory) View.VISIBLE else View.GONE
                    }

                    if (file.isDirectory) {
                        binding.root.setOnClickListener {
                            if (!file.canRead()) {
                                Toast.makeText(
                                    binding.root.context,
                                    "Cannot read directory",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                            setFiles(file.parentFile!!, listFiles(file))
                        }
                    } else {
                        binding.root.setOnClickListener {}
                    }

                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = file == selectedFile

                    binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            updateSelection(file)
                        } else if (file == selectedFile) {
                            updateSelection(null)
                        }
                    }
                }
            }

            fun updateCheckboxState() {
                currentFile?.let { file ->
                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = file == selectedFile
                    binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            updateSelection(file)
                        } else if (file == selectedFile) {
                            updateSelection(null)
                        }
                    }
                }
            }
        }

        private fun updateSelection(newSelection: File?) {
            selectedFile = newSelection
            boundViewHolders.forEach { holder ->
                holder.updateCheckboxState()
            }
        }

        private fun getSize(file: File): String {
            val size = file.length()
            val units = arrayOf("B", "KiB", "MiB", "GiB", "TiB")
            var unit = 0
            var sizeD = size.toDouble()
            while (sizeD > 1024 && unit < units.size) {
                sizeD /= 1024
                unit++
            }
            return String.format(Locale.getDefault(), "%.2f %s", sizeD, units[unit])
        }

        fun getSelectedFile(): File? = selectedFile
    }
}
