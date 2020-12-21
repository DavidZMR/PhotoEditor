package com.example.photoeditor.filtros

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.example.photoeditor.R

class FiltroContraste : Filtro {

    override fun getNombre(): String {
        return "Contraste"
    }

    override fun getImagen(): Int {
        return R.drawable.contraste
    }

    override fun click(img: ImageView) : Bitmap {
        val oldBitmap = (img.getDrawable() as BitmapDrawable).bitmap
        var picw : Int = oldBitmap.width
        var pich : Int = oldBitmap.height

        val bitmap : Bitmap = oldBitmap.copy(oldBitmap.config, true)
        val pix = IntArray(picw * pich)
        bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)

        for (y in 0 until pich) {
            for (x in 0 until picw) {
                val index: Int = y * picw + x
                var R = pix[index] shr 16 and 0xff
                var G = pix[index] shr 8 and 0xff
                var B = pix[index] and 0xff

                R = procesar(R)
                G = procesar(G)
                B = procesar(B)

                pix[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
            }
        }

        bitmap.setPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap
    }

    private fun procesar(N: Int) : Int {
        val CONTRASTE = 50
        var pixel = 0.0
        var contrast: Double = (100.0 + CONTRASTE) / 100.0
        contrast *= contrast

        pixel = N / 255.0
        pixel -= 0.5
        pixel *= contrast
        pixel += 0.5
        pixel *= 255.0
        if (pixel < 0) pixel = 0.0
        if (pixel > 255) pixel = 255.0
        return pixel.toInt()
    }
}