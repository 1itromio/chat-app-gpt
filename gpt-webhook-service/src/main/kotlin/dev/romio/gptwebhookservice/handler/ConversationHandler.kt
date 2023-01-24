package dev.romio.gptwebhookservice.handler

import arrow.core.Either
import arrow.core.getOrElse
import dev.romio.gptengine.GptEngine
import dev.romio.gptengine.model.CreateCompletionsRequest
import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.BotMessage
import dev.romio.gptwebhookservice.model.Conversation
import dev.romio.gptwebhookservice.model.UserMessage
import dev.romio.gptwebhookservice.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class ConversationHandler(private val config: Config, private val storage: Storage) {

    init {
        runBlocking {
            GptEngine.init(config.openAiKey)
        }
    }

    fun getResponseMessage(
        userMessage: UserMessage
    ): Flow<Either<dev.romio.gptengine.util.Error, BotMessage>> = flow {
        val completionResult = GptEngine.createCompletions(createCompletionRequest(userMessage)).map {
            val message = if(it.choices.isEmpty()) {
                "Unknown"
            } else {
                it.choices[0].text.replaceFirst("AI: ", "").substringBeforeLast(".") + "."
            }
            BotMessage(message)
        }
        if(completionResult.isRight()) {
            storage.saveConversation(
                getUserId(userMessage),
                Conversation(userMessage, completionResult.getOrElse { BotMessage("Unknown") })
            )
        }
        emit(completionResult)
    }

    private suspend fun createCompletionRequest(userMessage: UserMessage): CreateCompletionsRequest {
        val prompt = config.startingPrompt +
                "\n" +
                storage.getConversation(getUserId(userMessage)).joinToString("\n") {
                    "Human: ${it.userMessage.msg}\nAI: ${it.botMessage.msg}"
                }
        return CreateCompletionsRequest(
            model = config.textModel,
            prompt = prompt
        )
    }

    private fun getUserId(userMessage: UserMessage): String {
        return "${userMessage.source.name}_${userMessage.userId}"
    }

}