package com.example.togetthere.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.togetthere.data.repository.ReviewRepository
import com.example.togetthere.data.repository.UserProfileRepository
import com.example.togetthere.model.Destination
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.SocialHandle
import com.example.togetthere.model.UserProfile
import com.example.togetthere.model.UserReview
import kotlinx.coroutines.launch

data class UserReviewWithAuthor(
    val id: Int,
    val authorId: String,
    val authorName: String,
    val authorPhoto: String?,
    val authorGender: GenderType,
    val description: String
)

class ProfileViewModel(
    private val model: UserProfileRepository,
    private val reviewModel: ReviewRepository,
    private val userId: String
) : ViewModel() {
    // val user = mutableStateOf<UserProfile?>(null)
    var uiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    var id = userId
    var userName by mutableStateOf("")
    var userSurname by mutableStateOf("")
    var userNickname by mutableStateOf("")
    var userGender by mutableStateOf(GenderType.OTHER)
    var userNationality by mutableStateOf("")
    var userDescription by mutableStateOf("")
    var userPhoto by mutableStateOf<String?>(null)
    var userInterests by mutableStateOf(emptyList<String>())
    var userReviews by mutableStateOf(emptyList<UserReview>())
    var userSocials by mutableStateOf(emptyList<SocialHandle>())
    var userDreamTrips by mutableStateOf(emptyList<Destination>())

    var isReviewDialogVisible by mutableStateOf(false)
    var reviewDialogDescription by mutableStateOf("")


    var userReviewsWithAuthor by mutableStateOf(emptyList<UserReviewWithAuthor>())
        private set

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            val user = model.getUserById(userId)

            if (user != null) {
                uiState = ProfileUiState.Success(user)

                userName = user.name
                userSurname = user.surname
                userNickname = user.nickname
                userGender = user.gender
                userNationality = user.nationality
                userDescription = user.description
                userPhoto = user.photo
                userInterests = user.interests
                userSocials = user.socials
                userDreamTrips = user.desiredDestinations

                loadReviews()
            } else {
                uiState = ProfileUiState.Error("User not found.")
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            val reviews = reviewModel.getReviewsForUser(userId)
            println("DEBUG review: ${reviews}")

            userReviews = reviews

            userReviewsWithAuthor = reviews.mapNotNull { review ->
                val author = model.getUserById(review.author)
                author?.let {
                    UserReviewWithAuthor(
                        id = review.id,
                        authorId = it.userId,
                        authorName = it.name,
                        authorPhoto = it.photo,
                        authorGender = it.gender,
                        description = review.description
                    )
                }
            }
            println("DEBUG review con autore: ${userReviewsWithAuthor}")
        }
    }


    /************* REVIEWS *******************/
    fun showReviewDialog() {
        if(isReviewDialogVisible) {
            isReviewDialogVisible = false
            reviewDialogDescription = ""
        }else
            isReviewDialogVisible = true
    }

    fun addReview(
        reviewedUserId: String,
        reviewerUserId: String,
        description: String
    ) {
        viewModelScope.launch {
            val nextId = reviewModel.getNextReviewId()

            val newReview = UserReview(
                id = nextId,
                author = reviewerUserId,
                receiverId = reviewedUserId,
                description = description,
                createdAt = System.currentTimeMillis()
            )

            reviewModel.addUserReview(newReview)
            reviewDialogDescription = ""
            loadReviews()
        }
    }

    fun updateReviewDialogDescription(description: String) {
        reviewDialogDescription = description
    }

    fun validateAndSave(onResult: (Boolean) -> Unit) {
        val isValid = validateInputs()

        if (isValid) {
            val editedUser = UserProfile(
                userId = userId,
                name = userName,
                surname = userSurname,
                nickname = userNickname,
                gender = userGender,
                nationality = userNationality,
                description = userDescription,
                photo = userPhoto,
                interests = userInterests,
                desiredDestinations = userDreamTrips,
                socials = userSocials
            )

            viewModelScope.launch {
                model.updateUser(editedUser)
                onResult(true)
            }
        } else {
            onResult(false)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (userName.length < 2) isValid = false
        if (userNickname.length < 3) isValid = false
        if (userDescription.length > 300) isValid = false

        return isValid
    }

//    fun getUserName(): String {
//        return userName
//    }

    companion object {
        fun provideFactory(
            userRepository: UserProfileRepository,
            reviewRepository: ReviewRepository,
            userId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userRepository, reviewRepository, userId) as T
            }
        }
    }
}

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
