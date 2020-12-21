package com.example.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.photoeditor.filtros.Filtro

class FiltroControlador : LinearLayout {

    private lateinit var txtTexto : TextView;
    private lateinit var imgFoto : ImageView;
    private lateinit var filtro : Filtro;

    constructor(ctx: Context) : super(ctx) {
        inicializar();
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        inicializar();
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        inicializar();
    }

    fun inicializar() {
        val li = LayoutInflater.from(context)
        li.inflate(R.layout.controlador, this, true)

        txtTexto = findViewById(R.id.txtTexto)
        imgFoto = findViewById(R.id.imgFoto)

        asignarEventos();
    }

    var listener : onClickFiltroListener? = null;
    fun setOnClickFiltroListener(click: (String) -> Unit) {
        listener = object:onClickFiltroListener {
            override fun onClickFiltro(filtro: String) {
                click(filtro)
            }
        }
    }

    fun asignarEventos() {
        this.imgFoto.setOnClickListener {
            listener?.onClickFiltro(txtTexto.text.toString())
        }
    }

    fun setFiltro(filtro: Filtro) : FiltroControlador {
        this.filtro = filtro
        imgFoto.setImageResource(filtro.getImagen())
        txtTexto.setText(filtro.getNombre())
        return this
    }

    fun getLayout() : LinearLayout {
        return this
    }

    fun convertImg(foto: ImageView) : Bitmap {
        return filtro.click(foto)
    }
}