package com.loginwithfacebook

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.loginwithfacebook.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    var callbackManager: CallbackManager = CallbackManager.Factory.create();
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        printHashKey(applicationContext)

        HashKey.printHashKey(applicationContext);

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.userId?.let { Log.d("$TAG onSuccess", it) }

                    object : ProfileTracker(){
                        override fun onCurrentProfileChanged(
                            oldProfile: Profile?,
                            currentProfile: Profile?
                        ) {
                            currentProfile?.let {
                                binding.textView.text = "${it.firstName}  ${it.lastName}  ${it.linkUri}"
                            }

                        }

                    }
                }

                override fun onCancel() {
                    Log.d("$TAG onCancel", "onCancel")
                }

                override fun onError(error: FacebookException?) {
                    error?.message?.let { Log.d("$TAG onError", it) }
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object{
        const val TAG = "MainActivity"
    }
}

fun printHashKey(pContext: Context) {

    try {
        val info: PackageInfo = pContext.packageManager
            .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            val hashKey: String = String(Base64.encode(md.digest(), 0))
            Log.i("HashKey", "printHashKey() Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        Log.e("HashKey", "printHashKey()", e)
    } catch (e: Exception) {
        Log.e("HashKey", "printHashKey()", e)
    }
}