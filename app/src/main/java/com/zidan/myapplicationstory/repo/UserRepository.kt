package com.zidan.myapplicationstory.repo


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zidan.myapplicationstory.api.ApiService
import com.zidan.myapplicationstory.login.LoginUser
import com.zidan.myapplicationstory.register.RegisterResponse
import com.zidan.myapplicationstory.register.RegisterUser
import com.zidan.myapplicationstory.response.LoginResponse
import com.zidan.myapplicationstory.response.LoginResult
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
) {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean> = _userStatus

    private val _loginData = MutableLiveData<LoginResult>()
    val loginData: LiveData<LoginResult> = _loginData

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _signinResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _signinResponse

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    fun userLogin(signInBody: LoginUser) {
        _loading.value = true
        val client = apiService.login(signInBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true

                    Log.d("ResponseApi", "onResponse: ${responseBody.loginResult}")
                    _loginData.value = responseBody.loginResult

                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loading.value = false
                _snackbarText.value = t.message
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }

    fun userRegister(signUpBody: RegisterUser) {
        _loading.value = true
        val client = apiService.register(signUpBody)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true
                    Log.d("ResponseAPI", "onResponse: ${responseBody.message}")
                    _snackbarText.value = "Daftar"

                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }

            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false
                _snackbarText.value = t.message
                Log.d("ResponseAPI", "onFailure: ${t.message}")
            }

        })
    }

}