package model

data class UserModel(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = ""
) {
    // No-argument constructor
      constructor() : this("", "", "", "")
}
