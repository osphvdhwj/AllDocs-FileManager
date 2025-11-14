package com.alldocs.filemanager.util

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.io.File
import java.io.FileInputStream

object OfficeDocumentParser {
    
    fun parseDocument(file: File): String {
        return when (file.extension.lowercase()) {
            "docx" -> parseWordDocument(file)
            "xlsx" -> parseExcelDocument(file)
            "pptx" -> parsePowerPointDocument(file)
            else -> "Unsupported document format"
        }
    }
    
    private fun parseWordDocument(file: File): String {
        return try {
            FileInputStream(file).use { fis ->
                val document = XWPFDocument(fis)
                val text = StringBuilder()
                
                document.paragraphs.forEach { paragraph ->
                    text.append(paragraph.text)
                    text.append("\n\n")
                }
                
                document.close()
                text.toString().ifEmpty { "Document is empty" }
            }
        } catch (e: Exception) {
            "Error parsing Word document: ${e.message}"
        }
    }
    
    private fun parseExcelDocument(file: File): String {
        return try {
            FileInputStream(file).use { fis ->
                val workbook = XSSFWorkbook(fis)
                val text = StringBuilder()
                
                workbook.forEach { sheet ->
                    text.append("Sheet: ${sheet.sheetName}\n")
                    text.append("=".repeat(40))
                    text.append("\n\n")
                    
                    sheet.forEach { row ->
                        row.forEach { cell ->
                            val cellValue = when {
                                cell.cellType.name == "STRING" -> cell.stringCellValue
                                cell.cellType.name == "NUMERIC" -> cell.numericCellValue.toString()
                                cell.cellType.name == "BOOLEAN" -> cell.booleanCellValue.toString()
                                cell.cellType.name == "FORMULA" -> cell.cellFormula
                                else -> ""
                            }
                            text.append(cellValue)
                            text.append("\t")
                        }
                        text.append("\n")
                    }
                    text.append("\n")
                }
                
                workbook.close()
                text.toString().ifEmpty { "Spreadsheet is empty" }
            }
        } catch (e: Exception) {
            "Error parsing Excel document: ${e.message}"
        }
    }
    
    private fun parsePowerPointDocument(file: File): String {
        return try {
            FileInputStream(file).use { fis ->
                val presentation = XMLSlideShow(fis)
                val text = StringBuilder()
                
                presentation.slides.forEachIndexed { index, slide ->
                    text.append("Slide ${index + 1}\n")
                    text.append("=".repeat(40))
                    text.append("\n\n")
                    
                    slide.shapes.forEach { shape ->
                        if (shape is org.apache.poi.xslf.usermodel.XSLFTextShape) {
                            text.append(shape.text)
                            text.append("\n")
                        }
                    }
                    text.append("\n")
                }
                
                presentation.close()
                text.toString().ifEmpty { "Presentation is empty" }
            }
        } catch (e: Exception) {
            "Error parsing PowerPoint document: ${e.message}"
        }
    }
}