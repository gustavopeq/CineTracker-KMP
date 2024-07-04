package common.ui.components.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import common.ui.components.SystemNavBarSpacer
import common.ui.theme.MainBarGreyColor
import common.util.UiConstants.SMALL_MARGIN
import common.util.UiConstants.SMALL_PADDING

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GenericBottomSheet(
    dismissBottomSheet: () -> Unit,
    headerText: String,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { dismissBottomSheet() },
        containerColor = MainBarGreyColor,
    ) {
        BottomSheetHeader(
            headerText = headerText,
        )
        Divider(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(top = SMALL_PADDING.dp),
        )
        content()
        Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
        SystemNavBarSpacer()
    }
}

@Composable
fun BottomSheetHeader(
    headerText: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = (-SMALL_MARGIN).dp),
            text = headerText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
