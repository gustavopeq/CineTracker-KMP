package common.ui.components.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_star
import cinetracker_kmp.composeapp.generated.resources.rating_bottom_sheet_header
import cinetracker_kmp.composeapp.generated.resources.rating_bottom_sheet_save_button
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryBlueColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_MARGIN
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round

@Composable
fun RatingBottomSheet(
    initialRating: Float = 0f,
    dismissBottomSheet: () -> Unit,
    onRatingSave: (Float) -> Unit,
) {
    var rating by remember { mutableStateOf(initialRating) }
    val displayRating = round(rating * 10) / 10

    GenericBottomSheet(
        dismissBottomSheet = dismissBottomSheet,
        headerText = stringResource(Res.string.rating_bottom_sheet_header),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LARGE_MARGIN.dp, vertical = DEFAULT_MARGIN.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_star),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp),
                    colorFilter = ColorFilter.tint(PrimaryBlueColor)
                )
                Spacer(modifier = Modifier.size(DEFAULT_MARGIN.dp))
                Text(
                    text = displayRating.toString(),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp
                    ),
                    color = PrimaryBlueColor
                )
                Text(
                    text = "/10",
                    style = MaterialTheme.typography.titleLarge,
                    color = SecondaryGreyColor,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 0f..10f,
                steps = 99,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryBlueColor,
                    activeTrackColor = PrimaryBlueColor,
                    inactiveTrackColor = PrimaryBlueColor.copy(alpha = 0.24f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

            GenericButton(
                modifier = Modifier.fillMaxWidth(),
                buttonText = stringResource(Res.string.rating_bottom_sheet_save_button),
                onClick = {
                    onRatingSave(displayRating)
                    dismissBottomSheet()
                }
            )
        }
    }
}
