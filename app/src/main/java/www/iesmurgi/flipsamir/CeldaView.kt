package www.iesmurgi.flipsamir

import android.content.Context


class CeldaView(context: Context, x: Int, y: Int, topElementos: Int, index: Int, background: Int) : androidx.appcompat.widget.AppCompatButton(context) {
    var x: Int = x
    var y: Int = y
    var index: Int = index
    var topElementos: Int = topElementos
    var background : Int

    init {
        this.x = x
        this.y = y
        this.index = index
        this.topElementos = topElementos
        this.background = background

        this.setBackgroundResource(background)
    }

    fun getNewIndex(): Int {
        index++
        if (index == topElementos) {
            index = 0
        }
        return index
    }
}