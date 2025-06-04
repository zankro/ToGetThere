package com.example.togetthere.model

//import com.example.togetthere.data.tripReview1
//import com.example.togetthere.data.tripReview2
//import com.example.togetthere.data.tripReview3
//import com.example.togetthere.data.tripReview4
//import com.example.togetthere.data.tripReview5
//import com.example.togetthere.data.tripReview6
//import com.example.togetthere.data.tripReview7
//import com.example.togetthere.data.tripReview8
//import com.example.togetthere.data.tripReview9
//import com.example.togetthere.data.tripReview10

class TripReview(
    val id: Int = 0,
    val score: Int = 5,
    val title: String = "",
    val description: String = "",
    val author: String = "", // userId
    val photos: List<TripPhoto> = listOf(),
)

data class UserReview(
    val id: Int = 0,
    val author: String = "",
    val description: String = "",
    val receiverId: String = "",
    val createdAt: Long = 0L
)

//class ReviewRepository {
//    val reviews = listOf(tripReview1, tripReview2, tripReview3, tripReview4, tripReview5, tripReview6, tripReview7, tripReview8, tripReview9, tripReview10)
//}

