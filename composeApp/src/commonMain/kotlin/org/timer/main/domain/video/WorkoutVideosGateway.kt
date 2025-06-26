package org.timer.main.domain.video

object WorkoutVideosGateway {

    fun getWorkoutVideos() = listOf(
        Video(1, "Exercise 1", "https://www.youtube.com/watch?v=YAUGMT0_PiE"),
        Video(2, "Exercise 2", "https://www.youtube.com/watch?v=ml9ik3htY_w"),
        Video(3, "Exercise 3", "https://www.youtube.com/watch?v=d-tqetfOt5Q"),
        Video(4, "Exercise 4", "https://www.youtube.com/watch?v=8KiSvKx2SpM"),
        Video(5, "Exercise 5", "https://www.youtube.com/watch?v=hu_-_6VwaS8"),
        Video(6, "Exercise 6", "https://www.youtube.com/watch?v=s6vacIKdxog"),
        Video(7, "Exercise 7", "https://www.youtube.com/watch?v=YAUGMT0_PiE"),
        Video(8, "Exercise 8", "https://www.youtube.com/watch?v=OFibSNpw2hE"),
        Video(9, "Exercise 9", "https://www.youtube.com/watch?v=6NtCDYHp8eQ"),
        Video(10, "Exercise 10", "https://www.youtube.com/watch?v=exXly1KGEgM"),
        Video(11, "Exercise 11", "https://www.youtube.com/watch?v=Ef6LwAaB3_E")
    )

    fun getDanceAudios() = listOf(
        Video(1, "SEVENTEEN Feat. BSS)' Official MV", "https://www.youtube.com/watch?v=mBXBOLG06Wc"),
        Video(2, "PSY - GANGNAM STYLE (Official Music Video)", "https://www.youtube.com/watch?v=SW_iujvUAzQ"),
    )

}