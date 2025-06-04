package com.example.togetthere.model

import android.net.Uri
import com.example.togetthere.R

data class SocialHandle(
    val platform: SocialPlatform = SocialPlatform.INSTAGRAM,
    val username: String = "",
)

enum class SocialPlatform (
    val displayName: String = "",
    val iconResIdc: Int = 0,
    val baseUrl: String = ""
) {
    INSTAGRAM("Instagram", R.drawable.ic_instagram, "https://instagram.com"),
    FACEBOOK("Facebook", R.drawable.ic_facebook, "https://facebook.com")
}


data class Destination(
    val name: String = "",
    val imageURL: String = ""
)



enum class GenderType {
    MALE,
    FEMALE,
    OTHER,
}

sealed class UserPhoto {
    data class Resource(val resId: Int) : UserPhoto()
    data class UriPhoto(val uri: Uri) : UserPhoto()
    data class UrlPhoto(val url: String) : UserPhoto()  // <-- nuovo tipo
}


data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val surname: String = "",
    val nickname: String = "",
    val gender: GenderType = GenderType.OTHER,
    val nationality: String = "",
    val description: String = "",
//    val photo: UserPhoto? = UserPhoto.UrlPhoto(""),
    val photo: String? = null,
    val interests: List<String> = listOf(),
    val desiredDestinations: List<Destination> = listOf(),
   // val reviews: List<UserReview> = listOf(),
    val socials: List<SocialHandle> = listOf()
)
//class UserProfileRepository {
//    private val _users = MutableStateFlow(listOf(PaoloProfile, AndreProfile, LauraProfile, SofiaProfile, LiamProfile, AmiraProfile, HugoProfile, YukiProfile, RobertoProfile))
//
//    val users: StateFlow<List<UserProfile>> = _users
//
//    fun getAllUsers(): List<UserProfile> {
//        return _users.value
//    }
//
//    fun getUserById(userId: String): UserProfile? {
//        return _users.value.find { it.userId == userId }
//    }
//
//    fun getUsersByIds(userIds: List<String>): List<UserProfile> {
//        return _users.value.filter { userIds.contains(it.userId) }
//    }
//
//    fun addUser(user: UserProfile) {
//        _users.value += user
//    }
//
//    fun removeUser(userId: String) {
//        _users.value = _users.value.filter { it.userId != userId }
//    }
//
//    fun updateUser(user: UserProfile) {
//        _users.value = _users.value.map { if (it.userId == user.userId) user else it }
//    }
//
//    fun addReviewToUser(
//        targetUserId: String,
//        authorUserId: String,
//        description: String
//    ) {
//        _users.update { list ->
//            list.map { user ->
//                if (user.userId == targetUserId) {
//                    val nextId = user.reviews.size + 1
//                    val review = UserReview(
//                        id = nextId,
//                        author = authorUserId,
//                        description = description
//                    )
//                    user.copy(reviews = user.reviews + review)
//                } else user
//            }
//        }
//    }
//
//}
