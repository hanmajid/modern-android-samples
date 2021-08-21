package com.hanmajid.yggr.android.simpleformvalidation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class LoginForm {
    val email = MutableLiveData<String?>(null)
    val errorEmail = Transformations.map(email) {
        when {
            it == null -> {
                ""
            }
            it.isBlank() -> {
                "Please fill in your email"
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() -> {
                "Please fill in a valid email address"
            }
            else -> {
                ""
            }
        }
    }
    val errorEnabledEmail = Transformations.map(errorEmail) {
        it.isNotBlank()
    }

    val password = MutableLiveData<String?>(null)
    val errorPassword = Transformations.map(password) {
        when {
            it == null -> {
                ""
            }
            it.isBlank() -> {
                "Please fill in your password"
            }
            else -> {
                ""
            }
        }
    }
    val errorEnabledPassword = Transformations.map(errorPassword) {
        it.isNotBlank()
    }

    private fun touchForm() {
        val fields = listOf(
            email,
            password,
        )
        for (field in fields) {
            if (field.value == null) {
                field.value = ""
            }
        }
    }

    fun isValid(): Boolean {
        touchForm()
        val isErrors = listOf(
            errorEnabledEmail.value,
            errorEnabledPassword.value,
        )
        return isErrors.all { it == false }
    }
}