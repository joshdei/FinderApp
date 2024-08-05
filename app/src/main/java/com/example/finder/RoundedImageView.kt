import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private val cornerRadius = 20f // Adjust the radius as needed
    private val path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable as? BitmapDrawable
        val bitmap = drawable?.bitmap ?: return

        val width = width
        val height = height

        val rect = Rect(0, 0, width, height)
        val roundRect = RectF(rect)
        path.addRoundRect(roundRect, cornerRadius, cornerRadius, Path.Direction.CW)

        canvas?.clipPath(path)
        canvas?.drawBitmap(bitmap, null, rect, paint)

        super.onDraw(canvas)
    }
}
