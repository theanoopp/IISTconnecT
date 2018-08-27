package `in`.rgpvnotes.alert.myresource.dialog

import `in`.rgpvnotes.alert.myresource.R
import android.app.AlertDialog

import android.content.Context
import android.os.Bundle

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.qr_code_dialog.*


class QRDialog(context: Context,val code: String,val title: String) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.qr_code_dialog)
        setCanceledOnTouchOutside(false)

        dialogTitle.text = title

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrCodeView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }


    }





}