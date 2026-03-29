package dev.dalex.textpolisher.ui

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

object DiffResultHandler {

    fun show(project: Project, editor: Editor, originalText: String, polishedText: String, autoApply: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            if (autoApply) {
                replaceText(project, editor, polishedText)
                return@invokeLater
            }

            val factory = DiffContentFactory.getInstance()
            val originalContent = factory.create(originalText)
            val polishedContent = factory.create(polishedText)

            val request = SimpleDiffRequest(
                "AI Text Polisher — Review Changes",
                originalContent,
                polishedContent,
                "Original",
                "Polished"
            )

            DiffManager.getInstance().showDiff(project, request)

            // Ask user to apply after reviewing
            val result = Messages.showYesNoDialog(
                project,
                "Apply the polished text?",
                "AI Text Polisher",
                "Apply",
                "Dismiss",
                Messages.getQuestionIcon()
            )

            if (result == Messages.YES) {
                replaceText(project, editor, polishedText)
            }
        }
    }

    private fun replaceText(project: Project, editor: Editor, newText: String) {
        val selectionModel = editor.selectionModel
        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, newText)
        }
    }
}
