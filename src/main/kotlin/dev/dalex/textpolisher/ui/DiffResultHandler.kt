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

    fun show(
        project: Project,
        editor: Editor,
        selectionStart: Int,
        selectionEnd: Int,
        originalText: String,
        polishedText: String,
        autoApply: Boolean,
    ) {
        ApplicationManager.getApplication().invokeLater {
            if (autoApply) {
                replaceText(project, editor, selectionStart, selectionEnd, polishedText)
                return@invokeLater
            }

            val factory = DiffContentFactory.getInstance()
            val request = SimpleDiffRequest(
                "AI Text Polisher — Review Changes",
                factory.create(originalText),
                factory.create(polishedText),
                "Original",
                "Polished"
            )

            DiffManager.getInstance().showDiff(project, request)

            val result = Messages.showYesNoDialog(
                project,
                "Apply the polished text?",
                "AI Text Polisher",
                "Apply",
                "Dismiss",
                Messages.getQuestionIcon()
            )

            if (result == Messages.YES) {
                replaceText(project, editor, selectionStart, selectionEnd, polishedText)
            }
        }
    }

    private fun replaceText(project: Project, editor: Editor, start: Int, end: Int, newText: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, newText)
        }
    }
}
