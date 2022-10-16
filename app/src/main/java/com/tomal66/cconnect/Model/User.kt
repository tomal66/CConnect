package com.tomal66.cconnect.Model

data class User(var username : String? = null,
                var fullName : String? = null,
                var firstname : String? = null,
                var lastname : String? = null,
                var age : String? = null,
                var gender : String? = null,
                var institution : String? = null,
                var department : String? = null,
                var city : String? = null,
                var country : String? = null,
                var bio : String? = null,

                var uid: String?= null) {


    var posts : Int = 0
    var followers : Int = 0
    var following : Int = 0


    /*constructor(username: String, firstname: String, lastname: String, age: String, gender: String,
                institution: String, department: String, city: String, country: String)
    {
        this.username = username
        this.firstname = firstname
        this.lastname = lastname
        this.age = age
        this.gender = gender
        this.institution = institution
        this.department = department
        this.city = city
        this.country = country
    }*/

}