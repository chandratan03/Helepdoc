package edu.bluejack19_2.HelepDoc.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Transaction(id:String?, carts:ArrayList<Cart>?, userId:String, transactionDate:Date?, userBalance:Double){
    var id:String? = id
    var carts:ArrayList<Cart>? = carts
    var userId:String = userId
    var userBalance:Double = userBalance
    var transactionDate:Date? = transactionDate
    constructor(): this("", ArrayList(), "", null, 0.0)

}
