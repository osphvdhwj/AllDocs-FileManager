# AllDocs-FileManager: Deep Research & Initial Planning

## Overview
AllDocs-FileManager aims to be a highly customizable, open-source Android file manager with integrated document viewing for PDF, Office (DOCX, XLSX, PPTX), and compressed archive (RAR/ZIP/7z) formats—without supporting videos/music—to serve as the foundation for a modern alldocumentreader.office.viewer.filereader-style utility.

## Core Requirements
- **File Explorer**: Modern file/folder navigation, search, and permissions handling for Android.
- **PDF Viewing**: Render and interact with PDFs natively.
- **Office Document Viewing**: Read/display DOCX, XLSX, PPTX files (basic formatting adequate).
- **Archive Viewing**: Open/extract RAR, ZIP, 7z, etc., inspect contents, and decompress on demand.
- **All-in-one UX**: Centralized interface—one tap to view any supported document or archive in place.
- **No video/music playback.**

## Chosen Tech Stack
- **Platform**: Android (Java/Kotlin)
- **File Core**: Jetpack Compose/AndroidX for UI, SAF for file access
- **PDF Viewer**: [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) (Apache 2.0, ~8k stars)
- **DOCX/XLSX/PPTX**: [Andropen Office](https://play.google.com/store/apps/details?id=com.andropenoffice.editor) for reference; [docx4j-Android](https://github.com/plutext/docx4j-android) (for DOCX), [Apache POI Android port](https://github.com/Abubakr077/apachepoiandroid)
- **PPT(X) Viewer**: Custom view using Apache POI for parsing, with basic slide rendering
- **RAR/ZIP/7z Support**: [ZArchiver APK](https://www.zarchiver.pro/) (GPL, for reference), [UnRar Tool (Android)](https://github.com/mahmoudgalal/UnRar-Tool-Android), [SevenZipJBinding] for 7z
- **Other**: Markdown/EPUB reader based on Okular/other FOSS projects for extensibility

## Library Shortlist
- **PDF**: 
  - AndroidPdfViewer (supported, maintained, works on local/remote files)
  - afreakyelf/Pdf-Viewer (lightweight alt)
- **Office**: 
  - Apache POI (best for spreadsheets, basic for DOCX/PPTX on Android)
  - docx4j-Android (DOCX)
- **Archive**:
  - UnRar Tool for RAR
  - java.util.zip / commons-compress for ZIP
  - SevenZipJBinding for 7z

## Architecture Plan
- Modular document/format detectors
- Intents/activities for each supported type
- Pluggable viewer components
- Respect Android storage permissions, SAF

## Next Steps
1. Prototype integration: PDF and DOCX (POI/docx4j)
2. Implement archive browsing and basic extraction
3. Smooth in-app document switching (tab, view pager, etc.)
4. Performance optimization: async file IO, caching, large file handling
5. UI/UX: Single-click open in-place, modern Compose UI, error fallback for unsupported types
6. Licensing audit for compliance (Apache 2.0/GPL/MIT)

## References & Inspiration
- AndroidPdfViewer, afreakyelf/Pdf-Viewer, docx4j-Android, Apache POI, UnRar Tool-Android, ZArchiver, Okular
- Andropen Office, All Document Reader, Office Documents Viewer, Open / LibreOffice, WPS Office (for feature reference only)

## Target Audience
- Developers who want a documented, upgradable open-source base for file/document managers on Android with out-of-the box support for PDF, Office, RAR/ZIP/7z, Markdown, and more.

---
*Created: 2025-11-13*
*Author: AI Research on request*
