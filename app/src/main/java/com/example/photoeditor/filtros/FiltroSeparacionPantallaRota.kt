package com.example.photoeditor.filtros

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.example.photoeditor.R

class FiltroSeparacionPantallaRota : Filtro {

    override fun getNombre(): String {
        return "Pantalla rota"
    }

    override fun getImagen(): Int {
        return R.drawable.pantalla_rota
    }

    override fun click(img: ImageView) : Bitmap {
        val oldBitmap = (img.getDrawable() as BitmapDrawable).bitmap
        var picw : Int = oldBitmap.width
        var pich : Int = oldBitmap.height

        val bitmap : Bitmap = oldBitmap.copy(oldBitmap.config, true)
        val pix = IntArray(picw * pich)
        bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)

        var preR = 0
        var preG = 0
        var preB = 0

        for (y in 0 until pich) {
            for (x in 0 until picw) {
                val index: Int = y * picw + x
                var R = pix[index] shr 16 and 0xff
                var G = pix[index] shr 8 and 0xff
                var B = pix[index] and 0xff

                if ((y % 3 == 1 && x % 3 == 0) || (preR == preG && preG == preB && preB == 0)) {
                    preR = pix[index] shr 16 and 0xff
                    preG = pix[index] shr 8 and 0xff
                    preB = pix[index] and 0xff
                }

                pix[index] = -0x1000000 or (preR shl 16) or (preG shl 8) or preB
            }
        }

        bitmap.setPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap
    }
}