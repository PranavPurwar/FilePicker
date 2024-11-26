package dev.pranav.filepicker.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dev.pranav.filepicker.FilePickerCallback
import dev.pranav.filepicker.FilePickerDialogFragment
import dev.pranav.filepicker.FilePickerOptions
import dev.pranav.filepicker.sample.databinding.ActivityMainBinding
import java.io.File
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dialog = FilePickerDialogFragment(
            FilePickerOptions(),
            object : FilePickerCallback() {
                override fun onFileSelected(f: File) {
                    super.onFileSelected(f)
                    Toast.makeText(this@MainActivity, f.absolutePath, Toast.LENGTH_SHORT).show()
                }

                override fun onFileSelectionCancelled(): Boolean {
                    super.onFileSelectionCancelled()
                    Toast.makeText(this@MainActivity, "Selection canceled", Toast.LENGTH_SHORT)
                        .show()
                    return super.onFileSelectionCancelled()
                }
            }
        )

        Timer().schedule(object : TimerTask() {
            override fun run() {
                dialog.show(supportFragmentManager, "file_picker")
            }
        }, 1000)
    }
}
