package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.AiLearningMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val modeLabel: String = "",
)

data class AiAssistantUiState(
    val selectedMode: AiLearningMode = AiLearningMode.DAILY_GUIDANCE,
    val messages: List<AiChatMessage> = listOf(
        AiChatMessage(
            id = "welcome",
            isUser = false,
            text = "Assalamu Alaikum! I am the HIDAYAH AI Islamic Assistant.\n\nAsk me in English, Bengali (বাংলা), Arabic (العربية), Urdu (اردو), or Hindi about Quran verses, Tajweed, Hifz, Arabic grammar, Hadith, or daily Islamic practice.\n\nNote: For formal personal Fatwas or complex legal rulings, always consult a qualified local scholar.",
            modeLabel = "HIDAYAH AI"
        )
    ),
    val isStreaming: Boolean = false,
    val errorMessage: String? = null,
)

class AiAssistantViewModel : ViewModel() {
    private val repo = AiAssistantRepository()
    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun selectMode(mode: AiLearningMode) {
        _uiState.value = _uiState.value.copy(selectedMode = mode, errorMessage = null)
    }

    fun clearConversation() {
        _uiState.value = AiAssistantUiState(selectedMode = _uiState.value.selectedMode)
    }

    fun sendQuestion(question: String, preferredLanguageName: String) {
        val clean = question.trim()
        if (clean.isEmpty() || _uiState.value.isStreaming) return

        val mode = _uiState.value.selectedMode
        val userMsg = AiChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = clean,
            modeLabel = mode.label
        )
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"
        val placeholderMsg = AiChatMessage(
            id = assistantMsgId,
            isUser = false,
            text = "",
            modeLabel = mode.label
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg + placeholderMsg,
            isStreaming = true,
            errorMessage = null,
        )

        viewModelScope.launch {
            val acc = StringBuilder()
            try {
                withTimeout(60_000L) {
                    repo.streamAnswer(clean, mode, preferredLanguageName).collect { chunk ->
                        if (chunk.isNotEmpty()) {
                            acc.append(chunk)
                            val updatedText = acc.toString()
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages.map {
                                    if (it.id == assistantMsgId) it.copy(text = updatedText) else it
                                }
                            )
                        }
                    }
                }
                if (acc.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages.map {
                            if (it.id == assistantMsgId) {
                                it.copy(text = "I could not generate a response right now. Please try rephrasing your question.")
                            } else it
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Request timed out after 60s. Please check your connection and try again.",
                    messages = _uiState.value.messages.filterNot { it.id == assistantMsgId && it.text.isEmpty() }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "Unable to reach HIDAYAH AI Assistant right now."
                _uiState.value = _uiState.value.copy(
                    errorMessage = msg,
                    messages = _uiState.value.messages.map {
                        if (it.id == assistantMsgId && it.text.isEmpty()) {
                            it.copy(text = "Sorry, I encountered an error: $msg")
                        } else it
                    }
                )
            } finally {
                _uiState.value = _uiState.value.copy(isStreaming = false)
            }
        }
    }
}
