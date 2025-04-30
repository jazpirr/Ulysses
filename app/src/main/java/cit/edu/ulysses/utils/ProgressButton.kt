package cit.edu.ulysses.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private var progress = 0f // 0.0 to 1.0
    private val animator = ValueAnimator.ofFloat(0f, 1f)

    init {
        setTextColor(Color.BLACK)
        animator.duration = 5000L // 5 seconds, adjust as needed
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
    }

    fun startProgress() {
        progress = 0f
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        val cornerRadius = 10f // Match your drawable's corner radius in pixels
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipPath(path)

        val progressWidth = width * progress
        val rect = RectF(progressWidth, 0f, width.toFloat(), height.toFloat())

        val paint = Paint().apply {
            color = Color.WHITE
        }
        canvas.drawRect(rect, paint)
    }

}
