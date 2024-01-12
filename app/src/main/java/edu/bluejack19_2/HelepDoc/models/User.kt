package edu.bluejack19_2.HelepDoc.models

class User (id: String?, fullname: String, gender: String, dob: String, email: String, password: String, phoneNumber: String){
    var id: String? = id
    var fullname: String = fullname
    var gender: String = gender
    var dob: String = dob
    var email: String = email
    var password: String = password
    var phoneNumber: String = phoneNumber
    var balance: Float = 0.0f
    var picture: String = ""
    constructor() : this("","","","","","","")
}