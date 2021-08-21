package com.hanmajid.yggr.android.simpleformvalidation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.hanmajid.yggr.android.simpleformvalidation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.loginForm = loginViewModel.loginForm

        binding.submitButton.setOnClickListener {
            if (loginViewModel.loginForm.isValid()) {
                Toast.makeText(this, "Form valid!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Form NOT valid!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}