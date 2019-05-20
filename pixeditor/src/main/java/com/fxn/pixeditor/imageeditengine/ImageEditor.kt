package com.fxn.pixeditor.imageeditengine

import android.app.Activity
import android.content.Intent
import android.widget.Toast

import java.io.File

object ImageEditor {
    val EDITOR_STICKER = 1
    val EDITOR_TEXT = 2
    val EDITOR_PAINT = 3
    val EDITOR_CROP = 4
    val EDITOR_FILTERS = 5

    val EXTRA_STICKER_FOLDER_NAME = "EXTRA_STICKER_FOLDER_NAME"
    val EXTRA_IS_TEXT_MODE = "EXTRA_IS_TEXT_MODE"
    val EXTRA_IS_PAINT_MODE = "EXTRA_IS_PAINT_MODE"
    val EXTRA_IS_STICKER_MODE = "EXTRA_IS_STICKER_MODE"
    val EXTRA_IS_CROP_MODE = "EXTRA_IS_CROP_MODE"
    val EXTRA_HAS_FILTERS = "EXTRA_HAS_FILTERS"
    val EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH"
    val EXTRA_ORIGINAL = "EXTRA_ORIGINAL"
    val EXTRA_CROP_RECT = "EXTRA_CROP_RECT"
    val EXTRA_START = "EXTRA_START_WHAT"

    val EXTRA_EDITED_PATH = "EXTRA_EDITED_PATH"

    val RC_IMAGE_EDITOR = 0x34

    class Builder(private val context: Activity, private val imagePath: String?) {
        private var stickerFolderName: String? = null
        private var enabledEditorText = true
        private var enabledEditorPaint = true
        private var enabledEditorSticker = true
        private var enableEditorCrop = true
        private var enableFilters = true

        fun setStickerAssets(folderName: String): Builder {
            this.stickerFolderName = folderName
            enabledEditorSticker = true
            return this
        }

        fun disable(editorType: Int): Builder {
            if (editorType == EDITOR_TEXT) {
                enabledEditorText = false
            } else if (editorType == EDITOR_PAINT) {
                enabledEditorPaint = false
            } else if (editorType == EDITOR_STICKER) {
                enabledEditorSticker = false
            } else if (editorType == EDITOR_CROP) {
                enableEditorCrop = false
            } else if (editorType == EDITOR_FILTERS) {
                enableFilters = false
            }
            return this
        }

        fun open() {
            if (imagePath != null && File(imagePath).exists()) {
                val intent = Intent(context, ImageEditActivity::class.java)
                intent.putExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME, stickerFolderName)
                intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, enabledEditorPaint)
                intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, enabledEditorSticker)
                intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, enabledEditorText)
                intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, enableEditorCrop)
                intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, enableFilters)
                intent.putExtra(ImageEditor.EXTRA_IMAGE_PATH, imagePath)
                context.startActivityForResult(intent, RC_IMAGE_EDITOR)
            } else {
                Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
