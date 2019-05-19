/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.fxn.pixeditor.imageeditengine.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue

/**
 * Utility class for handling all of the Paint used to draw the CropOverlayView.
 */
object PaintUtil {

    // Private Constants ///////////////////////////////////////////////////////

    private val DEFAULT_CORNER_COLOR = Color.WHITE
    private val SEMI_TRANSPARENT = "#AAFFFFFF"
    private val DEFAULT_BACKGROUND_COLOR_ID = "#B0000000"
    /**
     * Returns the value of the line thickness of the border
     *
     * @return Float equivalent to the line thickness
     */
    val lineThickness = 3f
    /**
     * Returns the value of the corner thickness
     *
     * @return Float equivalent to the corner thickness
     */
    val cornerThickness = 5f
    private val DEFAULT_GUIDELINE_THICKNESS_PX = 1f

    // Public Methods //////////////////////////////////////////////////////////

    /**
     * Creates the Paint object for drawing the crop window border.
     *
     * @param context
     * the Context
     * @return new Paint object
     */
    fun newBorderPaint(context: Context): Paint {

        // Set the line thickness for the crop window border.
        val lineThicknessPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, lineThickness, context
                .resources.displayMetrics
        )

        val borderPaint = Paint()
        borderPaint.color = Color.parseColor(SEMI_TRANSPARENT)
        borderPaint.strokeWidth = lineThicknessPx
        borderPaint.style = Paint.Style.STROKE

        return borderPaint
    }

    /**
     * Creates the Paint object for drawing the crop window guidelines.
     *
     * @return the new Paint object
     */
    fun newGuidelinePaint(): Paint {

        val paint = Paint()
        paint.color = Color.parseColor(SEMI_TRANSPARENT)
        paint.strokeWidth = DEFAULT_GUIDELINE_THICKNESS_PX

        return paint
    }

    /**
     * Creates the Paint object for drawing the crop window guidelines.
     *
     * @return the new Paint object
     */
    fun newRotateBottomImagePaint(): Paint {

        val paint = Paint()
        paint.color = Color.WHITE
        paint.strokeWidth = 3f

        return paint
    }

    /**
     * Creates the Paint object for drawing the translucent overlay outside the
     * crop window.
     *
     * @param context
     * the Context
     * @return the new Paint object
     */
    fun newBackgroundPaint(context: Context): Paint {

        val paint = Paint()
        paint.color = Color.parseColor(DEFAULT_BACKGROUND_COLOR_ID)

        return paint
    }

    /**
     * Creates the Paint object for drawing the corners of the border
     *
     * @param context
     * the Context
     * @return the new Paint object
     */
    fun newCornerPaint(context: Context): Paint {

        // Set the line thickness for the crop window border.
        val lineThicknessPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, cornerThickness,
            context.resources.displayMetrics
        )

        val cornerPaint = Paint()
        cornerPaint.color = DEFAULT_CORNER_COLOR
        cornerPaint.strokeWidth = lineThicknessPx
        cornerPaint.style = Paint.Style.STROKE

        return cornerPaint
    }

}
