import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoop.pos.type.Context
import com.zoop.pos.type.Option
import extension.convertToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.DesktopPluginManager

@Composable
fun App(context: Context, viewModel: MainViewModel) {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            MainScreen(context, viewModel)
        }
    }
}

@Composable
fun MainScreen(
    context: Context,
    viewModel: MainViewModel
) {
    DesktopPluginManager().initialize(context)
    var isPaymentPix by remember { mutableStateOf(false) }
    var isInitializePayment by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (!isInitializePayment) {
            Row(modifier = Modifier.padding(12.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.handle(MainEvent.OnOptionPayment)
                        isPaymentPix = false
                        isInitializePayment = true
                    },
                ) {
                    Text(
                        text = "Venda",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                Button(
                    onClick = {
                        viewModel.handle(MainEvent.OnOptionPayment)
                        isPaymentPix = true
                        isInitializePayment = true
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Pix",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = { viewModel.handle(MainEvent.OnStartCancellation) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = "Cancelamento",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            Button(
                onClick = { viewModel.handle(MainEvent.IsPinpadConnected) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = "PinPad",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            Button(
                onClick = { viewModel.handle(MainEvent.CheckZoopKey) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = "Zoop Key",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 12.dp))

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .border(
                        border = BorderStroke(1.dp, color = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ),
                onClick = {
                    viewModel.handle(MainEvent.OnStartLogin)
                }
            ) {
                Text(
                    text = "Login",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            AnimatedVisibility(visible = viewModel.state.status == Status.MESSAGE) {
                Text(text = viewModel.state.message)
            }

            AnimatedVisibility(
                visible = viewModel.state.message.contains(
                    "enter",
                    true
                )
            ) {
                CancelButton(handler = viewModel::handle)
            }

            AnimatedVisibility(visible = viewModel.state.status == Status.QR_CODE) {
                Column(
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    rememberQrBitmapPainter(viewModel.state.qrCode)?.let {
//                        Image(
//                            painter = it,
//                            contentDescription = "qRCode_token",
//                            contentScale = ContentScale.FillBounds,
//                            modifier = Modifier.size(135.dp),
//                            alignment = Alignment.Center
//                        )
//                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                    CancelButton(handler = viewModel::handle)
                }
            }
        }

        if (isInitializePayment) {
            AnimatedVisibility(visible = viewModel.state.status == Status.DISPLAY_OPTION_PAYMENT) {
                ChargePaymentOption(
                    handleCancel = {
                        viewModel.handle(MainEvent.OnDisplayNone)
                        isInitializePayment = false
                    },
                    handleEvent = {
                        isInitializePayment = false
                        val (amount, installment, option) = it
                        if (isPaymentPix) {
                            viewModel.handle(MainEvent.OnStartPix(amount))
                        } else {
                            viewModel.handle(MainEvent.OnStartPayment(amount, installment, option))
                        }
                    },
                    isPaymentPix = isPaymentPix,
                    isInitializePayment = isInitializePayment
                )
            }
        }
    }
}

@Composable
private fun ChargePaymentOption(
    handleEvent: (Triple<Long, Int, Option>) -> Unit,
    handleCancel: () -> Unit,
    isPaymentPix: Boolean = false,
    isInitializePayment: Boolean = false
) {
    val textFieldValueAmount = remember { mutableStateOf(TextFieldValue(text = "1")) }
    val textFieldValueInstallment = remember { mutableStateOf(TextFieldValue(text = "1")) }
    val radioOptions = listOf(Option.DEBIT, Option.CREDIT)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val textTitleOptPayment = if (!isPaymentPix) "payment_option"
        else "enter_the_value ".plus(" do pix")

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Center,
            text = textTitleOptPayment.uppercase(),
            fontSize = 18.sp
        )

        if (!isPaymentPix) {
            PaymentMethodSelection(radioOptions, onOptionSelected)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 4.dp),
                value = textFieldValueAmount.value,
                label = {
                    Text(text = "enter_the_value")
                },
                placeholder = {
                    Text(text = "enter_the_value")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (selectedOption == Option.DEBIT) ImeAction.Done
                    else ImeAction.Next
                ),
                onValueChange = {
                    textFieldValueAmount.value = it
                }
            )

            if (!isPaymentPix) {
                AnimatedVisibility(
                    modifier = Modifier
                        .weight(.4f),
                    visible = selectedOption == Option.CREDIT
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp),
                        value = textFieldValueInstallment.value,
                        label = {
                            Text(text = "installments")
                        },
                        placeholder = {
                            Text(text = "installments")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onValueChange = {
                            textFieldValueInstallment.value = it
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        val (amount, installment, option) = Triple(
            textFieldValueAmount.value.text.convertToLong(),
            textFieldValueInstallment.value.text.convertToLong().toInt(),
            selectedOption
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 12.dp, end = 4.dp)
                    .border(
                        border = BorderStroke(1.dp, color = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ),
                onClick = handleCancel
            ) {
                Text(text = "button_close")
            }

            Button(
                modifier = Modifier
                    .weight(.5f)
                    .padding(start = 4.dp, end = 12.dp),
                onClick = {
                    if (amount <= 0) {
                        return@Button
                    }

                    if (selectedOption == Option.CREDIT && installment <= 0) {
                        return@Button
                    }
                    handleEvent(Triple(amount, installment, option))
                }
            ) {
                Text(
                    text = "button_continue",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PaymentMethodSelection(
    paymentMethods: List<Option>,
    onOptionSelected: (Option) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(Option.CREDIT) }

    Column(modifier = Modifier.padding(16.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(paymentMethods) { method ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPaymentMethod = method
                                onOptionSelected(selectedPaymentMethod)
                            },
                        shape = RoundedCornerShape(8.dp),
                        elevation = if (selectedPaymentMethod == method) 8.dp else 4.dp,
                        border = if (selectedPaymentMethod == method) BorderStroke(
                            2.dp,
                            Color.Blue
                        ) else null
                    ) {
                        // Conteúdo do Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = method.name,
                                color = if (selectedPaymentMethod == method) Color.Blue else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CancelButton(handler: (MainEvent) -> Unit) {
    OutlinedButton(
        onClick = { handler(MainEvent.OnCancelAction) },
        modifier = Modifier
            .padding(5.dp)
            .background(color = Color.Transparent)
    ) {
        Text(
            text = "cancel_operation",
            modifier = Modifier.padding(8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

expect val initialContext: Context