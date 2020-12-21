package com.example.photoeditor.filtros

import android.graphics.Bitmap


class ConvolucionMatriz {

    /**
     * Basado en el código de
     * https://homepages.inf.ed.ac.uk/rbf/HIPR2/flatjavasrc/Convolution.java
     *
     * Clase arreglada, adaptada y optimizada por:
     * Rodrigo Maafs Atilano
     */

    /**
     * Referencias:
     * https://www.youtube.com/watch?v=8rrHTtUzyZA&ab_channel=TheJuliaProgrammingLanguage
     * https://ramok.tech/2018/09/27/convolution-in-java/
     * https://ai.stanford.edu/~syyeung/cvweb/tutorial1.html
     * https://www.codeproject.com/Articles/2008/Image-Processing-for-Dummies-with-C-and-GDI-Part-2
     */

    private val SIZE = 3
    private var matriz: Array<IntArray> = Array(SIZE) { IntArray(SIZE) }
    var Factor = 1
    var Offset = 1

    fun setMatriz(config: Array<IntArray>) {
        for (x in 0 until SIZE) {
            for (y in 0 until SIZE) {
                matriz[x][y] = config[x][y]
            }
        }
    }

    fun convolucion(result: Bitmap) : Bitmap {
        var picw : Int = result.width
        var pich : Int = result.height
        val bitmap : Bitmap = result.copy(result.config, true)
        val pix = IntArray(picw * pich)
        bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)

        var sumaR = 0
        var sumaG = 0
        var sumaB = 0
        var index = 0
        var R = 0
        var G = 0
        var B = 0

        var flag = 2;

        for (x in 1 until picw - 1) {
            for (y in 1 until pich - 1) {

                sumaR = 0
                sumaG = 0
                sumaB = 0
                for (i in -1 until 2) {
                    for (j in -1 until 2) {
                        index = (y + j) * picw + (x + i)
                        sumaR += (pix[index] shr 16 and 0xff) * matriz[i + 1][j + 1]
                        sumaG += (pix[index] shr 8 and 0xff) * matriz[i + 1][j + 1]
                        sumaB += (pix[index] and 0xff) * matriz[i + 1][j + 1]

                        if (flag > 0) {
                            println("${x} X ${y}, ${(y + j)} X ${(x + i)} \t[${i + 1} X ${j + 1}]")
                        }
                    }
                }

                println("-----------")
                flag--

                index = (y) * picw + (x)
                R = validarValor(sumaR)
                G = validarValor(sumaG)
                B = validarValor(sumaB)

                pix[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
            }
        }

        bitmap.setPixels(pix, 0, picw, 0, 0, picw, pich);
        return bitmap
    }

    private fun validarValor(sumatoria: Int) : Int {
        var suma = sumatoria / Factor + Offset
        if (suma < 0.0) {
            suma = -suma
        }
        if (suma < 0) {
            suma = 0
        } else if (suma > 255) {
            suma = 255
        }
        return suma
    }
}