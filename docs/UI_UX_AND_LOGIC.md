# UI/UX and App Logic Design

## User Experience Principles
- Simple, modern Compose UI focusing on fast browsing and clear file type icons
- Central home screen: folders, file search, recent files, quick shortcuts (Downloads, Documents, Archive, etc.)
- Each document format uses a custom activity/viewer for best UX
- File actions: Open, Rename, Delete, Share, Extract (archives only)
- Fast file preview by tapping file, with fallback to external apps if unsupported
- Smooth in-app navigation (tabbed or drawer UI for switching folders/types)
- Minimal onboarding, “how-to” dialog on first launch

## Core Screens
- **Home**: Folder tree, quick actions, file search bar
- **Viewer**: Renders PDF/DOCX/PPTX/XLSX/Archive directly; toolbar: share, print, info, jump to page, basic annotation highlights for PDF
- **Archive**: List archive contents, extract single/multiple files, view archive info
- **Settings**: Theme, cache size, clear history, show/hide hidden files, file type associations

## Workflow (Logic)
1. Launch: request SAF permissions per Android’s requirements; load storage structure
2. User can browse or search files/folders
3. On file click, detect format (by file extension > lightweight scan if needed)
4. Route to the correct viewer: PDF, Office, Archive, Markdown/Epub (future: plug in more)
5. Show loading dialog as files load; async and background workers handle large file IO; cancelable tasks
6. For archives, show file list with extract/download options, password prompt if protected
7. If a format is not natively supported, offer to open in external app
8. Caching for big files (thumbnails, last-viewed page, etc.)
9. Error handling: user-friendly dialogs and logs
10. All file/view state managed by ViewModel layer for robust reactivity

## Folder Structure Proposal
- `app/` - Main Android project code
- `ui/` - Composables, screens, icon assets
- `core/` - File logic, type detection, archive/PDF helpers
- `docs/` - Architecture, research, changelogs
- `viewers/` - Integrations for PDF, Office, Archive

## Design Notes
- Start with Material 3 and Android’s standard Compose navigation/components
- Built-in file icons for each format (PDF, DOCX, XLS, PPT, RAR, ZIP, etc.)
- Unified color palette for clean, professional look
- Extensive error handling and helpful tooltips for new users

---
This plan covers initial UX/UI and logic design structure, guiding prototype → MVP → extensible polished app ready for broad Android deployment.