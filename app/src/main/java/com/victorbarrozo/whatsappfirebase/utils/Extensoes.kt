package com.victorbarrozo.whatsappfirebase.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensage(mensagem: String){
    Toast.makeText(this, "$mensagem", Toast.LENGTH_SHORT).show()
}