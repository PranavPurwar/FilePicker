package dev.pranav.filepicker

/**
 * Defines the sorting options for files in the file picker
 */
enum class SortBy {
    NAME_ASC,         // Sort by name (A-Z)
    NAME_DESC,        // Sort by name (Z-A)
    SIZE_ASC,         // Sort by size (smallest first)
    SIZE_DESC,        // Sort by size (largest first)
    DATE_MODIFIED_ASC,// Sort by date modified (oldest first)
    DATE_MODIFIED_DESC// Sort by date modified (newest first)
}

/**
 * Defines the selection mode for the file picker
 */
enum class SelectionMode {
    FILE,    // Select only files
    FOLDER,  // Select only folders
    BOTH     // Select both files and folders
}

/**
 * Options for the file picker dialog.
 *
 * @property selectionMode Mode of selection (files, folders, or both)
 * @property extensions Allowed file extensions for selection.
 * @property title Title of the file picker dialog.
 * @property multipleSelection Whether to allow multiple file selection.
 * @property sortBy How files should be sorted in the picker.
 * @property showSortOption Whether to show the sort option UI in the picker.
 */
class FilePickerOptions {
    var selectionMode = SelectionMode.FILE
    var extensions = emptyArray<String>()
    var title: String? = null
    var multipleSelection = false
    var sortBy = SortBy.NAME_ASC
    var showSortOption = true
    var initialDirectory: String? = null
}
