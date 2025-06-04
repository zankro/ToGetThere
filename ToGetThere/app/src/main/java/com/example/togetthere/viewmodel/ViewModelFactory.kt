package com.example.togetthere.viewmodel

//object ViewModelFactory: ViewModelProvider.Factory {
//    private val model: ??? = ???()
//
//}

//class MainViewModelFactory(
//    private val mainRepository: MainRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(TripViewModel::class.java) -> {
//                TripViewModel(mainRepository.tripRepository, ) as T
//            }
//            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
//                UserViewModel(mainRepository) as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//        }
//    }
//}

/**
 * Ask About This
 */
//class TripDetailViewModelFactory(
//    private val tripRepository: TripRepository,
//    private val tripId: Int
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TripDetailViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return TripDetailViewModel(tripRepository, tripId) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}