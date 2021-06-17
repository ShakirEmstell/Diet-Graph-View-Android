package com.shakir.a7diets.graph

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.ems.a7diets.Application.AppApplication
import com.ems.a7diets.R
import java.text.SimpleDateFormat
import java.util.*

class GraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val DEFAULT_MIN_KG = 30
    private val DEFAULT_MAX_KG = 100
    private val GRAPH_END_MAX_KG = 150
    private val CIRCLE_POINT_MAX_KG = 155

    private val monthWeightList = arrayListOf<Pair<String, Int>>()
    private val kgList = arrayListOf<Int>()
    private var maxKg = DEFAULT_MAX_KG
    private var minKg = DEFAULT_MIN_KG
    private val horizontal_line_thickness =
        context.resources.getDimension(R.dimen._1sdp)
    private val vertical_line_bottom_padding =
        context.resources.getDimension(R.dimen._4sdp)
    private val vertical_line_thickness =
        context.resources.getDimension(R.dimen._4sdp)
    private val textSize1 =context.resources.getDimension(R.dimen._9sdp)
    private val textSize2 = context.resources.getDimension(R.dimen._10sdp)








    fun RoundUpTo10(value: Int): Int {
        return 10 * ((value + 9) / 10)
    }

    fun RoundDownTo10(value: Int): Int {
        return 10 * (value / 10)
    }


    fun refresh(data: List<Pair<String, Int>>) {
        maxKg = DEFAULT_MAX_KG
        minKg = DEFAULT_MIN_KG
        monthWeightList.clear()
        monthWeightList.addAll(data)
        if (monthWeightList.isEmpty())
            monthWeightList.addAll((-5..0).map {
               SimpleDateFormat("MMM", Locale.ENGLISH).format( Calendar.getInstance().apply { set(Calendar.MONTH,it) }.time) to -1
            })
        val minKgOfuser = data.map { it.second }.minOrNull() ?: DEFAULT_MIN_KG
        val maxKgOfuser = data.map { it.second }.maxOrNull() ?: DEFAULT_MAX_KG
        if (minKgOfuser < DEFAULT_MIN_KG)
            minKg = RoundDownTo10(minKgOfuser)
        if (maxKgOfuser > DEFAULT_MAX_KG)
            maxKg = RoundUpTo10(maxKgOfuser)
        if (maxKg>GRAPH_END_MAX_KG) maxKg=GRAPH_END_MAX_KG
        kgList.clear()
        kgList.addAll((maxKg downTo minKg).step(10))
        invalidate()
    }


    //@formatter:off
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            val canvasWidth = canvas.width
            val canvasHeight = canvas.height
            val graph_start_x = canvasWidth.toFloat() * (90f / 556f) /*values  measured from xd screen */
            val graph_end_x = canvasWidth.toFloat() * (541f / 556f)
            val graph_start_y = canvasHeight.toFloat() * (50f / 449f)
            val graph_end_y = canvasHeight.toFloat() * (387f / 449f)

            val kgNameX /*right aligned */ = canvasWidth.toFloat() * (90f / 686f)
            for (i in 0 until kgList.size) {
                val y = (((kgList.size - i - 1) * graph_start_y) + ((i) * graph_end_y)) / (i + (kgList.size - i - 1))
                canvas?.drawRoundRect(RectF(graph_start_x, y, graph_end_x, y + horizontal_line_thickness), 6f, 6f, paintLineGrey)
                canvas.drawText("${kgList.get(i)} Kg",kgNameX,y+textSize2.div(4),paintTextWhite2Right)
            }



            var monthIndex = 0
            val div_size = monthWeightList.size * 2
            var prev_dot_x=0f
            var prev_dot_y=0f
            for (i in 0..div_size) {
                if (i % 2 == 1) {
                    val x = (((div_size - i) * graph_start_x) + (i * graph_end_x)) / (i + (div_size - i))
                    val yBottom = graph_end_y - vertical_line_bottom_padding
                    var kg = monthWeightList.get(monthIndex).second
                    var kgString ="$kg Kg"
                    val circleX=x + (.5f * vertical_line_thickness)
                    val monthNameY=(graph_end_y+canvasHeight)/2
                    if (kg>0){
                        if (kg>CIRCLE_POINT_MAX_KG) kg=CIRCLE_POINT_MAX_KG
                        val yTop = (((graph_start_y * (minKg - kg)) + (graph_end_y * (kg - maxKg))) / (minKg - maxKg))
                        val isCurrent=monthIndex==monthWeightList.size-1
                        if (prev_dot_x!=0f&&prev_dot_y!=0f){
                            canvas?.drawLine(prev_dot_x, prev_dot_y, circleX, yTop,  paintLineGrey)
                        }
                        prev_dot_x=circleX
                        prev_dot_y=yTop
                        canvas?.drawRoundRect(RectF(x, yTop, x + vertical_line_thickness, yBottom), 6f, 6f, if (isCurrent) paintLineWhite else paintLineGrey)
                        if (isCurrent){
                            canvas.drawCircle(circleX, yTop, vertical_line_thickness * 1.2f, paintCircleFillWhite)
                        }else{
                            canvas.drawCircle(circleX, yTop, vertical_line_thickness * 1.2f, paintCircleFill)
                            canvas.drawCircle(circleX, yTop, vertical_line_thickness * 1.2f, paintCircleStroke)
                        }
                        canvas.drawText(kgString,circleX,yTop-textSize1,if (isCurrent) paintTextWhite1 else paintTextGrey1)
                    }
                    canvas.drawText(monthWeightList.get(monthIndex).first,circleX,monthNameY,paintTextWhite2)
                    monthIndex++



                }
            }





        }


    }
//@formatter:on

    private val paintLineGrey = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")

    }


    private val paintLineWhite = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")

    }


    private val paintTextGrey1 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")
        textSize = textSize1
        setTextAlign(Paint.Align.CENTER)

    }
    private val paintTextWhite1 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize1
        setTextAlign(Paint.Align.CENTER)

    }


    private val paintTextWhite2 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize2
        setTextAlign(Paint.Align.CENTER)

    }

    private val paintTextWhite2Right = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize2
        setTextAlign(Paint.Align.RIGHT)

    }


    private val paintCircleStroke = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }


    private val paintCircleFill = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#96BC5D")

    }

    private val paintCircleFillWhite = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        if (Build.VERSION.SDK_INT >= 29)
            setShadowLayer(5.5f, 6.0f, 6.0f, 0x80000000)
        setLayerType(LAYER_TYPE_SOFTWARE, this)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        println("widthMeasureSpec $widthMeasureSpec heightMeasureSpec $heightMeasureSpec")
    }


}
