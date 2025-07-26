var pluginMainThreadId = 0L

fun checkForPluginMainThread() {
    if (Thread.currentThread().id != pluginMainThreadId) {
        throw IllegalArgumentException("Entered method not on plugin thread!")
    }
}