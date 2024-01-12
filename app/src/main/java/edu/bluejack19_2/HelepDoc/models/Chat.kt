package edu.bluejack19_2.HelepDoc.models


class Chat (userId:String, doctorId:String, messages:ArrayList<Message>? ){
    var userId:String = userId
    var doctorId:String=  doctorId
    var messages:ArrayList<Message>? =  messages


    constructor():this("","", ArrayList())
}