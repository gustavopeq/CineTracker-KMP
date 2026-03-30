package common.util.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

actual object AppHaptics {
    private val lightImpact = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
    )
    private val mediumImpact = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
    )
    private val heavyImpact = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
    )
    private val selectionFeedback = UISelectionFeedbackGenerator()
    private val notificationFeedback = UINotificationFeedbackGenerator()

    actual fun warmUp() {
        lightImpact.prepare()
        mediumImpact.prepare()
        heavyImpact.prepare()
        selectionFeedback.prepare()
        notificationFeedback.prepare()
    }

    actual fun light() {
        lightImpact.impactOccurred()
    }

    actual fun medium() {
        mediumImpact.impactOccurred()
    }

    actual fun heavy() {
        heavyImpact.impactOccurred()
    }

    actual fun tick() {
        selectionFeedback.selectionChanged()
    }

    actual fun success() {
        notificationFeedback.notificationOccurred(
            UINotificationFeedbackType.UINotificationFeedbackTypeSuccess
        )
    }

    actual fun error() {
        notificationFeedback.notificationOccurred(
            UINotificationFeedbackType.UINotificationFeedbackTypeError
        )
    }
}
