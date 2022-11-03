package com.tomal66.cconnect.Model

data class Post(var pid: String?=null,
                var title : String? = null,
                var description : String? = null,
                var picture : String? = null,
                var postedBy : String,
                var date : String? = null
                ){
}

