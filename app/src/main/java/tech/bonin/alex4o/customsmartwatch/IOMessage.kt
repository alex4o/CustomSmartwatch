package tech.bonin.alex4o.customsmartwatch

import kotlinx.coroutines.experimental.CompletableDeferred

/**
 * Created by alex4o on 1/22/18.
 */
sealed class IOMessage
class Send(val byte: Int) : IOMessage() // one-way message to increment counter
class Receive(val response: CompletableDeferred<Int>) : IOMessage() // a request with reply