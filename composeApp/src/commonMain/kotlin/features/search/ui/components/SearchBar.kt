package features.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_back_arrow
import cinetracker_kmp.composeapp.generated.resources.ic_close
import cinetracker_kmp.composeapp.generated.resources.search_bar_placeholder
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryYellowColor_90
import common.ui.theme.SecondaryGreyColor
import common.ui.theme.placeholderGrey2
import features.search.events.SearchEvent
import features.search.ui.SearchViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(viewModel: SearchViewModel, onBackPress: () -> Unit) {
    val searchBarValue by viewModel.searchQuery

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        value = searchBarValue,
        onValueChange = { query ->
            viewModel.onEvent(SearchEvent.SearchQuery(query))
        },
        placeholder = {
            Text(
                text = stringResource(resource = Res.string.search_bar_placeholder),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = placeholderGrey2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_back_arrow),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        trailingIcon = {
            if (searchBarValue.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.onEvent(SearchEvent.ClearSearchBar)
                    }
                ) {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_close),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = textFieldColors(),
        textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        singleLine = true
    )
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    cursorColor = MaterialTheme.colorScheme.secondary,
    selectionColors = TextSelectionColors(
        backgroundColor = SecondaryGreyColor,
        handleColor = PrimaryYellowColor_90
    ),
    focusedContainerColor = MainBarGreyColor,
    unfocusedContainerColor = MainBarGreyColor
)
