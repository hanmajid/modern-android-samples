package com.hanmajid.android.rolemanager

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    private val roleManager: RoleManager by lazy {
        getSystemService(Context.ROLE_SERVICE) as RoleManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prepare Intent launcher.
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    refreshUI()
                    showToast("Success requesting ROLE_BROWSER!")
                } else {
                    showToast("Failed requesting ROLE_BROWSER")
                }
            }
    }

    override fun onResume() {
        super.onResume()

        // Refresh UI.
        refreshUI()
    }

    /**
     * Refresh UI elements.
     */
    private fun refreshUI() {
        val isRoleAvailable = roleManager.isRoleAvailable(role)
        findViewById<TextView>(R.id.text_is_role_available).text =
            if (isRoleAvailable) "TRUE" else "FALSE"

        val isHoldingRole = roleManager.isRoleHeld(role)
        findViewById<TextView>(R.id.text_is_holding_role).text =
            if (isHoldingRole) "TRUE" else "FALSE"
        findViewById<Button>(R.id.button_request_role).apply {
            isEnabled = isRoleAvailable && !isHoldingRole
            setOnClickListener {
                requestRole()
            }
        }
    }

    /**
     * Request user to grant this app with the [role].
     */
    private fun requestRole() {
        if (roleManager.isRoleAvailable(role)) {
            if (!roleManager.isRoleHeld(role)) {
                intentLauncher.launch(
                    roleManager.createRequestRoleIntent(role)
                )
            } else {
                showToast("ROLE_BROWSER is already held by this app.")
            }
        } else {
            showToast("ROLE_BROWSER doesn't exist.")
        }
    }

    /**
     * Shows [message] in a Toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    companion object {
        // The requested role.
        const val role = RoleManager.ROLE_BROWSER
    }
}