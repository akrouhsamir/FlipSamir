package www.iesmurgi.flipsamir

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import www.iesmurgi.flipsamir.databinding.ActivityGameFieldBinding
import kotlin.random.Random

class GameField : AppCompatActivity() {
    private lateinit var bind: ActivityGameFieldBinding
    private var currPopup : PopupWindow? = null
    private var currPopupId : Int = -1
    private var colores = intArrayOf(R.drawable.ic_1c, R.drawable.ic_2c, R.drawable.ic_3c, R.drawable.ic_4c, R.drawable.ic_5c)
    private var numeros = intArrayOf(R.drawable.ic_1n, R.drawable.ic_2n, R.drawable.ic_3n, R.drawable.ic_4n, R.drawable.ic_5n)
    private var dibujos = intArrayOf()
    private var topTileX = 0
    private var topTileY = 0
    private var topElement = 0
    private var sonido = false
    private var vibracion = false
    private var ids = Array(topTileX) { IntArray(topTileY) }
    private var values = Array(topTileX) { IntArray(topTileY) }
    private var contador = 0
    private lateinit var mp: MediaPlayer
    private lateinit var vibratorService: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityGameFieldBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)

        val bundle = intent.extras
        if (bundle != null) {
            topTileX = bundle.getInt("xTiles")
            topTileY = bundle.getInt("yTiles")
            topElement = bundle.getInt("elements")
            sonido = bundle.getBoolean("sonido")
            vibracion = bundle.getBoolean("vibracion")
            var modo = bundle.getInt("modo")

            if (modo == 0){
                dibujos = colores.copyOf()
            }else{
              dibujos = numeros.copyOf()
            }
        }

        bind.cronometro.start()

        mp = MediaPlayer.create(this, R.raw.swipe3)

        vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        bind.mainGame.removeAllViews()


        bind.mainGame.post{
            val width = bind.mainGame.width
            val params = bind.mainGame.layoutParams
            params.height = width
            bind.mainGame.layoutParams = params

            ids = Array(topTileX) { IntArray(topTileY) }
            values = Array(topTileX) { IntArray(topTileY) }

            var ident = 0
            for(i in 0 until topTileY){
                var l2 = LinearLayout(this)
                l2.orientation = LinearLayout.HORIZONTAL
                for(j in 0 until topTileX){
                    var trama = miRandom(1)
                    values[j][i] = trama
                    var celda = CeldaView(this,j,i,topElement,trama,dibujos[trama])
                    ident++
                    celda.id = ident
                    ids[j][i] = ident
                    celda.layoutParams = LinearLayout.LayoutParams(0,(width-topTileY*10)/topTileY,1.0f)
                    celda.layoutParams = (celda.layoutParams as ViewGroup.MarginLayoutParams).apply {
                        setMargins(5,5,5,5)
                    }
                    celda.setOnClickListener{
                        hasClick(celda.x, celda.y)
                    }
                    l2.addView(celda)
                }
                bind.mainGame.addView(l2)
            }
        }
    }

    fun miRandom(max: Int): Int {
        return Random.nextInt(max)
    }

    fun hasClick(x:Int,y:Int){
        if (sonido){
            mp.start()
        }
        if(vibracion){
            vibratorService.vibrate(100L)
        }
        if(x==0 && y==0){
            changeView(0,1)
            changeView(1,0)
            changeView(1,1)
        }else if(x == 0 && y == topTileY -1){
            changeView(0,topTileY-2)
            changeView(1,topTileY-2)
            changeView(1,topTileY-1)
        }else if(x == topTileX-1 && y==0){
            changeView(topTileX-2,0)
            changeView(topTileX-1,1)
            changeView(topTileX-2,1)
        }else if(x == topTileX - 1 && y == topTileY-1){
            changeView(topTileX-1, topTileY-2)
            changeView(topTileX-2,topTileY-2)
            changeView(topTileX-2,topTileY-1)
        }else if(x == 0){
            changeView(x,y-1)
            changeView(x,y+1)
            changeView(x+1,y)
        }else if(y == 0){
            changeView(x+1,y)
            changeView(x-1,y)
            changeView(x,y+1)
        }else if(x == topTileX-1){
            changeView(x,y-1)
            changeView(x,y+1)
            changeView(x-1,y)
        }else if(y == topTileY-1){
            changeView(x+1,y)
            changeView(x-1,y)
            changeView(x,y-1)
        }else{
            changeView(x-1,y)
            changeView(x+1,y)
            changeView(x,y+1)
            changeView(x,y-1)
        }
        contador++;
        bind.tvContador.text = resources.getString(R.string.movimientos) + " " + contador
        checkIfFinished()
    }

    fun changeView(x:Int,y:Int){
        var celda = findViewById<Button>(ids[x][y])
        var newIndex = (celda as CeldaView).getNewIndex()
        values[x][y] = newIndex
        celda.setBackgroundResource(dibujos[newIndex])
        celda.invalidate()
    }

    fun checkIfFinished(){
        var finish = true
        var valor = values[0][0]
        for(col in values){
            for(celda in col){
                if (valor != celda){
                    finish = false
                }
            }
        }
        if(finish){
            bind.cronometro.stop()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Felicidades!")
            val message = "Has ganado en $contador movimientos en este tiempo ${bind.cronometro.text}"
            builder.setMessage(message)
            builder.setPositiveButton("OK") { _, _ ->
                finish()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun volver(view:View){
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(currPopupId != item.itemId){
            currPopup?.contentView?.startAnimation(currPopup?.let { animationPopup(it,applicationContext,R.anim.popup_anim_exit) })
            currPopupId = item.itemId
            when (item.itemId) {
                R.id.menu_settings -> {
                    return true
                }
                R.id.menu_play -> {
                    val contentView = layoutInflater.inflate(R.layout.popup_comojugar, null)
                    val popup = PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    currPopup = popup
                    popup.contentView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_trans))
                    val gestureDetectorPopup = gestureDetectorPopup(popup)
                    val gestureDetectorMain = gestureDetectorMain(popup)
                    popup.contentView.setOnTouchListener { _, event -> gestureDetectorPopup.onTouchEvent(event) }
                    bind.mainLayout.setOnTouchListener { _, event -> gestureDetectorMain.onTouchEvent(event) }
                    popup.showAtLocation(bind.mainLayout, Gravity.BOTTOM, 0, 0)
                    return true
                }
                R.id.menu_about -> {
                    val contentView = layoutInflater.inflate(R.layout.popup_acerca_de, null)
                    val popup = PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    currPopup = popup
                    popup.contentView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_trans))
                    val gestureDetectorPopup = gestureDetectorPopup(popup)
                    val gestureDetectorMain = gestureDetectorMain(popup)
                    popup.contentView.setOnTouchListener { _, event -> gestureDetectorPopup.onTouchEvent(event) }
                    bind.mainLayout.setOnTouchListener { _, event -> gestureDetectorMain.onTouchEvent(event) }
                    popup.showAtLocation(bind.mainLayout, Gravity.BOTTOM, 0, 0)

                    return true
                }
                else -> return super.onOptionsItemSelected(item)
            }
        }else{
            currPopup?.contentView?.startAnimation(currPopup?.let { animationPopup(it,applicationContext,R.anim.popup_anim_exit) })
            currPopupId = -1
        }
        return false
    }

    private fun gestureDetectorPopup(popup:PopupWindow):GestureDetector{
        return GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                if (e1 != null && e2 != null && e1.y < e2.y) {
                    popup.contentView.startAnimation(animationPopup(popup,applicationContext,R.anim.popup_anim_exit))
                    currPopupId = -1
                    return true
                }
                return false
            }
        })
    }

    private fun gestureDetectorMain(popup:PopupWindow):GestureDetector{
        return GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                if (e != null) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    val location = IntArray(2)
                    popup.contentView.getLocationOnScreen(location)
                    val popupX = location[0]
                    val popupY = location[1]
                    val popupWidth = popup.contentView.width
                    val popupHeight = popup.contentView.height
                    if (x < popupX || x > popupX + popupWidth || y < popupY || y > popupY + popupHeight) {
                        popup.contentView.startAnimation(animationPopup(popup,applicationContext,R.anim.popup_anim_exit))
                        currPopupId = -1
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun animationPopup(popup: PopupWindow,context: Context, id_anim: Int):Animation{
        return AnimationUtils.loadAnimation(context,id_anim).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    popup.dismiss()
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
    }
}


