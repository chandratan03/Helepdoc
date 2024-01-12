package edu.bluejack19_2.HelepDoc.models

import java.util.*

class Message (senderId:String, receiverId:String, message:String, date: Date?){
    var senderId: String = senderId
    var receiverId:String = receiverId
    var message:String = message
    var date:Date? = date


    constructor(): this("", "" , "" , null)

}