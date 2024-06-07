package org.dhis2.form.ui.provider.inputfield

import android.content.Context
import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.databinding.DataBindingUtil
import com.simprints.simprints.databinding.FormSimprintsBiometricsBinding
import org.dhis2.form.BR
import org.dhis2.form.R
import org.dhis2.form.extensions.autocompleteList
import org.dhis2.form.extensions.inputState
import org.dhis2.form.extensions.legend
import org.dhis2.form.extensions.supportingText
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.UiRenderType
import org.dhis2.form.model.simprints.SimprintsBiometricsAttributeWrapperUiModel
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.intent.FormIntent
import org.hisp.dhis.mobile.ui.designsystem.component.InputBarCode
import org.hisp.dhis.mobile.ui.designsystem.component.InputQRCode
import org.hisp.dhis.mobile.ui.designsystem.component.InputStyle
import org.hisp.dhis.mobile.ui.designsystem.component.InputText

@Composable
internal fun ProvideInputsForValueTypeText(
    modifier: Modifier = Modifier,
    inputStyle: InputStyle,
    fieldUiModel: FieldUiModel,
    intentHandler: (FormIntent) -> Unit,
    uiEventHandler: (RecyclerViewUiEvents) -> Unit,
    focusManager: FocusManager,
    onNextClicked: () -> Unit,
) {
    if (fieldUiModel is SimprintsBiometricsAttributeWrapperUiModel) {
        val context: Context = LocalContext.current
        val inflater = LayoutInflater.from(context)
        val binding: FormSimprintsBiometricsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.form_simprints_biometrics,
            null,
            false,
        )
        binding.setVariable(BR.simprintsBiometricsUiModel, fieldUiModel.getSimprintsBiometricsUiModel())
        AndroidView(factory = { binding.root }, modifier = Modifier.fillMaxWidth())
        return
    }

    when (fieldUiModel.renderingType) {
        UiRenderType.QR_CODE, UiRenderType.GS1_DATAMATRIX -> {
            ProvideQRInput(
                modifier = modifier,
                inputStyle = inputStyle,
                fieldUiModel = fieldUiModel,
                intentHandler = intentHandler,
                uiEventHandler = uiEventHandler,
                focusManager = focusManager,
                onNextClicked = onNextClicked,
            )
        }

        UiRenderType.BAR_CODE -> {
            ProvideBarcodeInput(
                modifier = modifier,
                inputStyle = inputStyle,
                fieldUiModel = fieldUiModel,
                intentHandler = intentHandler,
                uiEventHandler = uiEventHandler,
                focusManager = focusManager,
                onNextClicked = onNextClicked,
            )
        } else -> {
            ProvideDefaultTextInput(
                modifier = modifier,
                inputStyle = inputStyle,
                fieldUiModel = fieldUiModel,
                intentHandler = intentHandler,
                focusManager = focusManager,
                onNextClicked = onNextClicked,
            )
        }
    }
}

@Composable
private fun ProvideQRInput(
    modifier: Modifier,
    inputStyle: InputStyle,
    fieldUiModel: FieldUiModel,
    intentHandler: (FormIntent) -> Unit,
    uiEventHandler: (RecyclerViewUiEvents) -> Unit,
    focusManager: FocusManager,
    onNextClicked: () -> Unit,
) {
    val textSelection = TextRange(if (fieldUiModel.value != null) fieldUiModel.value!!.length else 0)
    var value by remember(fieldUiModel.value) {
        mutableStateOf(TextFieldValue(fieldUiModel.value ?: "", textSelection))
    }

    InputQRCode(
        modifier = modifier.fillMaxWidth(),
        title = fieldUiModel.label,
        state = fieldUiModel.inputState(),
        supportingText = fieldUiModel.supportingText(),
        legendData = fieldUiModel.legend(),
        inputTextFieldValue = value,
        inputStyle = inputStyle,
        isRequiredField = fieldUiModel.mandatory,
        onNextClicked = onNextClicked,
        onValueChanged = {
            value = it ?: TextFieldValue()
            intentHandler(
                FormIntent.OnTextChange(
                    fieldUiModel.uid,
                    value.text,
                    fieldUiModel.valueType,
                ),
            )
        },
        onQRButtonClicked = {
            if (value.text.isEmpty()) {
                uiEventHandler.invoke(
                    RecyclerViewUiEvents.ScanQRCode(
                        fieldUiModel.uid,
                        optionSet = fieldUiModel.optionSet,
                        fieldUiModel.renderingType,
                    ),
                )
            } else {
                uiEventHandler.invoke(
                    RecyclerViewUiEvents.DisplayQRCode(
                        fieldUiModel.uid,
                        optionSet = fieldUiModel.optionSet,
                        value = value.text,
                        renderingType = fieldUiModel.renderingType,
                        editable = fieldUiModel.editable,
                        label = fieldUiModel.label,
                    ),
                )
            }
        },
        autoCompleteList = fieldUiModel.autocompleteList(),
        autoCompleteItemSelected = {
            focusManager.clearFocus()
        },
    )
}

@Composable
private fun ProvideDefaultTextInput(
    modifier: Modifier,
    inputStyle: InputStyle = InputStyle.DataInputStyle(),
    fieldUiModel: FieldUiModel,
    intentHandler: (FormIntent) -> Unit,
    focusManager: FocusManager,
    onNextClicked: () -> Unit,
) {
    val textSelection = TextRange(fieldUiModel.value?.length ?: 0)
    var value by remember(fieldUiModel.value) {
        mutableStateOf(TextFieldValue(fieldUiModel.value ?: "", textSelection))
    }
    InputText(
        modifier = modifier.fillMaxWidth(),
        title = fieldUiModel.label,
        state = fieldUiModel.inputState(),
        supportingText = fieldUiModel.supportingText(),
        legendData = fieldUiModel.legend(),
        inputTextFieldValue = value,
        inputStyle = inputStyle,
        isRequiredField = fieldUiModel.mandatory,
        onNextClicked = onNextClicked,
        onValueChanged = {
            value = it ?: TextFieldValue()
            intentHandler(
                FormIntent.OnTextChange(
                    fieldUiModel.uid,
                    value.text,
                    fieldUiModel.valueType,
                ),
            )
        },
        autoCompleteList = fieldUiModel.autocompleteList(),
        onAutoCompleteItemSelected = {
            focusManager.clearFocus()
        },
    )
}

@Composable
private fun ProvideBarcodeInput(
    modifier: Modifier,
    inputStyle: InputStyle,
    fieldUiModel: FieldUiModel,
    intentHandler: (FormIntent) -> Unit,
    uiEventHandler: (RecyclerViewUiEvents) -> Unit,
    focusManager: FocusManager,
    onNextClicked: () -> Unit,
) {
    val textSelection = TextRange(if (fieldUiModel.value != null) fieldUiModel.value!!.length else 0)

    var value by remember(fieldUiModel.value) {
        mutableStateOf(TextFieldValue(fieldUiModel.value ?: "", textSelection))
    }

    InputBarCode(
        modifier = modifier.fillMaxWidth(),
        inputStyle = inputStyle,
        title = fieldUiModel.label,
        state = fieldUiModel.inputState(),
        supportingText = fieldUiModel.supportingText(),
        legendData = fieldUiModel.legend(),
        inputTextFieldValue = value,
        isRequiredField = fieldUiModel.mandatory,
        onNextClicked = onNextClicked,
        onValueChanged = {
            value = it ?: TextFieldValue()
            intentHandler(
                FormIntent.OnTextChange(
                    fieldUiModel.uid,
                    value.text,
                    fieldUiModel.valueType,
                ),
            )
        },
        onActionButtonClicked = {
            if (value.text.isEmpty()) {
                uiEventHandler.invoke(
                    RecyclerViewUiEvents.ScanQRCode(
                        fieldUiModel.uid,
                        optionSet = fieldUiModel.optionSet,
                        fieldUiModel.renderingType,
                    ),
                )
            } else {
                uiEventHandler.invoke(
                    RecyclerViewUiEvents.DisplayQRCode(
                        fieldUiModel.uid,
                        optionSet = fieldUiModel.optionSet,
                        value = value.text,
                        renderingType = fieldUiModel.renderingType,
                        editable = fieldUiModel.editable,
                        label = fieldUiModel.label,
                    ),
                )
            }
        },
        autoCompleteList = fieldUiModel.autocompleteList(),
        autoCompleteItemSelected = {
            focusManager.clearFocus()
        },
    )
}
