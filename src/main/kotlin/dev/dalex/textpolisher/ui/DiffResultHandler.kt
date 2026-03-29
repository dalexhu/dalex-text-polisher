package dev.dalex.textpolisher.ui

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel

object DiffResultHandler {

    private enum class Action { APPLY, VIEW_DIFF, CANCEL }

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
                else -> showInline(project, editor, selectionStart, selectionEnd, originalText, polishedText)
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
        val dialog = JDialog()
        dialog.title = "AI Text Polisher"
        dialog.isModal = true
        dialog.layout = BorderLayout(8, 8)

        var action = Action.CANCEL

        val textArea = JBTextArea(polishedText).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            rows = 6
        }

        val buttonPanel = JPanel().apply {
            val applyBtn = JButton("Apply").apply {
                addActionListener { action = Action.APPLY; dialog.dispose() }
            }
            val diffBtn = JButton("View Diff").apply {
                addActionListener { action = Action.VIEW_DIFF; dialog.dispose() }
            }
            val cancelBtn = JButton("Cancel").apply {
                addActionListener { dialog.dispose() }
            }
            add(applyBtn)
            add(diffBtn)
            add(cancelBtn)
            dialog.rootPane.defaultButton = applyBtn
        }

        dialog.add(JLabel("  Polished result:"), BorderLayout.NORTH)
        dialog.add(JBScrollPane(textArea).apply { preferredSize = Dimension(520, 140) }, BorderLayout.CENTER)
        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.pack()
        dialog.setLocationRelativeTo(null)
        dialog.isVisible = true

        when (action) {
            Action.APPLY -> replaceText(project, editor, selectionStart, selectionEnd, polishedText)
            Action.VIEW_DIFF -> showDiff(project, editor, selectionStart, selectionEnd, originalText, polishedText)
            Action.CANCEL -> Unit
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

        // Modal: diff window must be closed before the Apply dialog appears
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
