import androidx.compose.runtime.mutableStateOf
import com.zoop.pos.Zoop
import com.zoop.pos.collection.UserInput
import com.zoop.pos.collection.UserSelection
import com.zoop.pos.collection.VoidTransaction
import com.zoop.pos.exception.ZoopRequestCanceledException
import com.zoop.pos.plugin.DashboardConfirmationResponse
import com.zoop.pos.plugin.DashboardThemeResponse
import com.zoop.pos.plugin.DashboardTokenResponse
import com.zoop.pos.plugin.ZoopFoundationPlugin
import com.zoop.pos.requestfield.MessageCallbackRequestField
import com.zoop.pos.requestfield.PinCallbackRequestField
import com.zoop.pos.requestfield.QRCodeCallbackRequestField
import com.zoop.pos.requestfield.TransactionIdCallbackRequestField
import com.zoop.pos.terminal.Terminal
import com.zoop.pos.type.Callback
import com.zoop.pos.type.Option
import com.zoop.pos.type.Request
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.zoop.pos.desktop.DesktopPlugin
import com.zoop.pos.desktop.requestBuilder.DesktopPluginPixPaymentResponse
import com.zoop.pos.desktop.requestBuilder.DesktopZoopKeyValidationResponse
import com.zoop.pos.desktop.requestBuilder.PayResponse
import com.zoop.pos.desktop.requestBuilder.PinPadStatusResponse
import com.zoop.pos.desktop.requestBuilder.VoidResponse

class MainViewModel {
    var state by mutableStateOf(MainState(status = Status.NONE))
        private set
    private var currentRequest: Request? = null
    private var voidTransaction: UserSelection<VoidTransaction>? = null

    val TAG = "ExampleDesktopPlugin"

    fun handle(event: MainEvent) {
        when (event) {
            MainEvent.OnStartPinPad -> checkPinPad()
            MainEvent.IsPinpadConnected -> checkPinPad()
            MainEvent.CheckZoopKey -> checkZoopKey()
            MainEvent.OnStartLogin -> login()
            is MainEvent.OnStartPayment -> payment()
            is MainEvent.OnStartPix -> pix()
            MainEvent.OnStartCancellation -> void()
            MainEvent.OnCancelAction -> cancelAction()
            is MainEvent.OnSelectTransaction -> {
                voidTransaction?.select(event.transaction)
            }

            MainEvent.OnDisplayNone -> restoreUI()
            MainEvent.OnOptionPayment -> onOptionPayment()
        }
    }

    private fun checkZoopKey() {
        currentRequest = DesktopPlugin.createZoopKeyValidationRequestBuilder()
            .callback(object : Callback<DesktopZoopKeyValidationResponse>() {
                override fun onFail(error: Throwable) {
                }

                override fun onSuccess(response: DesktopZoopKeyValidationResponse) {
                    state = if (response.hasKey) {
                        state.copy(status = Status.MESSAGE, message = "PinPad possui chave Zoop.")
                    } else {
                        state.copy(
                            status = Status.MESSAGE,
                            message = "PinPad não possui chave Zoop."
                        )
                    }
                }

            })
            .build()
             Zoop.post(currentRequest!!)
    }

    private fun onOptionPayment() {
        state = state.copy(status = Status.DISPLAY_OPTION_PAYMENT)
    }

    private fun restoreUI() {
        state = state.copy(status = Status.NONE)
    }

    private fun cancelAction() {
        voidTransaction?.cancel()
        currentRequest?.cancel()
    }

    private fun login() {
        currentRequest = ZoopFoundationPlugin.createDashboardActivationRequestBuilder()
            .tokenCallback(object : Callback<DashboardTokenResponse>() {
                override fun onStart() {
                    state = state.copy(status = Status.MESSAGE, message = "Requisitando token")
                }

                override fun onFail(error: Throwable) {
                    println("$TAG Falha ao requisitar token")
                    state =
                        state.copy(status = Status.MESSAGE, message = "Falha ao requisitar token")
                }

                override fun onSuccess(response: DashboardTokenResponse) {
                    println("$TAG Apresentar token ao usuário: ${response.token}")
                    state = state.copy(
                        status = Status.MESSAGE,
                        message = "Insira o token no dashboard: ${response.token}"
                    )
                }
            })
            .confirmCallback(object : Callback<DashboardConfirmationResponse>() {
                override fun onFail(error: Throwable) {
                    /**
                    Caso o login seja cancelado, receberá a resposta aqui, com mensagem "request canceled"
                    loginRequest.cancel()
                     */
                    println("$TAG Apresentar erro na confirmação do token: ${error.message}")
                    state = when (error) {
                        is ZoopRequestCanceledException -> state.copy(
                            status = Status.MESSAGE,
                            message = "Operação cancelada"
                        )

                        else -> state.copy(
                            status = Status.MESSAGE,
                            message = error.message.toString()
                        )
                    }
                }

                override fun onSuccess(response: DashboardConfirmationResponse) {
                    /**
                     * Nesse ponto, recomendamos guardar as credenciais localmente em um banco de dados/shared preferences,
                     * para usar na próxima inicialização, passando como parâmetro para o SmartPOSPluginManager
                     */
                    println("$TAG Aqui, você recebe as credenciais do estabelecimento")
                    println("$TAG MarketplaceId: ${response.credentials.marketplace}")
                    println("$TAG SellerId: ${response.credentials.seller}")
                    println("$TAG Terminal: ${response.credentials.terminal}")
                    println("$TAG AccessKey: ${response.credentials.accessKey}")
                    println("$TAG SellerName: ${response.owner.name}")
                    state = state.copy(
                        status = Status.MESSAGE,
                        message = "SellerName: ${response.owner.name}"
                    )
                }
            })
            .themeCallback(object : Callback<DashboardThemeResponse>() {
                override fun onStart() {
                    if (currentRequest?.isCancelRequested == true) return
                    state = state.copy(status = Status.MESSAGE, message = "Baixando temas")
                }

                override fun onFail(error: Throwable) {
                    println("$TAG Apresentar erro no download de temas: ${error.message}")
                    state = state.copy(status = Status.MESSAGE, message = error.message.toString())
                }

                override fun onSuccess(response: DashboardThemeResponse) {
                    /**
                     * Aqui você recebe o esquema de cores configurado para o seller no dashboard,
                     * e também sinaliza o sucesso no fluxo de ativação do terminal.
                     */
                    println("$TAG, Exemplo de cor de fonte ${response.color.font}")
                    println("$TAG, Exemplo de cor de botão ${response.color.button}")
                    println("$TAG  Exemplo de logo colorido ${response.logo.coloredBase64}")
                    state = state.copy(status = Status.MESSAGE, message = "Login realizado")

                }
            }).build()

        Zoop.post(currentRequest!!)
    }

    private fun payment() {
        currentRequest = DesktopPlugin.createPaymentRequestBuilder()
            .amount(5)
            .option(Option.CREDIT)
            .installments(1)
            .callback(object : Callback<PayResponse>() {
                override fun onStart() {
//                startLoadingAnimation()
                    println("$TAG onStart")
                    state = state.copy(status = Status.MESSAGE, message = "Iniciando")
                }

                override fun onSuccess(response: PayResponse) {
//                handleSucessfullPayment(response)
                    println("$TAG onSuccess")
                    state = state.copy(status = Status.MESSAGE, message = "SUCESSO")
                }

                override fun onFail(error: Throwable) {
//                handlePaymentFailure(exception)
                    println("$TAG onFail ${error.message}")
                    val message = if (error.message?.contains("invalid session") == true) {
                        "Não foi realizado um login"
                    } else {
                        error.message
                    }

                    state = state.copy(status = Status.MESSAGE, message = message ?: "Falha")
                }
            })
            .messageCallback(object : Callback<MessageCallbackRequestField.MessageData>() {
                override fun onSuccess(response: MessageCallbackRequestField.MessageData) {
//                displayUserMessage(messageData.message)
                    println("$TAG messageCallback ${response.message}")
                    state = state.copy(status = Status.MESSAGE, message = response.message)
                }

                override fun onFail(error: Throwable) {
                    println("$TAG messageCallback fail")
                }
            })
            .build()

        Zoop.post(currentRequest!!)
    }

    private fun pix() {
        currentRequest = DesktopPlugin.createPixPaymentRequestBuilder()
            .amount(5L)
            .callback(object : Callback<DesktopPluginPixPaymentResponse>() {
                override fun onStart() {
                    state = state.copy(status = Status.MESSAGE, message = "Iniciando")
                }

                override fun onSuccess(response: DesktopPluginPixPaymentResponse) {
                    state = state.copy(status = Status.MESSAGE, message = "SUCESSO")
                }

                override fun onFail(error: Throwable) {
                    val message = if (error.message?.contains("invalid session") == true) {
                        "Não foi realizado um login"
                    } else {
                        error.message
                    }

                    state = state.copy(status = Status.MESSAGE, message = message ?: "Falha")
                }
            })
            .messageCallback(object : Callback<MessageCallbackRequestField.MessageData>() {
                override fun onSuccess(response: MessageCallbackRequestField.MessageData) {
                    state = state.copy(status = Status.MESSAGE, message = response.message)
                }

                override fun onFail(error: Throwable) {
                }
            })
            .qrCodeCallback(object : Callback<QRCodeCallbackRequestField.QRCodeData>() {
                override fun onSuccess(response: QRCodeCallbackRequestField.QRCodeData) {
                    state = state.copy(status = Status.QR_CODE, qrCode = response.data)
                }

                override fun onFail(error: Throwable) {
                }
            })
            .transactionIdCallback(object :
                Callback<TransactionIdCallbackRequestField.transactionIdData>() {
                override fun onSuccess(response: TransactionIdCallbackRequestField.transactionIdData) {
                }

                override fun onFail(error: Throwable) {
                }
            })
            .build()

        Zoop.post(currentRequest!!)
    }

    private fun checkPinPad() {
        currentRequest = DesktopPlugin.createPinPadStatusRequestBuilder()
            .callback(object : Callback<PinPadStatusResponse>() {
                override fun onFail(error: Throwable) {
                }

                override fun onSuccess(response: PinPadStatusResponse) {
                    state = if (response.isConnected) {
                        state.copy(status = Status.MESSAGE, message = "PinPad está conectado")
                    } else {
                        state.copy(
                            status = Status.MESSAGE,
                            message = "PinPad não está conectado"
                        )
                    }
                }

            })
            .build()
             Zoop.post(currentRequest!!)
    }

    private fun void() {
        currentRequest = DesktopPlugin.createVoidRequestBuilder()
            .callback(object : Callback<VoidResponse>() {
                override fun onStart() {
                    state = state.copy(status = Status.MESSAGE, message = "Processando")
                }

                override fun onSuccess(response: VoidResponse) {
                    state = state.copy(status = Status.MESSAGE, message = "Cancelamento realizado")
                    voidTransaction = null
                }

                override fun onFail(error: Throwable) {
                    state = state.copy(
                        status = Status.MESSAGE,
                        message = error.message ?: "Falha na operação"
                    )
                    voidTransaction = null
                }

                override fun onComplete() {
                    state = state.copy(status = Status.FINISHED, message = "")
                }
            })
            .voidTransactionCallback(object : Callback<UserSelection<VoidTransaction>>() {
                override fun onSuccess(response: UserSelection<VoidTransaction>) {
                    voidTransaction = response
                    state = state.copy(
                        transactionsList = voidTransaction!!.items.toList(),
                        status = Status.DISPLAY_LIST
                    )
                }

                override fun onFail(error: Throwable) {
                }
            })
            .messageCallback(object : Callback<MessageCallbackRequestField.MessageData>() {
                override fun onSuccess(response: MessageCallbackRequestField.MessageData) {
                    state = state.copy(status = Status.MESSAGE, message = response.message)
                }

                override fun onFail(error: Throwable) {
                }
            })
            .build()

        Zoop.post(currentRequest!!)
    }
}
