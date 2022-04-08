package bean

data class DepData(
    val isProject: Boolean = false,

    val addName: String = "",

    val fromName: String = "",
    val toName: String = "",

    val group: String = "",
    val artifact: String = "",
    val version: String = ""
)
