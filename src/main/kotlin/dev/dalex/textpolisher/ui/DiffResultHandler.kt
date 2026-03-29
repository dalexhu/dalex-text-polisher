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
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Point
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JMenuItem

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
        val textArea = JBTextArea(polishedText).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            rows = 5
        }

        // Bottom bar: [Apply]  [⋮]
        val bottomBar = JPanel(FlowLayout(FlowLayout.RIGHT, 4, 4))
        val applyBtn = JButton("Apply")
        val moreBtn = JButton("⋯")

        bottomBar.add(applyBtn)
        bottomBar.add(moreBtn)

        val panel = JPanel(BorderLayout(0, 4)).apply {
            add(JBScrollPane(textArea).apply { preferredSize = Dimension(420, 110) }, BorderLayout.CENTER)
            add(bottomBar, BorderLayout.SOUTH)
        }

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, applyBtn)
            .setTitle("AI Text Polisher")
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()

        applyBtn.addActionListener {
            popup.cancel()
            replaceText(project, editor, selectionStart, selectionEnd, polishedText)
        }

        moreBtn.addActionListener {
            val menu = JPopupMenu()
            menu.add(JMenuItem("View Diff").apply {
                addActionListener {
                    popup.cancel()
                    showDiff(project, editor, selectionStart, selectionEnd, originalText, polishedText)
                }
            })
            menu.add(JMenuItem("Dismiss").apply {
                addActionListener { popup.cancel() }
            })
            menu.show(moreBtn, 0, moreBtn.height)
        }

        // Show the popup just below the selection end
        val selectionEndPoint = editor.offsetToXY(selectionEnd)
        val lineHeight = editor.lineHeight
        val showPoint = Point(selectionEndPoint.x, selectionEndPoint.y + lineHeight)
        popup.show(RelativePoint(editor.contentComponent, showPoint))
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
