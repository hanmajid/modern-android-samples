package com.hanmajid.android.androidprotectedconfirmation

import android.os.Build
import android.os.Bundle
import android.security.ConfirmationAlreadyPresentingException
import android.security.ConfirmationCallback
import android.security.ConfirmationNotAvailableException
import android.security.ConfirmationPrompt
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.Executor

data class TransactionData(
    val sender: String,
    val amount: Int,
    val recipient: String,
)

class MainActivity : AppCompatActivity() {
    private val attestationChallenge = "CHALLENGE"
    private val extraData = TransactionData(
        sender = "You",
        amount = 200,
        recipient = "Your Mom"
    )
    private var privateKey: KeyStore.PrivateKeyEntry? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        privateKey = createOrGetPrivateKey(attestationChallenge)

        findViewById<Button>(R.id.button_send_money).setOnClickListener {
            showAndroidProtectedConfirmationPrompt(privateKey, extraData)
        }

        initUI()
    }

    private fun initUI() {
        findViewById<TextView>(R.id.text_attestation_challenge).text = attestationChallenge
        findViewById<TextView>(R.id.text_attestation_certificate).text =
            (privateKey?.certificateChain?.firstOrNull() as? X509Certificate)?.issuerDN?.name
        findViewById<TextView>(R.id.text_extra_data).text = extraData.toString()
    }

    /**
     * Get PrivateKey with alias [KEYSTORE_ALIAS]. If it doesn't exist, create it with the given
     * [attestationChallenge].
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun createOrGetPrivateKey(attestationChallenge: String): KeyStore.PrivateKeyEntry? {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val aliases: Enumeration<String> = ks.aliases()
        if (aliases.toList().firstOrNull { it == KEYSTORE_ALIAS } == null) {
            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore"
            )
            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).run {
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                setUserConfirmationRequired(true)
//                setIsStrongBoxBacked(true)
                setAttestationChallenge(attestationChallenge.toByteArray())
                build()
            }
            kpg.initialize(parameterSpec)

            kpg.generateKeyPair()
        }
        return ks.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.PrivateKeyEntry
    }

    @RequiresApi(Build.VERSION_CODES.P)
    class MyConfirmationCallback(
        private val privateKey: KeyStore.PrivateKeyEntry?,
        private val onResult: (dataThatWasConfirmed: Map<*, *>, signature: String) -> Unit,
        private val onDismiss: () -> Unit,
    ) :
        ConfirmationCallback() {

        override fun onConfirmed(dataThatWasConfirmed: ByteArray) {
            super.onConfirmed(dataThatWasConfirmed)
            val dataThatWasConfirmedJson =
                CBORMapper().readValue(dataThatWasConfirmed, Map::class.java)

            privateKey?.let {
                val signature: ByteArray = Signature.getInstance("SHA256withECDSA").run {
                    initSign(it.privateKey)
                    update(dataThatWasConfirmed)
                    sign()
                }
                onResult(dataThatWasConfirmedJson, signature.toString(Charsets.UTF_8))
            }
        }

        override fun onDismissed() {
            super.onDismissed()
            onDismiss()
        }

        override fun onCanceled() {
            super.onCanceled()
            // Handle case where your app closed the dialog before the user
            // could respond to the prompt.
        }

        override fun onError(e: Throwable?) {
            super.onError(e)
            // Handle the exception that the callback captured.
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showAndroidProtectedConfirmationPrompt(
        privateKey: KeyStore.PrivateKeyEntry?,
        extraData: TransactionData,
    ) {
        val threadReceivingCallback = Executor { runnable ->
            runnable.run()
        }
        val callback = MyConfirmationCallback(privateKey, { dataThatWasConfirmed, signature ->
            val dataThatWasConfirmedFormatted = dataThatWasConfirmed.entries.associate {
                val value = it.value
                if (value is ByteArray) {
                    it.key to value.toString(Charsets.UTF_8)
                } else {
                    it.key to value.toString()
                }
            }
            runOnUiThread {
                findViewById<TextView>(R.id.text_data_that_was_confirmed).text =
                    dataThatWasConfirmedFormatted.toString()
                findViewById<TextView>(R.id.text_signature).text = signature
                Toast.makeText(this, "Transaction confirmed!", Toast.LENGTH_SHORT).show()
            }
        }, {
            runOnUiThread {
                Toast.makeText(this, "User cancel the transaction.", Toast.LENGTH_SHORT).show()
            }
        })

        val dialog = ConfirmationPrompt.Builder(this)
            .setPromptText("Send $200 to your mom?")
            .setExtraData(extraData.toString().toByteArray(Charsets.UTF_8))
            .build()
        try {
            dialog.presentPrompt(threadReceivingCallback, callback)
        } catch (e: ConfirmationNotAvailableException) {
            Toast.makeText(
                this,
                "Android Protected Confirmation is not available in this device.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: ConfirmationAlreadyPresentingException) {
            Toast.makeText(
                this,
                "Android Protected Confirmation is already being presented.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val KEYSTORE_ALIAS = "com.hanmajid.android.androidprotectedconfirmation.key"
    }
}