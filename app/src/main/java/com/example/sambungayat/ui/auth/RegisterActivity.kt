package com.example.sambungayat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sambungayat.databinding.ActivityRegisterBinding
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.SessionManager
import com.example.sambungayat.network.repository.AuthRepository
import com.example.sambungayat.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val repository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener { attemptRegister() }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun attemptRegister() {
        val name     = binding.etName.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm  = binding.etConfirmPassword.text.toString()

        var valid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Nama tidak boleh kosong"; valid = false
        } else binding.tilName.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"; valid = false
        } else binding.tilEmail.error = null

        if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"; valid = false
        } else binding.tilPassword.error = null

        if (password != confirm) {
            binding.tilConfirmPassword.error = "Password tidak sama"; valid = false
        } else binding.tilConfirmPassword.error = null

        if (!valid) return

        setLoading(true)

        lifecycleScope.launch {
            when (val result = repository.register(name, email, password)) {
                is ApiResult.Success -> {
                    val data = result.data
                    sessionManager.saveSession(data.userId ?: 0, data.username ?: name)
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                is ApiResult.Error -> {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }
}
