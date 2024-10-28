import com.zoop.pos.collection.VoidTransaction

data class MainState(
    val status: Status = Status.FINISHED,
    val checkValidationKey: Boolean = false,
    val startPayment: Boolean = false,
    val startLogin: Boolean = false,
    val message: String = "",
    val qrCode: String = "",
    val transactionsList: List<VoidTransaction> = listOf()
)

enum class Status {
    STARTED,
    NONE,
    MESSAGE,
    QR_CODE,
    DISPLAY_LIST,
    FINISHED,
    DISPLAY_OPTION_PAYMENT
}