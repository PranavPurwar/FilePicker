package dev.pranav.filepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import dev.pranav.filepicker.databinding.FilePickerBinding
import dev.pranav.filepicker.databinding.ItemFileBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FilePickerDialogFragment(
    val options: FilePickerOptions,
    val callback: FilePickerCallback = FilePickerCallback()
) : DialogFragment() {
    private lateinit var binding: FilePickerBinding

    private var currentDirectory: File =
        if (options.initialDirectory != null) File(options.initialDirectory!!) else Environment.getExternalStorageDirectory()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FilePickerBinding.inflate(inflater, container, false)

        when (options.selectionMode) {
            SelectionMode.FOLDER -> binding.select.text = getString(R.string.select_folder)
            SelectionMode.BOTH -> binding.select.text = getString(R.string.select_items)
            SelectionMode.FILE -> binding.select.text = getString(R.string.select_file)
        }

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

        binding.toolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_sort_24)

        binding.toolbar.title = options.title ?: when (options.selectionMode) {
            SelectionMode.FOLDER -> getString(R.string.pick_a_folder)
            SelectionMode.BOTH -> getString(R.string.pick_items)
            SelectionMode.FILE -> getString(R.string.pick_a_file)
        }

        binding.select.setOnClickListener {
            val selectedFiles = (binding.files.adapter as FileAdapter).getSelectedFiles()
            if (selectedFiles.isEmpty()) {
                Toast.makeText(
                    requireContext(), when (options.selectionMode) {
                        SelectionMode.FOLDER -> getString(R.string.no_folder_selected)
                        SelectionMode.BOTH -> getString(R.string.no_item_selected)
                        SelectionMode.FILE -> getString(R.string.no_file_selected)
                    }, Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            dismiss()
            if (options.multipleSelection) {
                callback.onFilesSelected(selectedFiles)
            } else {
                callback.onFileSelected(selectedFiles.first())
            }
        }

        if (options.showSortOption) {
            binding.toolbar.inflateMenu(R.menu.sort_menu)
            binding.toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.sort_name_asc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.NAME_ASC
                    }
                    R.id.sort_name_desc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.NAME_DESC
                    }
                    R.id.sort_size_asc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.SIZE_ASC
                    }
                    R.id.sort_size_desc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.SIZE_DESC
                    }
                    R.id.sort_date_asc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.DATE_MODIFIED_ASC
                    }
                    R.id.sort_date_desc -> {
                        item.isChecked = true
                        options.sortBy = SortBy.DATE_MODIFIED_DESC
                    }
                    else -> options.sortBy = SortBy.NAME_ASC
                }
                refreshFiles()
                true
            }
        }

        binding.files.adapter =
            FileAdapter().apply { setFiles(currentDirectory, listFiles(currentDirectory)) }

        isCancelable = false
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

        WindowCompat.setDecorFitsSystemWindows(dialog?.window!!, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.updatePadding(top = systemBars.top)
            binding.root.updatePadding(
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (currentDirectory.parentFile?.canRead() != true) {
                    Toast.makeText(
                        requireContext(), "Cannot read parent directory", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val parentDir = currentDirectory.parentFile ?: return@setOnKeyListener false
                    currentDirectory = parentDir
                    (binding.files.adapter as FileAdapter).setFiles(parentDir, listFiles(parentDir))
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()
        val files = listFiles(currentDirectory)
        val adapter = binding.files.adapter as FileAdapter
        adapter.setFiles(currentDirectory, files)
    }

    private fun requestManageAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) return
            try {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        "package:${requireContext().packageName}".toUri()
                    )
                )
            } catch (_: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) return
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0
            )
        }
    }

    private fun listFiles(root: File): List<File> {
        val filteredFiles = (root.listFiles()?.toList() ?: emptyList()).filter { file ->
            when (options.selectionMode) {
                SelectionMode.FOLDER -> file.isDirectory
                SelectionMode.BOTH -> {
                    // When selecting both, show all files that match extensions (or all if no extensions specified)
                    if (options.extensions.isEmpty()) true
                    else if (file.isDirectory) true
                    else options.extensions.any { file.extension == it }
                }

                SelectionMode.FILE -> {
                    // Default file-only selection behavior
                    if (options.extensions.isEmpty() || file.isDirectory) true
                    else options.extensions.any { file.extension == it }
                }
            }
        }

        return filteredFiles.sortedWith(
            compareBy<File> { !it.isDirectory }.then(
                when (options.sortBy) {
                    SortBy.NAME_ASC -> compareBy { it.name.lowercase(Locale.getDefault()) }
                    SortBy.NAME_DESC -> compareByDescending { it.name.lowercase(Locale.getDefault()) }
                    SortBy.SIZE_ASC -> compareBy { it.length() }
                    SortBy.SIZE_DESC -> compareByDescending { it.length() }
                    SortBy.DATE_MODIFIED_ASC -> compareBy { it.lastModified() }
                    SortBy.DATE_MODIFIED_DESC -> compareByDescending { it.lastModified() }
                }
            )
        )
    }

    private fun refreshFiles() {
        val files = listFiles(currentDirectory)
        (binding.files.adapter as FileAdapter).setFiles(currentDirectory, files)
    }

    private inner class FileAdapter : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
        private val files = mutableListOf<File>()
        private val selectedFiles: MutableList<File> = mutableListOf()

        private val visibleViewHolders = mutableSetOf<FileViewHolder>()

        @SuppressLint("NotifyDataSetChanged")
        fun setFiles(root: File, files: List<File>) {
            this.files.clear()
            this.files.add(root)
            this.files.addAll(files)
            if (!options.multipleSelection) {
                selectedFiles.clear()
            }
            binding.toolbar.subtitle = currentDirectory.absolutePath
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
            val binding =
                ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FileViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
            visibleViewHolders.add(holder)
            if (position == 0) {
                holder.bind(files[0], true)
            } else holder.bind(files[position])
        }

        override fun onViewRecycled(holder: FileViewHolder) {
            super.onViewRecycled(holder)
            visibleViewHolders.remove(holder)
        }

        override fun getItemCount(): Int = files.size

        inner class FileViewHolder(private val binding: ItemFileBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private var currentFile: File? = null

            @SuppressLint("SetTextI18n")
            fun bind(file: File, up: Boolean = false) {
                currentFile = file

                if (up) {
                    if (file.parentFile?.canRead() != true) {
                        // changing view visibility doesn't work lol
                        itemView.isVisible = false
                        itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    }

                    binding.iconContainer.isVisible = true

                    binding.name.text = ".."
                    binding.details.text = "Parent Directory"
                    binding.checkbox.visibility = View.GONE
                    binding.root.setOnClickListener {
                        if (file.parentFile?.canRead() != true) {
                            Toast.makeText(
                                binding.root.context,
                                "Cannot read parent directory",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        val parentDir = currentDirectory.parentFile ?: return@setOnClickListener
                        currentDirectory = parentDir
                        setFiles(parentDir, listFiles(parentDir))
                    }
                } else {
                    if (file.isDirectory) {
                        binding.iconContainer.setCardBackgroundColor(
                            MaterialColors.getColor(
                                binding.iconContainer,
                                com.google.android.material.R.attr.colorSurfaceContainerHigh
                            )
                        )
                        binding.icon.setImageResource(R.drawable.outline_folder_24)
                    } else {
                        binding.iconContainer.setCardBackgroundColor(Color.TRANSPARENT)
                        binding.icon.setImageResource(R.drawable.outline_insert_drive_file_24)
                    }

                    binding.name.text = file.name
                    binding.details.text = SimpleDateFormat(
                        "dd-mm-yyyy", Locale.getDefault()
                    ).format(Date(file.lastModified()))

                    if (file.isFile) binding.details.text =
                        binding.details.text.toString() + " | " + getSize(file)

                    // Set checkbox visibility based on selection mode
                    when (options.selectionMode) {
                        SelectionMode.BOTH -> {
                            // When selecting both, show checkbox for all items
                            binding.checkbox.visibility = View.VISIBLE
                        }

                        SelectionMode.FOLDER -> {
                            // When selecting folders only, show checkbox only for directories
                            binding.checkbox.visibility =
                                if (file.isDirectory) View.VISIBLE else View.GONE
                        }

                        SelectionMode.FILE -> {
                            // When selecting files only, show checkbox only for files
                            binding.checkbox.visibility =
                                if (!file.isDirectory) View.VISIBLE else View.GONE
                        }
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
                            currentDirectory = file
                            setFiles(file, listFiles(file))
                        }
                    } else {
                        binding.root.setOnClickListener {}
                    }

                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = (file in selectedFiles)

                    binding.root.setOnClickListener {
                        updateSelection(file)
                    }
                }
            }

            fun updateCheckboxState() {
                currentFile?.let { file ->
                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = file in selectedFiles
                }
            }
        }

        private fun updateSelection(newSelection: File) {
            if (!options.multipleSelection) {
                selectedFiles.clear()
            }
            if (newSelection in selectedFiles) {
                selectedFiles.remove(newSelection)
            } else {
                selectedFiles.add(newSelection)
            }
            visibleViewHolders.forEach { holder ->
                holder.updateCheckboxState()
            }
        }

        private fun getSize(file: File): String {
            if (file.isDirectory) {
                return "Directory"
            }
            val size = if (file.isDirectory) {
                file.walk().sumOf { it.length() }
            } else {
                file.length()
            }
            val units = arrayOf("B", "KiB", "MiB", "GiB", "TiB")
            var unit = 0
            var sizeD = size.toDouble()
            while (sizeD > 1024 && unit < units.size) {
                sizeD /= 1024
                unit++
            }
            return String.format(Locale.getDefault(), "%.2f %s", sizeD, units[unit])
        }

        fun getSelectedFiles(): List<File> = selectedFiles
    }
}
