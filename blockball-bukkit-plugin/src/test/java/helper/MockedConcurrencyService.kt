package helper

import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import java.util.concurrent.Executors

class MockedConcurrencyService : ConcurrencyService {
    /**
     * Runs the given [function] synchronised with the given [delayTicks] and [repeatingTicks].
     */
    override fun runTaskSync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
        function.invoke()
    }

    /**
     * Runs the given [function] asynchronous with the given [delayTicks] and [repeatingTicks].
     */
    override fun runTaskAsync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
        function.invoke()
    }
}