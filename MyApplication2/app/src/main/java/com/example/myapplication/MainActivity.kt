package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.okta.oidc.*
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.security.DefaultEncryptionManager
import com.okta.oidc.util.AuthorizationException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var webAuth : WebAuthClient
    private lateinit var sessionClient : SessionClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupWebAuth()
        setupWebAuthCallback(webAuth)
        signIn.setOnClickListener {
            val playload = AuthenticationPayload.Builder()
                    .build()
            webAuth.signIn(this, playload)
        }

        getProfile.setOnClickListener {
            downloadProfile()
        }

        signOut.setOnClickListener {
            webAuth.signOutOfOkta(this)
        }
    }

//        signOut.setOnClickListener {
//            webAuth.signOut(this,object : RequestCallback<Int,AuthorizationException>{
//                override fun onSuccess(result: Int) { //6
//                    if (result == BaseAuth.SUCCESS) { //0
//                        Toast.makeText(this@MainActivity,"SIGNED OUT ALL",Toast.LENGTH_SHORT).show()
//                        Log.d("MainActivity","SIGNED_OUT")
//                    }
//                    if (result == BaseAuth.FAILED_CLEAR_SESSION) {  //8
//                        Toast.makeText(this@MainActivity,"FAILED CLEAR SESSION",Toast.LENGTH_SHORT).show()
//                        Log.d("MainActivity","FAILED CLEAR SESSION")
//                    }
//                    if (result == BaseAuth.FAILED_REVOKE_ACCESS_TOKEN) {  //1
//                        Toast.makeText(this@MainActivity,"FAILED REVOKE ACCESS TOKEN",Toast.LENGTH_SHORT).show()
//                        Log.d("MainActivity","FAILED_REVOKE_ACCESS_TOKEN")
//                    }
//                    if (result == BaseAuth.FAILED_REVOKE_REFRESH_TOKEN) {  //2
//                        Toast.makeText(this@MainActivity,"failed_revoke_refresh_token",Toast.LENGTH_SHORT).show()
//                        Log.d("MainActivity","FAILED_REVOKE_REFRESH_TOKEN")
//                    }
//                    if (result == BaseAuth.FAILED_CLEAR_DATA) {  //4
//                        Toast.makeText(this@MainActivity,"failed_clear_data",Toast.LENGTH_SHORT).show()
//                        Log.d("MainActivity","FAILED_CLEAR_DATA")
//                    }
//                }
//
//                override fun onError(msg: String?, error: AuthorizationException?) {
//                    Log.d("MainActivity","${error?.error} onError",error)
//                    Toast.makeText(this@MainActivity,error?.toJsonString(),Toast.LENGTH_SHORT).show()
//                }
//
//            })
//        }

    private fun setupWebAuth() {
        val oidcConfig = OIDCConfig.Builder()
            .clientId("0oa1bmnt60n75BUKJ5d6")
            .redirectUri("com.okta.dev-4626807:/callback")
            .endSessionRedirectUri("com.okta.dev-4626807:/")
            .scopes("openid","profile","offline_access")
            .discoveryUri("https://dev-4626807.okta.com")
            .create()
        webAuth = Okta.WebAuthBuilder()
            .withConfig(oidcConfig)
            .withContext(applicationContext)
            .withCallbackExecutor(null)
            .withEncryptionManager(DefaultEncryptionManager(this))
            .setRequireHardwareBackedKeyStore(true)
            .create()
        sessionClient = webAuth.sessionClient
    }

    private fun setupWebAuthCallback(webAuth: WebAuthClient) {
        val callback : ResultCallback<AuthorizationStatus,AuthorizationException> =
            object : ResultCallback<AuthorizationStatus,AuthorizationException> {
                override fun onSuccess(status: AuthorizationStatus) {
                    if (status== AuthorizationStatus.AUTHORIZED) {
                        Log.d("MainActivity","AUTHORIZED")
                        Toast.makeText(this@MainActivity,"AUTHORIZED",Toast.LENGTH_SHORT).show()

                    } else if (status==AuthorizationStatus.SIGNED_OUT){
                        Log.d("MainActivity","SIGNED_OUT")
                        Toast.makeText(this@MainActivity,"SIGNED_OUT",Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancel() {
                    Log.d("MainActivity","CANCELED")
                    Toast.makeText(this@MainActivity,"CANCELED",Toast.LENGTH_SHORT).show()
                }

                override fun onError(msg: String?, error: AuthorizationException?) {
                    Log.d("MainActivity","${error?.error} onError",error)
                    Toast.makeText(this@MainActivity,error?.toJsonString(),Toast.LENGTH_SHORT).show()


                }
            }
        webAuth.registerCallback(callback,this)
    }

    private fun downloadProfile() {
        sessionClient.getUserProfile(object : RequestCallback<UserInfo, AuthorizationException> {
            override fun onSuccess(result: UserInfo) {
                Log.d("Profile", result.toString())
            }

            override fun onError(error: String, exception: AuthorizationException) {
                Log.d("Profile", error, exception.cause)

            }
        })
    }
}
