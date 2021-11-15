package com.yuchen.howyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}