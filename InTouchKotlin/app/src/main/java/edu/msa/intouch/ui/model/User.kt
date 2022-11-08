package edu.msa.intouch.ui.model

class User {
    var uid: String? = null
    var email: String? = null

    constructor(){}
    constructor(uid: String?, email: String?){
        this.uid = uid
        this.email = email
    }
}