package com.zaita.aliyounes.zaitafc.helpers

import com.zaita.aliyounes.zaitafc.pojos.Member
import com.zaita.aliyounes.zaitafc.pojos.Player
import com.zaita.aliyounes.zaitafc.pojos.Post
import com.zaita.aliyounes.zaitafc.pojos.PostType

import java.util.ArrayList
import java.util.Date

/**
 * Created by Lenovo on 10/21/2017.
 */

object TestHelper {
    private val dummyPostImages: ArrayList<String>
        get() {
            val postImages = ArrayList<String>()
            postImages.add("http://webneel.com/wallpaper/sites/default/files/images/06-2013/beautiful%20sky%20tree%20wallpaper.jpg")
            postImages.add("https://i0.wp.com/techbeasts.com/wp-content/uploads/2016/01/nature-wallpapers-2.jpeg")
            postImages.add("https://awesomewallpapers.files.wordpress.com/2016/02/taschachhausinpitztal-2880x1800-large.jpg")
            postImages.add("https://wallpaperlayer.com/img/2015/6/nature-wallpapers-hd-6676-6954-hd-wallpapers.jpg")
            postImages.add("http://s1.picswalls.com/wallpapers/2014/08/08/icelandic-nature-wallpaper_015715501_150.jpg")
            return postImages
        }
    val dummyFootballPlayers: List<Member>
        get() {
            val footballPlayers = ArrayList<Member>()
            footballPlayers.add(Player("Player 1", 1, "GoalKeeper", 22, Date()))
            footballPlayers.add(Player("Player 2", 2, "Defense", 24, Date()))
            footballPlayers.add(Player("Player 3", 3, "Defense", 19, Date()))
            footballPlayers.add(Player("Player 4", 5, "Left Wing", 21, Date()))
            footballPlayers.add(Player("Player 5", 6, "Right Wing", 25, Date()))
            footballPlayers.add(Player("Player 6", 8, "Center", 23, Date()))
            footballPlayers.add(Player("Player 7", 7, "Striker", 20, Date()))
            footballPlayers.add(Player("Player 8", 10, "Striker", 18, Date()))
            footballPlayers.add(Player("Player 9", 22, "GoalKeeper", 25, Date()))
            footballPlayers.add(Player("Player 10", 12, "Defense", 27, Date()))
            footballPlayers.add(Player("Player 11", 14, "Striker", 32, Date()))
            footballPlayers.add(Player("Player 12", 15, "Center", 26, Date()))
            footballPlayers.add(Player("Player 13", 9, "Striker", 21, Date()))
            return footballPlayers
        }
    val dummyPingPongPlayers: List<Member>
        get() {
            val pingPongPlayers = ArrayList<Member>()
            pingPongPlayers.add(Member("Player 1", "PingPong Player", 22, Date()))
            pingPongPlayers.add(Member("Player 2", "PingPong Player", 24, Date()))
            pingPongPlayers.add(Member("Player 3", "PingPong Player", 19, Date()))
            pingPongPlayers.add(Member("Player 4", "PingPong Player", 21, Date()))
            pingPongPlayers.add(Member("Player 5", "PingPong Player", 25, Date()))
            pingPongPlayers.add(Member("Player 6", "PingPong Player", 23, Date()))
            pingPongPlayers.add(Member("Player 7", "PingPong Player", 20, Date()))
            pingPongPlayers.add(Member("Player 8", "PingPong Player", 18, Date()))
            pingPongPlayers.add(Member("Player 9", "PingPong Player", 25, Date()))
            pingPongPlayers.add(Member("Player 10", "PingPong Player", 27, Date()))
            pingPongPlayers.add(Member("Player 11", "PingPong Player", 32, Date()))
            pingPongPlayers.add(Member("Player 12", "PingPong Player", 26, Date()))
            pingPongPlayers.add(Member("Player 13", "PingPong Player", 21, Date()))
            return pingPongPlayers
        }
    val allMembers: List<Member>
        get() {
            val members = ArrayList<Member>()
            members.addAll(dummyFootballPlayers)
            members.addAll(dummyPingPongPlayers)
            return members
        }
    @Suppress("unused")
    val dummyNews: List<Post>
        get() {
            val dummyNews = ArrayList<Post>()
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.PHOTOS, DateTimeUtils.toDayDateString(Date()), dummyPostImages))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.TEXT_ONLY, DateTimeUtils.toDayDateString(Date())))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.SINGLE_IMAGE, DateTimeUtils.toDayDateString(Date())))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.VIDEO, DateTimeUtils.toDayDateString(Date())))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.PHOTOS, DateTimeUtils.toDayDateString(Date()), dummyPostImages))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.TEXT_ONLY, DateTimeUtils.toDayDateString(Date())))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.SINGLE_IMAGE, DateTimeUtils.toDayDateString(Date())))
            dummyNews.add(Post("Dummy Post Title", "Dummy Post Body", PostType.VIDEO, DateTimeUtils.toDayDateString(Date())))
            return dummyNews
        }
}
