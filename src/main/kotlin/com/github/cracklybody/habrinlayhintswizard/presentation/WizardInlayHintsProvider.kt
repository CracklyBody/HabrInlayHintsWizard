package com.github.cracklybody.habrinlayhintswizard.presentation

import com.github.cracklybody.habrinlayhintswizard.MyBundle
import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent

class WizardInlayHintsProvider : InlayHintsProvider<WizardInlayHintsProvider.Settings> {

    override val key: SettingsKey<Settings> = SettingsKey("WizardInlayHintsProvider")

    override val name: String = MyBundle.message("name")

    override val previewText: String? = null

    override fun createConfigurable(settings: Settings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override val cases: List<ImmediateConfigurable.Case> = emptyList()
            override val mainCheckboxText: String get() = "Enable my hint"
            override fun createComponent(listener: ChangeListener): JComponent {
                return Box(BoxLayout.X_AXIS)
            }
        }
    }


    override fun createSettings(): Settings = Settings()

    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): InlayHintsCollector? {
        return object : FactoryInlayHintsCollector(editor) {


            override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                // Получение объекта VirtualFile, связанного с Document
//                val virtualFile: VirtualFile? = FileDocumentManager.getInstance().getFile(editor.document)
//                val relativePath = getRelativePath(virtualFile, editor.project) ?: return true
//                val text = getText(editor, element).trim()


                // Используем PSI для определения типа переменной
                val variableType = getVariableType(element) ?: "Any"

                // Добавляем инициализацию переменной в конец строки
                val initializationText = " = $variableType()"
                val caretOffset = editor.caretModel.offset
//                editor.document.insertString(caretOffset, initializationText)

                if (element is PsiWhiteSpace && element.textContains('\n')) {
//                    sink.addInlineElement(editor.document.getLineEndOffset(editor.document.getLineNumber(element.textOffset)), true, combinedPresentation, true)
                }

                return true
            }
        }
    }

    private fun getVariableType(element: PsiElement): String? {
        return when (element) {
            is KtProperty -> {
                // Получаем тип переменной из объявления свойства
                val propertyType = element.typeReference?.let { it.text }
                // Возвращаем текст типа переменной
                propertyType
            }
            is KtNameReferenceExpression -> {
                // Получаем тип переменной из ссылки на переменную
                val declaration = element.getParentOfType<KtDeclaration>(true)
                // Если это объявление переменной, получаем тип из него
                if (declaration is KtProperty) {
                    declaration.typeReference?.let { it.text }
                } else {
                    null
                }
            }
            else -> null
        }
    }


//    fun getRelativePath(virtualFile: VirtualFile?, project: Project?): String? {
//        val projectBasePath = project?.basePath ?: return null
//        val filePath = virtualFile?.path ?: return null
//
//        return if (filePath.startsWith(projectBasePath)) {
//            filePath.substring(projectBasePath.length + 1).replace(File.separatorChar, '/')
//        } else {
//            null
//        }
//    }
//
//    fun getText(editor: Editor, element: PsiElement): String {
//        // Получаем всю строку
//        val lineNumber = editor.document.getLineNumber(element.textOffset)
//        val lineStart = editor.document.getLineStartOffset(lineNumber)
//        val lineEnd = editor.document.getLineEndOffset(lineNumber)
//        return editor.document.getText(TextRange(lineStart, lineEnd))
//    }

    class Settings
}