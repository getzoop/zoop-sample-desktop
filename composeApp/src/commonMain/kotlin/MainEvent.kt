import com.zoop.pos.collection.VoidTransaction
import com.zoop.pos.type.Option

sealed class MainEvent {
    data object OnStartPinPad : MainEvent()
    data object IsPinpadConnected : MainEvent()
    data object CheckZoopKey : MainEvent()
    data object OnStartLogin : MainEvent()
    data class OnStartPayment(
        val amount: Long,
        val installment: Int,
        val option: Option
    ) : MainEvent()
    data class OnStartPix(val amount: Long) : MainEvent()
    data object OnStartCancellation : MainEvent()
    data object OnCancelAction : MainEvent()
    data object OnOptionPayment : MainEvent()
    data object OnDisplayNone : MainEvent()
    class OnSelectTransaction(val transaction: VoidTransaction) : MainEvent()
}