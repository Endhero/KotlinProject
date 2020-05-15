package com.lcd.kotlinproject.utils.http

import android.text.TextUtils
import android.util.Log
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.CentralOGManagerApplication
import com.lcd.kotlinproject.data.model.remote.request.RefreshRequest
import com.lcd.kotlinproject.data.model.remote.respose.LoginData
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.AccountAPI
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import java.io.IOException

/**
 * Created by Chen on 2020/2/24.
 */
class TokenAuthenticator : Authenticator {
    private val preferenceSource: PreferenceSource = PreferenceSource()
    private var count = 0

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        var token: String?
        do {
            token = authorization
            if (!TextUtils.isEmpty(token)) {
                return response.request().newBuilder()
                    .header("Authorization", token)
                    .build()
            }
        } while (count < 2)
        return null
    }

    @get:Throws(IOException::class)
    private val authorization: String?
        private get() {
            val accountAPI: AccountAPI = ServiceGenerator.createService<AccountAPI>(AccountAPI::class.java)
            val call: Call<Ret<LoginData?>> = accountAPI.refreshCallable(
                RefreshRequest(preferenceSource.getAppId(), preferenceSource.getRefreshToken())
            )
            val ret: Ret<LoginData?>? = call.execute().body()
            Log.d(TAG, "getToken: $ret")
            count++
            return if (ret != null && ret.success) {
                val data: LoginData = ret.data!!
                preferenceSource.setLoginInfo(data.accessToken!!, data.refreshToken!!, data.expires)
                preferenceSource.getAuthorization()
            } else {
                null
            }
        }

    companion object {
        private const val TAG = "TokenAuthenticator"
    }
}