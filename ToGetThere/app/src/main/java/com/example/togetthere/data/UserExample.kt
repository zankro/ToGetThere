package com.example.togetthere.data

import com.example.togetthere.model.Destination
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.SocialHandle
import com.example.togetthere.model.SocialPlatform
import com.example.togetthere.model.UserProfile

var PaoloProfile = UserProfile(
    userId = "60DCjfz7ouUTxAsySkatlT6xzt13",
    name = "Paolo",
    surname = "Michelotti",
    nickname = "paul_baseball",
    gender = GenderType.MALE,
    nationality = "Italy",
    description = "Nice to meet you, I’m Paolo and I enjoy immersing myself in local traditions and capturing moments along the way.",
//    photo = UserPhoto.Resource(R.drawable.paolo),
    photo = "https://firebasestorage.googleapis.com/v0/b/togetthere-cfd4b.firebasestorage.app/o/paolo.jpg?alt=media&token=6cfa34d5-cb99-4922-98fa-00110d622ea4",
    interests = listOf("Adventure", "Art"),
    desiredDestinations = listOf(
        Destination("Santorini",
            "https://images.pexels.com/photos/161815/santorini-oia-greece-water-161815.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"),
        Destination("Rome",
            "https://images.pexels.com/photos/2760519/pexels-photo-2760519.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"),
        Destination("Maldives",
            "https://images.pexels.com/photos/1430677/pexels-photo-1430677.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"),
        Destination("Iceland",
            "https://images.pexels.com/photos/953182/pexels-photo-953182.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
    ),
   // reviews = listOf(userReviewKatmandu, userReviewBuenosAires, userReviewTokyo, userReviewRome),
    socials = listOf(SocialHandle(SocialPlatform.INSTAGRAM, username = "michepaolo"), SocialHandle(SocialPlatform.FACEBOOK, username = "Paolo Michelotti"))
)

//var AndreProfile = UserProfile(
//    userId = "1",
//    name = "Andrea",
//    surname = "Vaglio",
//    nickname = "_andre_99",
//    gender = GenderType.OTHER,
//    nationality = "Germany",
//    description = "Adventure seeker with a love for exploring new cultures and cuisines. Always looking for my next destination to discover hidden gems and unforgettable experiences.",
//    photo = UserPhoto.Resource(R.drawable.andre),
//    interests = listOf("Nature", "Culinary"),
//    desiredDestinations = listOf(
//        Destination("Tokyo",
//            "https://images.pexels.com/photos/1510595/pexels-photo-1510595.jpeg?auto=compress&cs=tinysrgb&w=600".toUri()),
//        Destination("Bangkok",
//            "https://images.pexels.com/photos/50689/skytrain-thailand-transportation-sky-50689.jpeg?auto=compress&cs=tinysrgb&w=600".toUri())
//    ),
//    reviews = listOf(userReviewRome),
//    socials = listOf(),
//)
//
//var LauraProfile = UserProfile(
//    userId = "2",
//    name = "Laura",
//    surname = "Villanova",
//    nickname = "laure25",
//    gender = GenderType.FEMALE,
//    nationality = "Spanish",
//    description = "Wanderlust-filled traveler who's passionate about sustainable travel. I believe every journey teaches you something new. Join me as I explore the world.",
//    photo = UserPhoto.Resource(R.drawable.laura),
//    interests = listOf("Backpack", "Nature", "Interrail"),
//    desiredDestinations = listOf(
//        Destination("Paris",
//            "https://images.pexels.com/photos/1308940/pexels-photo-1308940.jpeg?auto=compress&cs=tinysrgb&w=600".toUri()),
//        Destination("Marrakech",
//            "https://images.pexels.com/photos/4502969/pexels-photo-4502969.jpeg?auto=compress&cs=tinysrgb&w=600".toUri())
//    ),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var SofiaProfile = UserProfile(
//    userId = "3",
//    name = "Sofia",
//    surname = "Bianchi",
//    nickname = "sofie_travels",
//    gender = GenderType.FEMALE,
//    nationality = "Italy",
//    description = "Travel photographer with a soft spot for mountain villages and off-the-beaten-path destinations. Always ready for the next sunrise shot.",
//    photo = null,
//    interests = listOf("Photography", "Hiking", "Culture"),
//    desiredDestinations = listOf(
//        Destination("Paris",
//        "https://images.pexels.com/photos/1308940/pexels-photo-1308940.jpeg?auto=compress&cs=tinysrgb&w=600".toUri()),
//        Destination("Tokyo",
//            "https://images.pexels.com/photos/1510595/pexels-photo-1510595.jpeg?auto=compress&cs=tinysrgb&w=600".toUri()),
//        ),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var LiamProfile = UserProfile(
//    userId = "4",
//    name = "Liam",
//    surname = "Müller",
//    nickname = "liam_ontheroad",
//    gender = GenderType.MALE,
//    nationality = "Germany",
//    description = "Tech enthusiast who loves to travel light and document every journey with drone shots and blogs.",
//    photo = null,
//    interests = listOf("Technology", "Blogging", "Road Trips"),
//    desiredDestinations = listOf(
//        Destination("Santorini",
//            "https://images.pexels.com/photos/161815/santorini-oia-greece-water-161815.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1".toUri()),
//        Destination("Rome",
//            "https://images.pexels.com/photos/2760519/pexels-photo-2760519.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1".toUri()),
//        ),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var AmiraProfile = UserProfile(
//    userId = "5",
//    name = "Amira",
//    surname = "El-Sayed",
//    nickname = "amira.nomad",
//    gender = GenderType.FEMALE,
//    nationality = "Egypt",
//    description = "Cultural explorer and history lover. I enjoy connecting with locals and understanding the roots of every place I visit.",
//    photo = null,
//    interests = listOf("History", "Culture", "Museums"),
//    desiredDestinations = listOf(),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var HugoProfile = UserProfile(
//    userId = "6",
//    name = "Hugo",
//    surname = "Martinez",
//    nickname = "hugo_trails",
//    gender = GenderType.MALE,
//    nationality = "Mexico",
//    description = "Outdoor enthusiast and seasoned backpacker. I live for campfires, starry skies, and making new friends on the trail.",
//    photo = null,
//    interests = listOf("Camping", "Hiking", "Backpacking"),
//    desiredDestinations = listOf(),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var YukiProfile = UserProfile(
//    userId = "7",
//    name = "Yuki",
//    surname = "Tanaka",
//    nickname = "yuki.world",
//    gender = GenderType.OTHER,
//    nationality = "Japan",
//    description = "Minimalist traveler into meditation and mindfulness. I find peace in every journey, whether in cities or the wild.",
//    photo = null,
//    interests = listOf("Camping", "Minimalism", "Urban"),
//    desiredDestinations = listOf(),
//    reviews = listOf(),
//    socials = listOf()
//)
//
//var RobertoProfile = UserProfile(
//    userId = "8",
//    name = "Roberto",
//    surname = "Zancana",
//    nickname = "maldimeriggio",
//    gender = GenderType.MALE,
//    nationality = "Italy",
//    description = "Passionate traveler with a love for exploring new cultures and capturing moments along the way. My big passion is surfing!",
//    photo = UserPhoto.Resource(R.drawable.roberto),
//    interests = listOf("Adventure", "Art", "Surf"),
//    desiredDestinations = listOf(
//        Destination("Algarve",
//            "https://images.pexels.com/photos/1368502/pexels-photo-1368502.jpeg?auto=compress&cs=tinysrgb&w=600".toUri())
//    ),
//    reviews = listOf(userReviewKatmandu, userReviewBuenosAires, userReviewTokyo),
//    socials = listOf(SocialHandle(SocialPlatform.INSTAGRAM, username = "maldimeriggio"), SocialHandle(SocialPlatform.FACEBOOK, username = "Roberto Maldimeriggio"))
//)
