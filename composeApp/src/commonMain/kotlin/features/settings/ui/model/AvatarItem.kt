package features.settings.ui.model

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.animal_avatar_1
import cinetracker_kmp.composeapp.generated.resources.animal_avatar_2
import cinetracker_kmp.composeapp.generated.resources.animal_avatar_3
import cinetracker_kmp.composeapp.generated.resources.animal_avatar_4
import cinetracker_kmp.composeapp.generated.resources.anonymous_avatar
import cinetracker_kmp.composeapp.generated.resources.boy_avatar_1
import cinetracker_kmp.composeapp.generated.resources.boy_avatar_2
import cinetracker_kmp.composeapp.generated.resources.boy_avatar_3
import cinetracker_kmp.composeapp.generated.resources.boy_avatar_4
import cinetracker_kmp.composeapp.generated.resources.girl_avatar_1
import cinetracker_kmp.composeapp.generated.resources.girl_avatar_2
import cinetracker_kmp.composeapp.generated.resources.girl_avatar_3
import cinetracker_kmp.composeapp.generated.resources.girl_avatar_4
import org.jetbrains.compose.resources.DrawableResource

data class AvatarItem(val key: String, val drawableRes: DrawableResource)

enum class AvatarCategory {
    CRITTERS,
    HEROES,
    HEROINES
}

private val avatarsByCategory = linkedMapOf(
    AvatarCategory.HEROES to listOf(
        AvatarItem("boy_avatar_1", Res.drawable.boy_avatar_1),
        AvatarItem("boy_avatar_2", Res.drawable.boy_avatar_2),
        AvatarItem("boy_avatar_3", Res.drawable.boy_avatar_3),
        AvatarItem("boy_avatar_4", Res.drawable.boy_avatar_4)
    ),
    AvatarCategory.HEROINES to listOf(
        AvatarItem("girl_avatar_1", Res.drawable.girl_avatar_1),
        AvatarItem("girl_avatar_2", Res.drawable.girl_avatar_2),
        AvatarItem("girl_avatar_3", Res.drawable.girl_avatar_3),
        AvatarItem("girl_avatar_4", Res.drawable.girl_avatar_4)
    ),
    AvatarCategory.CRITTERS to listOf(
        AvatarItem("animal_avatar_1", Res.drawable.animal_avatar_1),
        AvatarItem("animal_avatar_2", Res.drawable.animal_avatar_2),
        AvatarItem("animal_avatar_3", Res.drawable.animal_avatar_3),
        AvatarItem("animal_avatar_4", Res.drawable.animal_avatar_4)
    )
)

fun getAvatarsByCategory(): Map<AvatarCategory, List<AvatarItem>> = avatarsByCategory

fun getDrawableForAvatarKey(key: String): DrawableResource = when (key) {
    "animal_avatar_1" -> Res.drawable.animal_avatar_1
    "animal_avatar_2" -> Res.drawable.animal_avatar_2
    "animal_avatar_3" -> Res.drawable.animal_avatar_3
    "animal_avatar_4" -> Res.drawable.animal_avatar_4
    "boy_avatar_1" -> Res.drawable.boy_avatar_1
    "boy_avatar_2" -> Res.drawable.boy_avatar_2
    "boy_avatar_3" -> Res.drawable.boy_avatar_3
    "boy_avatar_4" -> Res.drawable.boy_avatar_4
    "girl_avatar_1" -> Res.drawable.girl_avatar_1
    "girl_avatar_2" -> Res.drawable.girl_avatar_2
    "girl_avatar_3" -> Res.drawable.girl_avatar_3
    "girl_avatar_4" -> Res.drawable.girl_avatar_4
    else -> Res.drawable.anonymous_avatar
}

fun getRandomAvatar(): String {
    val heroAvatars = avatarsByCategory.getValue(AvatarCategory.HEROES) +
        avatarsByCategory.getValue(AvatarCategory.HEROINES)
    return heroAvatars.random().key
}
