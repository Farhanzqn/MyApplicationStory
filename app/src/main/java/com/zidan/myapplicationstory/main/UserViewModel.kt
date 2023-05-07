package com.zidan.myapplicationstory.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zidan.myapplicationstory.data.DataPreferences
import com.zidan.myapplicationstory.login.LoginUser
import com.zidan.myapplicationstory.register.RegisterResponse
import com.zidan.myapplicationstory.register.RegisterUser
import com.zidan.myapplicationstory.repo.UserRepository
import com.zidan.myapplicationstory.response.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: DataPreferences
) : ViewModel() {

    val isLoading: LiveData<Boolean> = userRepository.loading
    val userStatus: LiveData<Boolean> = userRepository.userStatus
    val loginResult: LiveData<LoginResult> = userRepository.loginData
    val signUpResult: LiveData<RegisterResponse> = userRepository.registerResponse
    val snackbar : LiveData<String> = userRepository.snackbarText

    fun userLogin(loginUser: LoginUser) {
        userRepository.userLogin(loginUser)
    }

    fun userRegister(registerUser: RegisterUser) {
        userRepository.userRegister(registerUser)
    }

    fun getDataSession(): LiveData<LoginResult> {
        return preference.getDataSetting().asLiveData()
    }

    fun saveDataSession(dataSetting: LoginResult) {
        viewModelScope.launch {
            preference.saveDataSetting(dataSetting)
        }
    }

    fun clearDataSession() {
        viewModelScope.launch {
            preference.clearDataSetting()
        }
    }
}