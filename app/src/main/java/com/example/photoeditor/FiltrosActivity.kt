package com.example.photoeditor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.photoeditor.filtros.*
import java.io.File
import java.io.FileOutputStream


class FiltrosActivity : AppCompatActivity() {

    companion object {
        var appContext: Context? = null
            private set
    }

    private lateinit var btnRegresar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnDeshacer: Button
    private lateinit var btnAceptar: Button
    private lateinit var imgFoto: ImageView
    private lateinit var layoutContainer: LinearLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filtros)

        appContext = this

        var strUser: String? = intent.getStringExtra("imagen")

        btnRegresar = findViewById(R.id.btnRegresar)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnDeshacer = findViewById(R.id.btnDeshacer)
        btnAceptar = findViewById(R.id.btnAceptar)
        imgFoto = findViewById(R.id.imgFoto)
        layoutContainer = findViewById(R.id.layoutContainer)

        imgFoto.setImageURI(Uri.parse(strUser))

        //Guardará la imagen previa antes de meter el próximo filtro
        var bitmapUndo: Bitmap = (imgFoto.getDrawable() as BitmapDrawable).bitmap.copy(
            (imgFoto.getDrawable() as BitmapDrawable).bitmap.config,
            true
        )
        //Guardará la imagen tal y como cuando la seleccionó de la galeria o desde la cámara
        var bitmapOriginal: Bitmap = (imgFoto.getDrawable() as BitmapDrawable).bitmap.copy(
            (imgFoto.getDrawable() as BitmapDrawable).bitmap.config,
            true
        )

        //Regresamos al main
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java));
        }

        //Botón para "Deshacer"
        btnDeshacer.setOnClickListener {
            imgFoto.setImageBitmap(bitmapOriginal)//Cambiará el BitMap al que guardó ultimamente
        }

        //Botón para "Aceptar"
        btnAceptar.setOnClickListener {
            bitmapUndo = (imgFoto.getDrawable() as BitmapDrawable).bitmap.copy(
                (imgFoto.getDrawable() as BitmapDrawable).bitmap.config,
                true
            )
            Toast.makeText(this, "Filtro guardado.", Toast.LENGTH_SHORT).show()
        }

        //Botón para "Guardar"
        btnGuardar.setOnClickListener {
            guardarImagen()
        }

        //Añadimos nuestro arreglo de filtros que usará la app
        var filtros = arrayOf(
            FiltroControlador(this).setFiltro(FiltroNegativo()),
            FiltroControlador(this).setFiltro(FiltroGrises()),
            FiltroControlador(this).setFiltro(FiltroBrillo()),
            FiltroControlador(this).setFiltro(FiltroContraste()),
            FiltroControlador(this).setFiltro(FiltroGamma()),
            FiltroControlador(this).setFiltro(FiltroSeparacionRojo()),
            FiltroControlador(this).setFiltro(FiltroSeparacionVerde()),
            FiltroControlador(this).setFiltro(FiltroSeparacionAzul()),

            //Blurs
            /*
            FiltroControlador(this).setFiltro(FiltroBlurSmoothing()),
            FiltroControlador(this).setFiltro(FiltroBlurGaussian()),
            FiltroControlador(this).setFiltro(FiltroBlurSharpen()),
            FiltroControlador(this).setFiltro(FiltroBlurMeanRemoval()),
            FiltroControlador(this).setFiltro(FiltroBlurEmbossing()),
            FiltroControlador(this).setFiltro(FiltroBlurEdgeDetection()),*/

            //Otros
            FiltroControlador(this).setFiltro(FiltroSeparacionMacabro()),
            FiltroControlador(this).setFiltro(FiltroSeparacionArreglarBlancos()),
            FiltroControlador(this).setFiltro(FiltroSeparacionArreglarNegros()),
            FiltroControlador(this).setFiltro(FiltroSeparacionPixeleado()),
            FiltroControlador(this).setFiltro(FiltroSeparacionPantallaRota())
        )

        //Ciclamos todo el arreglo
        for (filtro in filtros) {
            layoutContainer.addView(filtro.getLayout())//Lo añadimos al HorizontalScrollView
            filtro.setOnClickFiltroListener {
                Toast.makeText(this, "Filtro ${it} aplicado", Toast.LENGTH_SHORT).show()

                imgFoto.setImageBitmap(bitmapUndo)//Usamos la imagen previa
                imgFoto.setImageBitmap(filtro.convertImg(imgFoto))//Le añadimos el efecto seleccionado
            }
        }

    }

    /**
     * Función que guarda la imagen y abre su vista.
     */
    private fun guardarImagen() {
        //Guardamos la imagen
        val draw = imgFoto.getDrawable() as BitmapDrawable
        val bitmap = draw.bitmap

        var outStream: FileOutputStream? = null
        val sdCard: File = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.getAbsolutePath().toString() + "/PhotoEditor")
        dir.mkdirs()//Creamos la carpeta en caso de que no exista para guardar las fotos editadas de esta APP
        val fileName =
            String.format("%d.png", System.currentTimeMillis())//Nombre
        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush()
        outStream.close()
        val install = Intent(Intent.ACTION_VIEW)
        install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        install.setDataAndType(Uri.fromFile(outFile), "image/*")
        val apkURI = FileProvider.getUriForFile(
            this, this.getApplicationContext()
                .getPackageName().toString() + ".provider", outFile
        )
        install.setDataAndType(apkURI, "image/*")
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.startActivity(install)
    }


}