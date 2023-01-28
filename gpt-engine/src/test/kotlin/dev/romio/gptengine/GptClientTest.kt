package dev.romio.gptengine

import arrow.core.Either
import dev.romio.gptengine.GptClient.Companion.gptClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class GptClientTest {

    @Test
    fun testCreateStreamingCompletions() = runBlocking {
        val gptClient = gptClient {
            apiKey = "<YOUR_API_KEY>"
        }
        val result = gptClient.createStreamingCompletions {
            model = "text-davinci-003"
            prompt = "Hi!"
        }
        when(result) {
            is Either.Left -> {

            }
            is Either.Right -> {
                result.value.collect {
                    println(it.choices[0].text)
                }
            }
        }
    }
}