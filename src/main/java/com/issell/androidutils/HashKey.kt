package com.issell.androidutils

import android.content.Context
import java.security.MessageDigest
import android.content.pm.PackageManager
import android.os.Build
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*

const val TAG = "HashKey"
class SignatureUtils{
    companion object{
        @Nullable
        fun getHashKey(@NotNull context: Context):String? {
            val list = getApplicationSignature(context)
            if(list.isEmpty())
                return null
            if(list.size == 1)
                return list[0]
            return list[list.size-1]
        }


        fun getApplicationSignature(
            context: Context,
            packageName: String = context.packageName
        ): List<String> {
            val signatureList: List<String>
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // New signature
                    val sig = context.packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    ).signingInfo
                    signatureList = if (sig.hasMultipleSigners()) {
                        // Send all with apkContentsSigners
                        sig.apkContentsSigners.map {
                            val digest = MessageDigest.getInstance("SHA")
                            digest.update(it.toByteArray())
                            // bytesToHex(digest.digest())
                            Base64.getEncoder().encodeToString(digest.digest())
                        }
                    } else {
                        // Send one with signingCertificateHistory
                        sig.signingCertificateHistory.map {
                            val digest = MessageDigest.getInstance("SHA")
                            digest.update(it.toByteArray())
                            //bytesToHex(digest.digest())  안되면 이거랑 아랫것과 교체
                            Base64.getEncoder().encodeToString(digest.digest())
                        }
                    }
                } else { // under P
                    val sig =
                        context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                            .signatures
                    signatureList = sig.map {
                        val digest = MessageDigest.getInstance("SHA")
                        digest.update(it.toByteArray())
                        // bytesToHex(digest.digest())
                        Base64.getEncoder().encodeToString(digest.digest())
                    }
                }

                return signatureList
            } catch (e: Exception) {
                // Handle error
            }
            return emptyList()
        }

//fun bytesToHex(bytes: ByteArray): String {
//    val hexArray =
//        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
//    val hexChars = CharArray(bytes.size * 2)
//    var v: Int
//    for (j in bytes.indices) {
//        v = bytes[j].toInt() and 0xFF
//        hexChars[j * 2] = hexArray[v.ushr(4)]
//        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
//    }
//    return String(hexChars)
//}

    }
}