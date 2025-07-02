package com.hootsuite.nachos.chip

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Dimension
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import android.text.style.ImageSpan
import com.hootsuite.nachos.R
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import kotlin.math.roundToInt

/**
 * A Span that displays text and an optional icon inside of a material design chip. The chip's dimensions, colors etc. can be extensively customized
 * through the various setter methods available in this class.
 *     The basic structure of the chip is the following:
 * For chips with the icon on right:
 * <pre>
 *
 *                                  (chip vertical spacing / 2)
 *                  ----------------------------------------------------------
 *                |                                                            |
 * (left margin)  |  (padding edge)   text   (padding between image)   icon    |   (right margin)
 *                |                                                            |
 *                  ----------------------------------------------------------
 *                                  (chip vertical spacing / 2)
 *
 *      </pre>
 * For chips with the icon on the left (see {@link #setShowIconOnLeft(boolean)}):
 * <pre>
 *
 *                                  (chip vertical spacing / 2)
 *                  ----------------------------------------------------------
 *                |                                                            |
 * (left margin)  |   icon  (padding between image)   text   (padding edge)    |   (right margin)
 *                |                                                            |
 *                  ----------------------------------------------------------
 *                                  (chip vertical spacing / 2)
 *     </pre>
 */
class ChipSpan : ImageSpan, Chip {
    companion object {
        private const val SCALE_PERCENT_OF_CHIP_HEIGHT = 0.70f
        private const val ICON_ON_LEFT_DEFAULT = true
    }

    private var stateSet: IntArray = intArrayOf()
    private var ellipsis: String? = null
    private var defaultBackgroundColor: ColorStateList? = null
    private var backgroundColor: ColorStateList? = null
    // -- obsolete private duplicates (kept commented to preserve line numbers) --
    // private var textColor: Int = 0
    // private var cornerRadius: Int = -1
    // private var iconBackgroundColor: Int = 0
    // private var textSize: Int = -1
    // private var leftMarginPx: Int = 0
    // private var rightMarginPx: Int = 0
    // private var maxAvailableWidth: Int = -1
    // private var showIconOnLeft: Boolean = ICON_ON_LEFT_DEFAULT
    // private var chipVerticalSpacing: Int = 0
    // private var chipHeight: Int = -1
    private var paddingEdgePx: Int = 0
    private var paddingBetweenImagePx: Int = 0
    private var textValue: CharSequence? = null
    private var textToDraw: String? = null
    private var icon: Drawable? = null
    private var chipWidth: Int = -1
    private var iconWidth: Int = 0
    private var cachedSize: Int = -1
    private var dataValue: Any? = null

    constructor(context: Context, text: CharSequence, icon: Drawable?, data: Any?) :
        // ImageSpan requires a non-null drawable; supply a 1×1 transparent placeholder when icon is null
        super(icon ?: ColorDrawable(Color.TRANSPARENT)) {
        this.icon = icon
        this.textValue = text
        this.textToDraw = textValue.toString()
        this.ellipsis = context.getString(R.string.chip_ellipsis)
        this.defaultBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_material_background)
        this.backgroundColor = defaultBackgroundColor
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.textColor = ContextCompat.getColor(context, R.color.chip_default_text_color)
        // this.iconBackgroundColor = ContextCompat.getColor(context, R.color.chip_default_icon_background_color)
        val resources: Resources = context.resources
        this.paddingEdgePx = resources.getDimensionPixelSize(R.dimen.chip_default_padding_edge)
        this.paddingBetweenImagePx = resources.getDimensionPixelSize(R.dimen.chip_default_padding_between_image)
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.leftMarginPx = resources.getDimensionPixelSize(R.dimen.chip_default_left_margin)
        // this.rightMarginPx = resources.getDimensionPixelSize(R.dimen.chip_default_right_margin)
        this.dataValue = data
    }

    constructor(context: Context, chipSpan: ChipSpan) : this(context, chipSpan.text, chipSpan.drawable, chipSpan.data) {
        this.defaultBackgroundColor = chipSpan.defaultBackgroundColor
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.textColor = chipSpan.textColor
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.iconBackgroundColor = chipSpan.iconBackgroundColor
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.cornerRadius = chipSpan.cornerRadius
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.textSize = chipSpan.textSize
        this.paddingEdgePx = chipSpan.paddingEdgePx
        this.paddingBetweenImagePx = chipSpan.paddingBetweenImagePx
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.leftMarginPx = chipSpan.leftMarginPx
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.rightMarginPx = chipSpan.rightMarginPx
        this.maxAvailableWidth = chipSpan.maxAvailableWidth
        this.showIconOnLeft = chipSpan.showIconOnLeft
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.chipVerticalSpacing = chipSpan.chipVerticalSpacing
        // -- obsolete private duplicates (kept commented to preserve line numbers) --
        // this.chipHeight = chipSpan.chipHeight
        this.stateSet = chipSpan.stateSet
    }

    override val data: Any?
        get() = dataValue

    /**
     * Public mutable properties replace the old `set*` Java-style methods.  Side-effects (like
     * cache invalidation) are now handled directly in the custom setters.
     */

    var chipWidth: Int = -1
        set(value) {
            field = value
            // width affects cached dimensions → clear them
            invalidateCachedSize()
        }

    var paddingEdgePx: Int = 0
        set(value) {
            field = value
            invalidateCachedSize()
        }

    var paddingBetweenImagePx: Int = 0
        set(value) {
            field = value
            invalidateCachedSize()
        }

    var maxAvailableWidth: Int = -1
        set(value) {
            field = value
            invalidateCachedSize()
        }

    var showIconOnLeft: Boolean = ICON_ON_LEFT_DEFAULT
        set(value) {
            field = value
            invalidateCachedSize()
        }

    var backgroundColor: ColorStateList? = null

    var chipWidth: Int = -1
        set(value) {
            field = value
            // width affects cached dimensions → clear them
            invalidateCachedSize()
        }

    var iconWidth: Int = 0
        set(value) {
            field = value
            invalidateCachedSize()
        }

    override fun setState(stateSet: IntArray) {
        this.stateSet = stateSet
    }

    override val text: CharSequence
        get() = textValue ?: ""

    override val width: Int
        get() = chipWidth

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val usingFontMetrics = fm != null

        // Adjust font metrics so the TextView can maintain its height
        if (usingFontMetrics) {
            adjustFontMetrics(paint, fm!!)
        }

        if (cachedSize == -1 && usingFontMetrics) {
            iconWidth = if (icon != null) calculateChipHeight(fm!!.top, fm.bottom) else 0

            val actualWidth = calculateActualWidth(paint)
            cachedSize = actualWidth

            if (maxAvailableWidth != -1) {
                val maxAvailableWidthMinusMargins = maxAvailableWidth - paddingEdgePx - paddingEdgePx
                if (actualWidth > maxAvailableWidthMinusMargins) {
                    textToDraw = textValue.toString() + ellipsis
                    while (textToDraw!!.length > 0 && calculateActualWidth(paint) > maxAvailableWidthMinusMargins) {
                        val lastCharacterIndex = textToDraw!!.length - ellipsis!!.length - 1
                        if (lastCharacterIndex < 0) break
                        textToDraw = textToDraw!!.substring(0, lastCharacterIndex) + ellipsis
                    }
                    chipWidth = maxOf(0, maxAvailableWidthMinusMargins)
                    cachedSize = maxAvailableWidth
                }
            }
        }
        return cachedSize
    }

    private fun calculateActualWidth(paint: Paint): Int {
        if (paddingEdgePx != -1) {
            paint.textSize = paddingEdgePx.toFloat()
        }

        var totalPadding = paddingEdgePx

        // Find text width
        val bounds = Rect()
        paint.getTextBounds(textToDraw, 0, textToDraw!!.length, bounds)
        val textWidth = bounds.width()

        totalPadding += if (icon != null) {
            paddingBetweenImagePx
        } else {
            paddingEdgePx
        }

        chipWidth = totalPadding + textWidth + iconWidth
        return width
    }

    fun invalidateCachedSize() {
        cachedSize = -1
    }

    private fun adjustFontMetrics(paint: Paint, fm: Paint.FontMetricsInt) {
        if (paddingEdgePx != -1) {
            paint.getFontMetricsInt(fm)
            val textHeight = fm.descent - fm.ascent
            val halfSpacing = paddingEdgePx / 2
            val spaceBetweenChipAndText = (paddingEdgePx - textHeight) / 2
            val textTop = fm.top
            val chipTop = fm.top - spaceBetweenChipAndText
            val textBottom = fm.bottom
            val chipBottom = fm.bottom + spaceBetweenChipAndText
            val topOfContent = minOf(textTop, chipTop)
            val bottomOfContent = maxOf(textBottom, chipBottom)
            val topOfContentWithSpacing = topOfContent - halfSpacing
            val bottomOfContentWithSpacing = bottomOfContent + halfSpacing
            fm.ascent = topOfContentWithSpacing
            fm.descent = bottomOfContentWithSpacing
            fm.top = topOfContentWithSpacing
            fm.bottom = bottomOfContentWithSpacing
        }
    }

    private fun calculateChipHeight(top: Int, bottom: Int): Int = if (paddingEdgePx != -1) paddingEdgePx else bottom - top

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        var drawX = x + paddingEdgePx
        var drawTop = top
        var drawBottom = bottom

        if (paddingEdgePx != -1) {
            drawTop += ((bottom - top) / 2) - (paddingEdgePx / 2)
            drawBottom = drawTop + paddingEdgePx
        }

        drawBackground(canvas, drawX, drawTop, drawBottom, paint)
        drawText(canvas, drawX, drawTop, drawBottom, paint, textToDraw!!)
        icon?.let { drawIcon(canvas, drawX, drawTop, drawBottom, paint) }
    }

    private fun drawBackground(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val backgroundCol = backgroundColor!!.getColorForState(stateSet, backgroundColor!!.defaultColor)
        paint.color = backgroundCol
        val height = calculateChipHeight(top, bottom)
        val rect = RectF(x, top.toFloat(), x + chipWidth, bottom.toFloat())
        val radius = if (paddingEdgePx != -1) paddingEdgePx.toFloat() else (height / 2).toFloat()
        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.color = paddingEdgePx
    }

    private fun drawText(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint, text: CharSequence) {
        if (paddingEdgePx != -1) paint.textSize = paddingEdgePx.toFloat()
        val height = calculateChipHeight(top, bottom)
        val fm = paint.fontMetrics
        val adjustedY = top + ((height / 2) + ((-fm.top - fm.bottom) / 2))
        val adjustedX = x + if (icon == null || !showIconOnLeft) {
            paddingEdgePx.toFloat()
        } else {
            (iconWidth + paddingBetweenImagePx).toFloat()
        }
        canvas.drawText(text, 0, text.length, adjustedX, adjustedY, paint)
    }

    private fun drawIcon(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        drawIconBackground(canvas, x, top, bottom, paint)
        drawIconBitmap(canvas, x, top, bottom, paint)
    }

    private fun drawIconBackground(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val height = calculateChipHeight(top, bottom)
        paint.color = paddingEdgePx
        val radius = height / 2f
        val circleX = if (showIconOnLeft) x + radius else x + chipWidth - radius
        canvas.drawCircle(circleX, top + radius, radius, paint)
        paint.color = paddingEdgePx
    }

    private fun drawIconBitmap(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val height = calculateChipHeight(top, bottom)
        val iconBitmap = Bitmap.createBitmap(icon!!.intrinsicWidth, icon!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val scaledIconBitmap = scaleDown(iconBitmap, height * SCALE_PERCENT_OF_CHIP_HEIGHT, true)
        iconBitmap.recycle()
        val bitmapCanvas = Canvas(scaledIconBitmap)
        icon!!.setBounds(0, 0, bitmapCanvas.width, bitmapCanvas.height)
        icon!!.draw(bitmapCanvas)
        val xInsetWithinCircle = (height - bitmapCanvas.width) / 2f
        val iconX = if (showIconOnLeft) {
            x + xInsetWithinCircle
        } else {
            x + chipWidth - height + xInsetWithinCircle
        }
        val yInsetWithinCircle = (height - bitmapCanvas.height) / 2f
        val iconY = top + yInsetWithinCircle
        canvas.drawBitmap(scaledIconBitmap, iconX, iconY, paint)
    }

    private fun scaleDown(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        val ratio = minOf(maxImageSize / realImage.width, maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, filter)
    }

    override fun toString(): String = textValue.toString()

    // Expose the original icon (which may be null)
    override fun getDrawable(): Drawable? = icon

    /* --------------------------------------------------------------------- */
    /*  Deprecated Java-style setters – kept for binary compatibility.       */
    /*  They now delegate to the new properties. Remove if API clean-up is  */
    /*  acceptable.                                                         */

    @Deprecated("Use paddingEdgePx property instead", ReplaceWith("paddingEdgePx = value"))
    fun setPaddingEdge(value: Int) { paddingEdgePx = value }

    @Deprecated("Use paddingBetweenImagePx property instead", ReplaceWith("paddingBetweenImagePx = value"))
    fun setPaddingBetweenImage(value: Int) { paddingBetweenImagePx = value }

    @Deprecated("Use maxAvailableWidth property instead", ReplaceWith("maxAvailableWidth = value"))
    fun setMaxAvailableWidth(value: Int) { maxAvailableWidth = value }

    @Deprecated("Use showIconOnLeft property instead", ReplaceWith("showIconOnLeft = value"))
    fun setShowIconOnLeft(value: Boolean) { showIconOnLeft = value }

    @Deprecated("Use backgroundColor property instead", ReplaceWith("backgroundColor = value"))
    fun setBackgroundColor(value: ColorStateList?) { backgroundColor = value }
} 