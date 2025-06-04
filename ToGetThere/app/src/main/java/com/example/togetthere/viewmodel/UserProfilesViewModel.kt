package com.example.togetthere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.togetthere.data.repository.UserProfileRepository

class UserProfilesViewModel(val model: UserProfileRepository): ViewModel() {

   // val allUsers = model.users

    companion object {
        fun provideFactory(userProfileRepository: UserProfileRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserProfilesViewModel(userProfileRepository) as T
                }
            }
    }



//
//    private val _allUserProfiles = MutableStateFlow<List<UserProfile>>(listOf(PaoloProfile, LauraProfile, AndreProfile, SofiaProfile, LiamProfile, AmiraProfile, HugoProfile, YukiProfile))
//    val allUserProfiles: StateFlow<List<UserProfile>> = _allUserProfiles.asStateFlow()
//
//    private val _userProfile = MutableStateFlow<UserProfile?>(null)
//    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
//
//    // Editable fields
//    val name = MutableStateFlow("")
//    val surname = MutableStateFlow("")
//    val nickname = MutableStateFlow("")
//    val gender = MutableStateFlow(GenderType.OTHER)
//    val nationality = MutableStateFlow("")
//    val description = MutableStateFlow("")
//    val photo = MutableStateFlow<Int?>(null)
//    val interests = MutableStateFlow<List<String>>(emptyList())
//    val socials = MutableStateFlow<List<SocialHandle>>(emptyList())
//
//    // Validation errors
//    val nameError = MutableStateFlow<String?>(null)
//    val nicknameError = MutableStateFlow<String?>(null)
//    val descriptionError = MutableStateFlow<String?>(null)
//
//    fun loadUserProfile(userId: Int) {
//        val profile = allUserProfiles.value.find { it.userId == userId }
//        _userProfile.value = profile
//
//        profile?.let {
//            name.value = it.name
//            surname.value = it.surname
//            nickname.value = it.nickname
//            gender.value = it.gender
//            nationality.value = it.nationality
//            description.value = it.description
//            photo.value = it.photo
//            interests.value = it.interests
//            socials.value = it.socials
//        }
//    }
//
//
//    /************************************* CREAZIONE ***************************************/
//
//    private val _isCreating = MutableStateFlow(false)
//    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()
//
//    fun startCreatingNewUser() {
//        _isCreating.value = true
//
//        // Reset dei campi
//        name.value = ""
//        surname.value = ""
//        nickname.value = ""
//        gender.value = GenderType.OTHER
//        nationality.value = ""
//        description.value = ""
//        photo.value = null
//        interests.value = emptyList()
//        socials.value = emptyList()
//
//        // Nessun userProfile caricato
//        _userProfile.value = null
//    }
//
//    // TODO: aggiungere altra validazione
//    fun validateAndCreate(): UserProfile? {
//        var isValid = true
//
//        // Riutilizziamo la stessa logica di validazione di validateAndSave()
//        if (name.value.length < 2) {
//            nameError.value = "Name at least 2 characters long"
//            isValid = false
//        } else {
//            nameError.value = null
//        }
//
//        if (nickname.value.length < 3) {
//            nicknameError.value = "Nickname at least 2 characters long"
//            isValid = false
//        } else {
//            nicknameError.value = null
//        }
//
//        if (description.value.length > 300) {
//            descriptionError.value = "Description max 300 characters long"
//            isValid = false
//        } else {
//            descriptionError.value = null
//        }
//
//        if (!isValid) return null
//
//        // Generazione ID fittizia
//        val newUserId = (_allUserProfiles.value.maxOfOrNull { it.userId } ?: 0) + 1
//
//        val newProfile = UserProfile(
//            userId = newUserId,
//            name = name.value,
//            surname = surname.value,
//            nickname = nickname.value,
//            gender = gender.value,
//            nationality = nationality.value,
//            description = description.value,
//            photo = photo.value,
//            interests = interests.value,
//            socials = socials.value,
//            tripsCreated = emptyList(),
//            reviews = emptyList()
//        )
//
//        // Qui potresti aggiungerlo a un repository, salvarlo o restituirlo
//        val updatedList = _allUserProfiles.value + newProfile
//        _allUserProfiles.value = updatedList
//
//        return newProfile
//    }
//
//
//
//    /************************************ MODIFICA *************************************/
//
//    // TODO: aggiungere altra validazione
//    fun validateAndSave(): Boolean {
//        var isValid = true
//
//        if (name.value.length < 2) {
//            nameError.value = "Name at least 2 characters long"
//            isValid = false
//        } else {
//            nameError.value = null
//        }
//
//        if (nickname.value.length < 3) {
//            nicknameError.value = "Nickname at least 3 characters long"
//            isValid = false
//        } else {
//            nicknameError.value = null
//        }
//
//        if (description.value.length > 300) {
//            descriptionError.value = "Description max 300 characters"
//            isValid = false
//        } else {
//            descriptionError.value = null
//        }
//
//        if (isValid) {
//            // Ricrea lo UserProfile aggiornato
//            _userProfile.value = _userProfile.value?.copy(
//                name = name.value,
//                surname = surname.value,
//                nickname = nickname.value,
//                gender = gender.value,
//                nationality = nationality.value,
//                description = description.value,
//                photo = photo.value,
//                interests = interests.value,
//                socials = socials.value
//            )
//        }
//
//        return isValid
//    }
}
