package com.example.photoeditor

import android.Manifest.permission
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val CARPETA_RAIZ = ""
    private val RUTA_IMAGEN = CARPETA_RAIZ + "misFotos"
    val COD_SELECCIONA = 10
    val COD_FOTO = 20
    var botonCargar: Button? = null
    var imagen: ImageView? = null
    var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sleep(3000)
        setContentView(R.layout.activity_main)
        imagen = findViewById<View>(R.id.ivFoto) as ImageView
        botonCargar = findViewById<View>(R.id.btnTomarFoto) as Button
        if (validaPermisos()) {
            botonCargar!!.isEnabled = true
        } else {
            botonCargar!!.isEnabled = false
        }
        ActivityCompat.requestPermissions(this,
            arrayOf("Manifest.permission.WRITE_EXTERNAL_STORAGE"),1);
    }

    private fun validaPermisos(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (shouldShowRequestPermissionRationale(permission.CAMERA) ||
            shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)
        ) {
            cargarDialogoRecomendacion()
        } else {
            requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA), 100)
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                botonCargar!!.isEnabled = true
            } else {
                solicitarPermisosManual()
            }
        }
    }

    private fun solicitarPermisosManual() {
        val opciones = arrayOf<CharSequence>("si", "no")
        val alertOpciones: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?")
        alertOpciones.setItems(opciones,
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (opciones[i] == "si") {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        packageName, null
                    )
                    intent.data = uri
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Los permisos no fueron aceptados",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialogInterface.dismiss()
                }
            })
        alertOpciones.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun cargarDialogoRecomendacion() {
        val dialogo: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        dialogo.setTitle("Permisos Desactivados")
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App")
        dialogo.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissions(
                    arrayOf(
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.CAMERA
                    ), 100
                )
            })
        dialogo.show()
    }

    fun onclick(view: View?) {
        cargarImagen()
    }

    private fun cargarImagen() {
        val opciones = arrayOf<CharSequence>("Tomar Foto", "Cargar Imagen", "Cancelar")
        val alertOpciones: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alertOpciones.setTitle("Seleccione una Opción")
        alertOpciones.setItems(opciones,
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (opciones[i] == "Tomar Foto") {
                    tomarFotografia()
                } else {
                    if (opciones[i] == "Cargar Imagen") {
                        val intent = Intent(
                            Intent.ACTION_GET_CONTENT,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        )
                        intent.type = "image/"
                        startActivityForResult(
                            Intent.createChooser(
                                intent,
                                "Seleccione la Aplicación"
                            ), COD_SELECCIONA
                        )
                    } else {
                        dialogInterface.dismiss()
                    }
                }
            })
        alertOpciones.show()
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath()
        return image
    }

    val REQUEST_TAKE_PHOTO = 1
    var photoURI: Uri? = null




   private fun tomarFotografia() {
        val a = null
        Log.i("mensaje","tomar foto")
        val fileImagen = File(Environment.getExternalStorageDirectory().toString(),File.separator+"Pictures"+File.separator+RUTA_IMAGEN)
        Log.i("image","$fileImagen")
        var isCreada = fileImagen.isDirectory()
        var nombreImagen = ""
        if (!isCreada) {
            try{
                fileImagen.mkdir();
                Log.i("mkdir", fileImagen.mkdir().toString())
                isCreada= true
            }catch (e : SecurityException){
                Log.i("error", "$e")
            }

        }
        if (isCreada) {
            nombreImagen = (System.currentTimeMillis() / 1000).toString() + ".jpg"
            Log.i("image name","$nombreImagen")
        }
        path = Environment.getExternalStorageDirectory().toString()+"/" + File.separator + "Pictures" +
                File.separator + RUTA_IMAGEN + File.separator + nombreImagen
        val imagen = File(path)
        Log.i("ruta","$imagen")
        var intent: Intent
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        ////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authorities = applicationContext.packageName + ".provider"
            val imageUri = FileProvider.getUriForFile(this, authorities, imagen)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen))
        }
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)

        ////
    }
    val REQUEST_IMAGE_CAPTURE = 1;

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intent = Intent(this@MainActivity, FiltrosActivity::class.java);
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                COD_SELECCIONA -> {
                    val miPath = data!!.data
                    Log.i("rel foto","$miPath")
                    imagen!!.setImageURI(miPath)
                    intent.putExtra("imagen", data?.data.toString())
                    startActivity(intent);
                }
                REQUEST_IMAGE_CAPTURE -> {
                    Log.i("entra","ok")
                    MediaScannerConnection.scanFile(
                        this, arrayOf(path), null
                    ) { path, uri -> Log.i("Ruta de almacenamiento", "Path: $path") }
                    val bitmap = BitmapFactory.decodeFile(path)
                    imagen!!.setImageBitmap(bitmap)

                    intent.putExtra("imagen", path)

                    startActivity(intent);

                    /* var imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI)
                    ivFoto.setImageBitmap(imageBitmap)*/
                }
            }
        }
    }
}