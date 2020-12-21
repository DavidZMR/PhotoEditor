package com.example.photoeditor.filtros

import android.graphics.Bitmap
import android.widget.ImageView

interface Filtro {
    fun click(img: ImageView): Bitmap
    fun getImagen() : Int
    fun getNombre() : String
}