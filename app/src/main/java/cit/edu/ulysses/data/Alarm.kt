package cit.edu.ulysses.data

data class Alarm(
    var id: String? = null,
    var Hour: String = "",
    var Minute: String = "",
    var Time: String? = null,
    var Unit: String = "",
    var Label: String = "",
    var On: Boolean = true
) {
    constructor() : this(null, "", "", null, "", "", true)
}