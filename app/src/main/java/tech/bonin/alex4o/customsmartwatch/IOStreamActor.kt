package tech.bonin.alex4o.customsmartwatch

import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.CompletableDeferred
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by alex4o on 1/22/18.
 */

fun ioStreamActor(inputStream: InputStream, outputStream: OutputStream) = actor<IOMessage> {
    for (msg in channel) { // iterate over incoming messages
        when (msg) {
            is Send -> {
                outputStream.write(msg.byte)
            }
            is Receive -> {
                msg.response.complete(inputStream.read())
            }
        }
    }
}