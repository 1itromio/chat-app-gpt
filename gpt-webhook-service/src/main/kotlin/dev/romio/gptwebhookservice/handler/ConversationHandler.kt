package dev.romio.gptwebhookservice.handler

import arrow.core.Either
import arrow.core.getOrElse
import dev.romio.gptengine.GptClient
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptengine.model.CreateImageRequest
import dev.romio.gptengine.util.OpenAiClientError
import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.BotMessage
import dev.romio.gptwebhookservice.model.Conversation
import dev.romio.gptwebhookservice.model.UserMessage
import dev.romio.gptwebhookservice.model.UserMessageSource
import dev.romio.gptwebhookservice.storage.Storage

class ConversationHandler(
    private val config: Config,
    private val gptClient: GptClient,
    private val storage: Storage
) {
    suspend fun getResponseMessage(userMessage: UserMessage): Either<OpenAiClientError, BotMessage> {
        val completionResult = gptClient.createCompletions(createCompletionRequest(userMessage)).map {
            val message = if (it.choices.isEmpty()) {
                "Unknown"
            } else {
                it.choices[0].text.replaceFirst("AI: ", "").substringBeforeLast(".") + "."
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

    suspend fun generateCode(prompt: String): Either<OpenAiClientError, String> {
        return gptClient.createCompletions(
            CreateCompletionsRequest(
                model = config.codeModel,
                prompt = prompt,
                temperature = 0f,
                topP = 1f,
                frequencyPenalty = 0f,
                presencePenalty = 0f,
                bestOf = 1,
                maxTokens = 1000
            )
        ).map {
            it.choices[0].text
        }
    }

    suspend fun clearConversation(source: UserMessageSource, userId: String) {
        storage.clearConversation(getConversationKey(source, userId))
    }

    private suspend fun createCompletionRequest(userMessage: UserMessage): CreateCompletionsRequest {
        val prompt = config.startingPrompt +
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
        return CreateCompletionsRequest(
            model = config.textModel,
            prompt = prompt
        )
    }

    private fun getConversationKey(source: UserMessageSource, userId: String): String {
        return "$source-$userId"
    }
}
