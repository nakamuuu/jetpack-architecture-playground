package net.divlight.archplayground.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import net.divlight.archplayground.R
import net.divlight.archplayground.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val GITHUB_REPOSITORY_URL = "https://github.com/nakamuuu/jetpack-architecture-playground"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            activity = this@MainActivity
        }
    }

    fun onLaunchButtonClick() {
        startActivity(Intent(this, EditProfileActivity::class.java))
    }

    fun onOpenRepositoryButtonClick() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPOSITORY_URL)))
    }
}
