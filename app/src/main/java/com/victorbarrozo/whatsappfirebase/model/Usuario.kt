package com.victorbarrozo.whatsappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.IDN

@Parcelize
data class Usuario(
    var nome: String = "",
    var id: String = "",
    var email: String = "",
    var foto: String = ""
): Parcelable
