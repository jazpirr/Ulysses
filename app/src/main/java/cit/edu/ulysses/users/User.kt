package cit.edu.ulysses.db

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val dob: String
)
