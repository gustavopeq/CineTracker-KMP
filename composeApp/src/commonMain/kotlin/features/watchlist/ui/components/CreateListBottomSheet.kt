package features.watchlist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.create_new_list_button
import cinetracker_kmp.composeapp.generated.resources.create_new_list_header
import cinetracker_kmp.composeapp.generated.resources.create_new_list_placeholder
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.LARGE_MARGIN
import common.domain.util.UiConstants.NEW_LIST_MAX_CHARACTERS
import common.domain.util.UiConstants.SMALL_MARGIN
import common.domain.util.UiConstants.SMALL_PADDING
import common.ui.MainViewModel
import common.ui.components.SystemNavBarSpacer
import common.ui.components.button.GenericButton
import common.ui.theme.MainBarGreyColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateListBottomSheet(
    mainViewModel: MainViewModel,
) {
    val showBottomSheet by mainViewModel.displayCreateNewList.collectAsState()

    if (showBottomSheet) {
        ShowBottomSheet(
            mainViewModel = mainViewModel,
            dismissBottomSheet = {
                mainViewModel.updateDisplayCreateNewList(false)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowBottomSheet(
    mainViewModel: MainViewModel,
    dismissBottomSheet: () -> Unit,
) {
    val listName = mainViewModel.newListTextFieldValue.value
    val isDuplicatedName by mainViewModel.isDuplicatedListName.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val modalColor = MainBarGreyColor

    ModalBottomSheet(
        onDismissRequest = { dismissBottomSheet() },
        containerColor = modalColor,
        sheetState = sheetState,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = (-SMALL_MARGIN).dp),
            text = stringResource(resource = Res.string.create_new_list_header),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Divider(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(top = SMALL_PADDING.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = LARGE_MARGIN.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DEFAULT_MARGIN.dp),
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = listName,
                    onValueChange = {
                        if (it.length <= NEW_LIST_MAX_CHARACTERS) {
                            mainViewModel.updateCreateNewListTextField(it.uppercase().trim())
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(resource = Res.string.create_new_list_placeholder)
                                .uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface,
                            textAlign = TextAlign.Center,
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = modalColor,
                        unfocusedContainerColor = modalColor,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.secondary,
                        selectionColors = TextSelectionColors(
                            handleColor = MaterialTheme.colorScheme.secondary,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                        ),
                        errorIndicatorColor = Color.Red,
                    ),
                    isError = isDuplicatedName,
                )

                if (isDuplicatedName) {
                    Text(
                        text = "List name already exists!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = SMALL_PADDING.dp),
                    )
                }
            }

            GenericButton(
                buttonText = stringResource(resource = Res.string.create_new_list_button),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        mainViewModel.createNewList(
                            closeSheet = {
                                sheetState.hide()
                                mainViewModel.setRefreshLists(true)
                            },
                        )
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            dismissBottomSheet()
                        }
                    }
                },
                enabled = listName.isNotEmpty(),
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        SystemNavBarSpacer()
    }
}
