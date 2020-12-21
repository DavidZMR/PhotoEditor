package com.example.photoeditor.filtros

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.example.photoeditor.R
import java.lang.Exception

class FiltroZoomIn : Filtro {

    override fun getNombre(): String {
        return "Zoom +"
    }

    override fun getImagen(): Int {
        return R.drawable.blur_gaussian
    }

    override fun click(img: ImageView) : Bitmap {
        val oldBitmap = (img.getDrawable() as BitmapDrawable).bitmap
        var picw : Int = oldBitmap.width
        var pich : Int = oldBitmap.height

        val bitmap : Bitmap = oldBitmap.copy(oldBitmap.config, true)
        val pix = IntArray(picw * pich)
        bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)

        var R = 0
        var G = 0
        var B = 0

        var xChido = 0
        var yChido = 0

        for (y in 0 until pich) {
            for (x in 0 until picw) {
                var index = y * picw + x

                if (x % 100 == 0) {
                    try {
                        index = yChido++ * picw + xChido++
                        R = pix[index] shr 16 and 0xff
                        G = pix[index] shr 8 and 0xff
                        B = pix[index] and 0xff
                    } catch (e: Exception){}

                }

                pix[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
            }
        }

        bitmap.setPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap
    }
}