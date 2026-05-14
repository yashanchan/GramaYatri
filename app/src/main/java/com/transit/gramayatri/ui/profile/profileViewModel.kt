package com.transit.gramayatri.ui.profile

import androidx.lifecycle.ViewModel
import com.transit.gramayatri.data.repository.userRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfileUi(val name: String, val phone: String)

class ProfileViewModel(private val userRepo: userRepo) : ViewModel() {

    private val _profile = MutableStateFlow(
        UserProfileUi(userRepo.getUserName(), userRepo.getUserPhone())
    )
    val profile: StateFlow<UserProfileUi> = _profile.asStateFlow()

    fun updateProfile(name: String, phone: String) {
        userRepo.saveProfile(name, phone)
        _profile.value = UserProfileUi(name, phone)
    }

    fun reload() {
        _profile.value = UserProfileUi(userRepo.getUserName(), userRepo.getUserPhone())
    }
}