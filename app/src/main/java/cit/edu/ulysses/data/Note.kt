package cit.edu.ulysses.data

data class Note(
    var id: String? = null,
    var title: String = "",
    var content: String = "",
    var userId: String = ""
)