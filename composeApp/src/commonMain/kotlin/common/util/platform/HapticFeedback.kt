package common.util.platform

expect object AppHaptics {
    fun warmUp()
    fun light()
    fun medium()
    fun heavy()
    fun tick()
    fun success()
    fun error()
}
