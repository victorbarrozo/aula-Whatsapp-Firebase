package com.victorbarrozo.whatsappfirebase.model

import java.net.IDN

data class Usuario(
    var nome: String,
    var id: String,
    var email: String,
    var foto: String = ""
)
