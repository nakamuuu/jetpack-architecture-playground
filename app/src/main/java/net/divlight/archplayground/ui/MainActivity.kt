package net.divlight.archplayground.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import net.divlight.archplayground.R
import net.divlight.archplayground.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            activity = this@MainActivity
        }
    }

    fun onLaunchButtonClick() {
        startActivity(Intent(this, EditProfileActivity::class.java))
    }
}
