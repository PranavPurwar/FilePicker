package dev.pranav.filepicker

import java.io.File

/**
 * Callback interface for file picker events.
 * This interface provides methods to handle file selection, multiple file selection,
 * and cancellation of the file selection process.
 */
open class FilePickerCallback {
    /**
     * Callback for when a file is selected.
     * @param file The selected file.
     */
    open fun onFileSelected(file: File) = Unit

    /**
     * Callback for when multiple files are selected.
     * @param files The list of selected files.
     */
    open fun onFilesSelected(files: List<File>) = Unit

    /**
     * Callback for when the file selection is cancelled.
     * @return True to continue with the cancellation, false to prevent it.
     */
    open fun onFileSelectionCancelled() = true
}
