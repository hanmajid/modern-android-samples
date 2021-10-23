package com.hanmajid.yggr.android.security.publickeycryptography

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate key pair
        SecurityUtil.getKeyPair()

        // Display public key in the Logcat
        val publicKey = SecurityUtil.getPublicKey()
        Log.wtf("PUBLIC KEY", publicKey)

        // Display private key in the Logcat
        val privateKey = SecurityUtil.getPrivateKey()
        Log.wtf("PRIVATE KEY", privateKey.toString()) // does not support encoding
    }
}