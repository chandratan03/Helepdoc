package edu.bluejack19_2.HelepDoc.models

import java.util.*
import kotlin.collections.ArrayList

class Doctor (id: String?, fullname: String, email: String, phoneNumber: String, password: String, gender: String, specialist: String, fee: Float, ratedBy: ArrayList<String>){
    var id:String? = id
    var fullname: String = fullname
    var email: String = email
    var phoneNumber: String = phoneNumber
    var password: String = password
    var gender: String = gender
    var specialist: String = specialist
    var fee: Float = fee
    var picture: String = ""
    var rating: Float = 0.0f
    var count_rate: Int = 0
    var rated_by: ArrayList<String> = ratedBy
    constructor() : this("","","","", "","","",0.0f, ArrayList())
}
