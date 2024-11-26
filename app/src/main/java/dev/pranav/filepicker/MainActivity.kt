package dev.pranav.filepicker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.pranav.filepicker.databinding.ActivityMainBinding
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        enableEdgeToEdge()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let { uri ->
            val path = uri.path
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show()
        }
    }
}
