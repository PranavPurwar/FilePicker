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

    /**
     * Sets the selection mode for the file picker.
     * @param mode The selection mode to use.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setSelectionMode(mode: SelectionMode): FilePickerOptions {
        this.selectionMode = mode
        return this
    }

    /**
     * Sets the allowed file extensions for selection.
     * @param extensions An array of allowed file extensions.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setExtensions(extensions: Array<String>): FilePickerOptions {
        this.extensions = extensions
        return this
    }

    /**
     * Sets the title of the file picker dialog.
     * @param title The title to be displayed in the file picker dialog.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setTitle(title: String?): FilePickerOptions {
        this.title = title
        return this
    }

    /**
     * Sets whether the file picker should allow multiple file selection.
     * @param multipleSelection True if the picker should allow multiple selections, false otherwise.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setMultipleSelection(multipleSelection: Boolean): FilePickerOptions {
        this.multipleSelection = multipleSelection
        return this
    }

    /**
     * Sets the sorting method for files in the picker.
     * @param sortBy The sorting method to use.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setSortBy(sortBy: SortBy): FilePickerOptions {
        this.sortBy = sortBy
        return this
    }

    /**
     * Sets whether to show the sort option UI in the picker.
     * @param showSortOption True to show sorting options, false to hide them.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setShowSortOption(showSortOption: Boolean): FilePickerOptions {
        this.showSortOption = showSortOption
        return this
    }

    /**
     * Sets the initial directory for the file picker.
     * @param initialDirectory The path of the initial directory to open.
     * @return The current instance of [FilePickerOptions] for method chaining.
     */
    fun setInitialDirectory(initialDirectory: String?): FilePickerOptions {
        this.initialDirectory = initialDirectory
        return this
    }
}
