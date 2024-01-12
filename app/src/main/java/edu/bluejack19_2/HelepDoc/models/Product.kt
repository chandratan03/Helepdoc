package edu.bluejack19_2.HelepDoc.models

class Product(id: String?, type:String,
    name: String, indicator:String, attention: String, dosage:String, composition:String, price:Float, image:String, countRate:Int
){
    var id:String? = id
    var type:String = type
    var name:String = name
    var indicator:String = indicator
    var attention:String = attention
    var dosage:String = dosage
    var composition:String = composition
    var price:Float = price
    var image:String = image
    var rating: Float = 0.0f
    var count_rate: Int = 0
    // read data from firebase must be initialize the constructor
    constructor() : this("", "", "","", "", "" ,"" ,0.0f, "", 0)

}

