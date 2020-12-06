package helper

import com.github.shynixn.blockball.api.business.service.CoroutineSessionService
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MockedCoroutineSessionService : CoroutineSessionService {
    /**
     * Launch.
     */
    override fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            f.invoke(this)
        }
    }

    /**
     * LaunchAsync.
     */
    override fun launchAsync(f: suspend CoroutineScope.() -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            f.invoke(this)
        }
    }

    /**
     * Minecraft.
     */
    override val minecraftDispatcher: CoroutineContext
        get() = Dispatchers.Main

    /**
     * Async.
     */
    override val asyncDispatcher: CoroutineContext
        get() = Dispatchers.IO
}
