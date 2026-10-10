package com.questrings.data.qr

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeGenerator @Inject constructor() {

    fun generateBitmap(content: String, sizePx: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
        )
        val matrix = QRCodeWriter().encode(
            content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints
        )
        // Dark squares = #FFFFFF, background = #111116 (dark cyberpunk theme aesthetic)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) 0xFFFFFFFF.toInt() else 0xFF111116.toInt()
                )
            }
        }
        return bitmap
    }

    fun generateImageBitmap(content: String, sizePx: Int = 512): ImageBitmap {
        return generateBitmap(content, sizePx).asImageBitmap()
    }
}
