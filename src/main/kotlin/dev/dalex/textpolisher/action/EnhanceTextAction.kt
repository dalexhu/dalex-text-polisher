package dev.dalex.textpolisher.action

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import dev.dalex.textpolisher.api.AiClient
import dev.dalex.textpolisher.prompt.PromptBuilder
import dev.dalex.textpolisher.settings.ApiKeyStorage
import dev.dalex.textpolisher.settings.PolisherSettings
import dev.dalex.textpolisher.ui.DiffResultHandler

class EnhanceTextAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor?.selectionModel?.hasSelection() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText ?: return
        val selectionStart = selectionModel.selectionStart
        val selectionEnd = selectionModel.selectionEnd

        val state = PolisherSettings.getInstance().state

        if (selectedText.length > state.maxSelectionLength) {
            notify(project, "Selection too long (${selectedText.length} chars). Maximum is ${state.maxSelectionLength}.", NotificationType.WARNING)
            return
        }

        val apiKey = ApiKeyStorage.get()
        if (apiKey.isNullOrBlank() && state.provider != "ollama") {
            notify(project, "API key not configured. Please set it in Settings > Tools > AI Text Polisher.", NotificationType.ERROR)
            return
        }

        object : Task.Backgroundable(project, "AI Text Polisher: Enhancing...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                try {
                    val prompt = PromptBuilder.build(selectedText, state)
                    val result = AiClient.create(state, apiKey).complete(prompt, state.requestTimeout.toLong())
                    DiffResultHandler.show(project, editor, selectionStart, selectionEnd, selectedText, result, state.autoApply)
                } catch (ex: Exception) {
                    notify(project, "Enhancement failed: ${ex.message}", NotificationType.ERROR)
                }
            }
        }.queue()
    }

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("AI Text Polisher")
            .createNotification(content, type)
            .notify(project)
    }
}
