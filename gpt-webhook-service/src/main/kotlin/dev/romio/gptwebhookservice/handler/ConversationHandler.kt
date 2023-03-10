package dev.romio.gptwebhookservice.handler

import arrow.core.Either
import arrow.core.getOrElse
import dev.romio.gptengine.GptClient
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptengine.model.CreateCompletionsRequest.Companion.completionRequest
import dev.romio.gptengine.model.CreateImageRequest
import dev.romio.gptengine.util.OpenAiClientError
import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.BotMessage
import dev.romio.gptwebhookservice.model.Conversation
import dev.romio.gptwebhookservice.model.UserMessage
import dev.romio.gptwebhookservice.model.UserMessageSource
import dev.romio.gptwebhookservice.storage.Storage
import org.slf4j.Logger

class ConversationHandler constructor(
    private val config: Config,
    private val gptClient: GptClient,
    private val storage: Storage,
    private val log: Logger
) {
    suspend fun getResponseMessage(userMessage: UserMessage): Either<OpenAiClientError, BotMessage> {
        val completionResult = gptClient.createCompletions(createCompletionRequest(userMessage)).map {
            log.info("Received Response from GPT: ${it.choices[0].text}")
            val message = if (it.choices.isEmpty()) {
                "Unknown"
            } else {
                it.choices[0].text
                    .replaceFirst("AI:", "")
                    .substringBeforeLast(".").trim() + "."
            }
            BotMessage(message)
        }
        if (completionResult.isRight()) {
            storage.saveConversation(
                getConversationKey(userMessage.source, userMessage.userId),
                Conversation(userMessage, completionResult.getOrElse { BotMessage("Unknown") })
            )
        }
        return completionResult
    }

    suspend fun getImage(imageDescription: String): Either<OpenAiClientError, String> {
        return gptClient.createImage(CreateImageRequest(imageDescription)).map {
            it.imageDataList[0].url
        }
    }

    suspend fun generateCode(userPrompt: String): Either<OpenAiClientError, String> {
        return gptClient.createCompletions {
            model = config.codeModel
            prompt = userPrompt
            temperature = 0f
            topP = 1f
            frequencyPenalty = 0f
            presencePenalty = 0f
            bestOf = 1
            maxTokens = 1000
        }.map {
            it.choices[0].text
        }
    }

    suspend fun clearConversation(source: UserMessageSource, userId: String) {
        storage.clearConversation(getConversationKey(source, userId))
    }

    private suspend fun createCompletionRequest(userMessage: UserMessage): CreateCompletionsRequest {
        val finalPrompt = config.startingPrompt +
            "\n" +
            storage.getConversation(
                getConversationKey(
                    userMessage.source,
                    userMessage.userId
                )
            ).joinToString("\n") {
                "Human: ${it.userMessage.msg}\nAI: ${it.botMessage.msg}"
            } +
            "\n" +
            "Human: ${userMessage.msg}"
        return completionRequest {
            model = config.textModel
            prompt = finalPrompt
        }
    }

    private fun getConversationKey(source: UserMessageSource, userId: String): String {
        return "$source-$userId"
    }
}
