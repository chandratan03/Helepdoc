package edu.bluejack19_2.HelepDoc.models

class Cart(product:Product, quantity:Int){
    var product:Product = product
    var quantity:Int = quantity
    constructor() : this(Product(), 0)
}
