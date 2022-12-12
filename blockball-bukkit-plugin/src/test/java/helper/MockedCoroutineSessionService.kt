package helper

import com.github.shynixn.blockball.api.business.service.CoroutineSessionService
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class MockedCoroutineSessionService : CoroutineSessionService {
    private val mainCoroutineDispatcher = MainCoroutineDispatcher()

    /**
     * Launch.
     */
    override fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit): Job {
        return GlobalScope.launch(mainCoroutineDispatcher) {
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
        get() = mainCoroutineDispatcher

    /**
     * Async.
     */
    override val asyncDispatcher: CoroutineContext
        get() = Dispatchers.IO
}

class MainCoroutineDispatcher(
) : CoroutineDispatcher() {
    private val executor = Executors.newSingleThreadExecutor()
    private var threadId = 0L

    init {
        executor.submit {
            threadId = Thread.currentThread().id
        }
    }

    /**
     * Returns `true` if the execution of the coroutine should be performed with [dispatch] method.
     * The default behavior for most dispatchers is to return `true`.
     * This method should generally be exception-safe. An exception thrown from this method
     * may leave the coroutines that use this dispatcher in the inconsistent and hard to debug state.
     */
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return threadId != Thread.currentThread().id
    }

    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.submit(block)
    }
}
