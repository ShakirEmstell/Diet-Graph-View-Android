package com.shakir.a7diets.graph

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.ems.a7diets.Application.AppApplication
import com.ems.a7diets.R

class GraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    val kgs = arrayOf("100 Kg", "90 kg", "80 Kg", "70 Kg", "60 Kg", "50 Kg", "40 Kg", "30 Kg")
    val months = arrayOf(
        "Jan" to "93 Kg",
        "Feb" to "85 Kg",
        "Mar" to "89 Kg",
        "Apr" to "82 Kg",
        "May" to "75 Kg",
        "June" to "72 Kg",
    )
    val KG_REPLACER = " KG"
    val kgStart = kgs.first().replace(KG_REPLACER, "", true).toInt()
    val kgEnd = kgs.last().replace(KG_REPLACER, "", true).toInt()

    val horizontal_line_thickness = AppApplication.instance.resources.getDimension(R.dimen._1sdp)
    val vertical_line_bottom_padding = AppApplication.instance.resources.getDimension(R.dimen._4sdp)
    val vertical_line_thickness = AppApplication.instance.resources.getDimension(R.dimen._4sdp)
    val textSize1 = AppApplication.instance.resources.getDimension(R.dimen._9sdp)
    val textSize2 = AppApplication.instance.resources.getDimension(R.dimen._11sdp)


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
            for (i in 0 until kgs.size) {
                val y = (((kgs.size - i - 1) * graph_start_y) + ((i) * graph_end_y)) / (i + (kgs.size - i - 1))
                canvas?.drawRoundRect(RectF(graph_start_x, y, graph_end_x, y + horizontal_line_thickness), 6f, 6f, paintLineGrey)
                canvas.drawText(kgs.get(i),kgNameX,y+textSize2.div(4),paintTextWhite2Right)
            }



            var monthIndex = 0
            val div_size = months.size * 2
            var prev_dot_x=0f
            var prev_dot_y=0f
            for (i in 0..div_size) {
                if (i % 2 == 1) {
                    val x = (((div_size - i) * graph_start_x) + (i * graph_end_x)) / (i + (div_size - i))
                    val yBottom = graph_end_y - vertical_line_bottom_padding
                    val kg = months.get(monthIndex).second.replace(KG_REPLACER, "", true).toInt()
                    val yTop = (((graph_start_y * (kgEnd - kg)) + (graph_end_y * (kg - kgStart))) / (kgEnd - kgStart))
                    val circleX=x + (.5f * vertical_line_thickness)
                    val monthNameY=(graph_end_y+canvasHeight)/2
                    val isCurrent=monthIndex==months.size-1
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
                    canvas.drawText(months.get(monthIndex).second,circleX,yTop-textSize1,if (isCurrent) paintTextWhite1 else paintTextGrey1)
                    canvas.drawText(months.get(monthIndex).first,circleX,monthNameY,paintTextWhite2)
                    monthIndex++



                }
            }





        }


    }
//@formatter:on

    val paintLineGrey = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")

    }


    val paintLineWhite = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")

    }


    val paintTextGrey1 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")
        textSize = textSize1
        setTextAlign(Paint.Align.CENTER)

    }
    val paintTextWhite1 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize1
        setTextAlign(Paint.Align.CENTER)

    }


    val paintTextWhite2 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize2
        setTextAlign(Paint.Align.CENTER)

    }

    val paintTextWhite2Right = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffffff")
        textSize = textSize2
        setTextAlign(Paint.Align.RIGHT)

    }


    val paintCircleStroke = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C1E1AD")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }


    val paintCircleFill = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#96BC5D")

    }

    val paintCircleFillWhite = Paint(ANTI_ALIAS_FLAG).apply {
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
