package com.victorbarrozo.whatsappfirebase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.victorbarrozo.whatsappfirebase.databinding.ActivityMainBinding
import com.victorbarrozo.whatsappfirebase.databinding.ActivityPerfilBinding
import com.victorbarrozo.whatsappfirebase.utils.exibirMensage

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if ( uri != null ) {
            binding.imgPerfil.setImageURI( uri )
            uploadImagemStorage( uri )
        }else{
            exibirMensage("Nenhuma imagem selecionada")
        }
    }

    private fun uploadImagemStorage(uri: Uri) {

        val idUsuario = firebaseAuth.currentUser?.uid
        if ( idUsuario != null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child("idusuario")
                .child("perfil.jpg")
                .putFile( uri )
                .addOnSuccessListener {task->
                    exibirMensage( "Sucesso ao fazer upload" )
                    task.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener {  url->
                            val dados =  mapOf(
                                "foto" to url.toString()
                            )
                            atualizarDadosPerfil( idUsuario, dados)
                        }?.addOnFailureListener {
                        }
                }.addOnFailureListener {
                    exibirMensage( "Erro ao fazer upload" )
                }
        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore.collection("usuarios")
            .document( idUsuario )
            .update( dados )
            .addOnSuccessListener {
                exibirMensage( "Sucesso ao atualizar perfil" )
            }.addOnFailureListener {
                exibirMensage( "Erro ao atualizar perfil" )
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToobar()
        solicitarpermissoes()
        iniciarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciasUsuarios()
    }

    private fun recuperarDadosIniciasUsuarios() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if ( idUsuario != null ) {
            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot->
                    val dadosUsuarios = documentSnapshot.data
                    if ( dadosUsuarios != null) {
                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String
                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()) {
                            Picasso.get().load(foto).into(binding.imgPerfil)
                        }
                    }
                }
        }
    }

    private fun iniciarEventosClique() {
        binding.fabAdcionarFoto.setOnClickListener {
            if (temPermissaoGaleria) {
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirMensage("Não tem permissão galeria")
                solicitarpermissoes()
            }
        }

        binding.btnAtualizarPerfil.setOnClickListener {
            val idUsuario = firebaseAuth.currentUser?.uid
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if (nomeUsuario.isNotEmpty()) {
                if ( idUsuario != null ) {
                    val dados =  mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil( idUsuario, dados)
                }
            }else{
                exibirMensage("Preencha nome do usuário")
            }
        }
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