package com.example.payment

import android.content.Context
import network.celer.geth.Account
import network.celer.geth.Geth
import network.celer.geth.KeyStore
import java.io.File

class KeyStoreHelper {
    private val createPassword = "CelerNetwork"
    private val exportPassword = "CelerNetwork"
    private var gethKeyStore: KeyStore? = null
    private var account: Account? = null
    private var keyStoreString: String = ""

    fun getKeyStoreString(context: Context): String {
        if (keyStoreString.isEmpty()) {
            generateKeyStoreString(context)
        }
        return keyStoreString
    }

    fun getPassword(): String {
        return exportPassword
    }

    fun generateFilePath(context: Context): String {
        val file = File(context.filesDir.path, "celer")
        if (!file.exists()) {
            file.mkdir()
        }
        return file.path
    }

    private fun generateKeyStore(context: Context) {
        val filePath = generateFilePath(context)
        gethKeyStore = KeyStore(filePath, Geth.LightScryptN, Geth.LightScryptP)
    }

    private fun generateAccount(context: Context) {
        if (gethKeyStore == null) {
            generateKeyStore(context)
        }

        gethKeyStore?.let { gethKeyStore ->
            gethKeyStore.accounts?.let { accounts ->
                account = gethKeyStore.newAccount(createPassword)

            }
        }
    }

    private fun generateKeyStoreString(context: Context) {
        if (account == null) {
            generateAccount(context)
        }

        gethKeyStore?.let { gethKeyStore ->
            account?.let { account ->
                keyStoreString = String(gethKeyStore.exportKey(account, createPassword, exportPassword), Charsets.UTF_8)
            }
        }
    }
}