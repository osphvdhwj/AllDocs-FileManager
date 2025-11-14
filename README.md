# AllDocs-FileManager
A customizable open-source Android file manager with integrated PDF, document (Office), and archive (e.g., RAR) viewer capabilities, suitable for extension as an alldocumentreader-style app. Features centralized file browsing with support for multiple formats using modern open-source libraries.

## Core Features
- Clean file/folder browsing interface
- Open and view:
  - PDF documents (AndroidPdfViewer, afreakyelf/Pdf-Viewer)
  - Microsoft Office: DOCX/XLSX/PPTX (Apache POI, Andropen Office, docx4j)
  - Compressed archives: RAR/ZIP/7z (UnRar Tool, SevenZipJBinding, ZArchiver for reference)
  - Markdown and simple ebooks (Okular base/extension)
- No music/video media playback—docs/archives focus only
- Simple, user-focused UI (Compose)
- Extensible and developer-friendly codebase

## Getting Started
1. Clone this repo
2. Open with Android Studio
3. Add dependencies listed in `docs/COMPONENTS.md`

## License
Multi-license (component-specific): MIT/Apache/GPL. Review each library's license in `docs/COMPONENTS.md`.

## Credits
- AndroidPdfViewer, afreakyelf/Pdf-Viewer, docx4j-Android, Apache POI, UnRar Tool-Android, ZArchiver (GPL), Okular
- Inspiration: All Document Reader, Andropen Office, WPS Office, Office Documents Viewer

---
Initial architecture, docs, and plans in `/docs/`. AI-generated repo starter by request.
