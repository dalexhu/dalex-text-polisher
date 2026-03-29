package dev.dalex.textpolisher.ui

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JLabel

object DiffResultHandler {

    fun show(
        project: Project,
        editor: Editor,
        selectionStart: Int,
        selectionEnd: Int,
        originalText: String,
        polishedText: String,
        autoApply: Boolean,
        resultDisplay: String,
    ) {
        ApplicationManager.getApplication().invokeLater {
            if (autoApply) {
                replaceText(project, editor, selectionStart, selectionEnd, polishedText)
                return@invokeLater
            }

            when (resultDisplay) {
                "diff" -> showDiff(project, editor, selectionStart, selectionEnd, originalText, polishedText)
                else   -> showInline(project, editor, selectionStart, selectionEnd, originalText, polishedText)
            }
        }
    }

    private fun showInline(
        project: Project,
        editor: Editor,
        selectionStart: Int,
        selectionEnd: Int,
        originalText: String,
        polishedText: String,
    ) {
        // VIEW_DIFF exit code — DialogWrapper.NEXT_USER_EXIT_CODE is reserved for custom codes
        val VIEW_DIFF_CODE = DialogWrapper.NEXT_USER_EXIT_CODE

        val dialog = object : DialogWrapper(project) {
            init {
                title = "AI Text Polisher"
                setOKButtonText("Apply")
                init()
            }

            override fun createNorthPanel(): JComponent = JLabel("Polished result:")

            override fun createCenterPanel(): JComponent {
                val textArea = JBTextArea(polishedText).apply {
                    lineWrap = true
                    wrapStyleWord = true
                    isEditable = false
                    rows = 6
                }
                return JBScrollPane(textArea).apply { preferredSize = Dimension(520, 140) }
            }

            override fun createActions(): Array<Action> = arrayOf(
                okAction,
                object : AbstractAction("View Diff") {
                    override fun actionPerformed(e: ActionEvent) = close(VIEW_DIFF_CODE)
                },
                cancelAction,
            )
        }

        dialog.show()

        when (dialog.exitCode) {
            DialogWrapper.OK_EXIT_CODE -> replaceText(project, editor, selectionStart, selectionEnd, polishedText)
            VIEW_DIFF_CODE             -> showDiff(project, editor, selectionStart, selectionEnd, originalText, polishedText)
        }
    }

    private fun showDiff(
        project: Project,
        editor: Editor,
        selectionStart: Int,
        selectionEnd: Int,
        originalText: String,
        polishedText: String,
    ) {
        val factory = DiffContentFactory.getInstance()
        val request = SimpleDiffRequest(
            "AI Text Polisher — Review Changes",
            factory.create(originalText),
            factory.create(polishedText),
            "Original",
            "Polished"
        )

        // Modal: diff window is closed before the Apply dialog appears
        DiffManager.getInstance().showDiff(project, request, DiffDialogHints.MODAL)

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

    private fun replaceText(project: Project, editor: Editor, start: Int, end: Int, newText: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, newText)
        }
    }
}
