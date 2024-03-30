package com.victorbarrozo.whatsappfirebase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.victorbarrozo.whatsappfirebase.databinding.ActivityMainBinding
import com.victorbarrozo.whatsappfirebase.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToobar()
        solicitarpermissoes()
    }

    private fun solicitarpermissoes() {

        //verificar se ja tem permissõo

        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //Lista permissões negadas


        val listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera) listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        if (!temPermissaoGaleria) listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)

        //Solicitar Multiplas Permissões

        if (listaPermissoesNegadas.isNotEmpty() ) {
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){permissoes->
                temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria
            }
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())

        }
    }

    private fun inicializarToobar() {
        val toobar = binding.includeToobarPerfil.tbPrincipal
        setSupportActionBar(toobar)
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}