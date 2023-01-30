package www.iesmurgi.flipsamir

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import www.iesmurgi.flipsamir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private var currPopup : PopupWindow? = null
    private var currPopupId : Int = -1
    private var numX = 3
    private var numY = 3
    private var elements = 2
    private var modo = 0
    private var sonido = false
    private var vibracion = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)

        bind.tvEjeX.text = resources.getString(R.string.elementosX) +" " +3
        bind.tvEjeY.text = resources.getString(R.string.elementosY) + " "+3
        bind.tvColores.text = resources.getString(R.string.elementosTrama) +" " +2

        bind.seekbarX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                numX = p1 + 3
                bind.tvEjeX.text = resources.getString(R.string.elementosX) + " "+  numX
            }
        })

        bind.seekbarY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                numY = p1 + 3
                bind.tvEjeY.text = resources.getString(R.string.elementosY) + " "+ numY
            }
        })

        bind.seekbarColores.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                elements = p1 + 2
                bind.tvColores.text = resources.getString(R.string.elementosTrama) + " " + elements
            }
        })


        bind.rgModelo.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.rbColores){
                modo = 0
            }else{
                modo = 1
            }
        }

        bind.cbSonido.setOnClickListener{
            sonido = bind.cbSonido.isChecked
        }

        bind.cbVibracion.setOnClickListener{
            vibracion = bind.cbVibracion.isChecked
        }

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
                    bind.mainview.setOnTouchListener { _, event -> gestureDetectorMain.onTouchEvent(event) }
                    popup.showAtLocation(bind.mainview, Gravity.BOTTOM, 0, 0)
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
                    bind.mainview.setOnTouchListener { _, event -> gestureDetectorMain.onTouchEvent(event) }
                    popup.showAtLocation(bind.mainview, Gravity.BOTTOM, 0, 0)

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

    fun startPLay(view:View){
        val i = Intent(this,GameField::class.java)
        i.putExtra("xTiles", numX)
        i.putExtra("yTiles",numY)
        i.putExtra("elements",elements)
        i.putExtra("modo",modo)
        i.putExtra("sonido",sonido)
        i.putExtra("vibracion",vibracion)
        startActivity(i)
    }

}