package com.victorbarrozo.whatsappfirebase.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.victorbarrozo.whatsappfirebase.databinding.ActivityCadastroBinding
import com.victorbarrozo.whatsappfirebase.model.Usuario
import com.victorbarrozo.whatsappfirebase.utils.exibirMensage

class CadastroActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToobar()
        inicializarEventosCliques()



    }

    private fun validarCampos(): Boolean {

        nome=binding.editNome.text.toString()
        email=binding.editEmail.text.toString()
        senha=binding.editSenha.text.toString()



        if (nome.isNotEmpty()) {
            binding.textInputNome.error= null
            if (email.isNotEmpty()){
                binding.textInputEmail.error= null
                if (senha.isNotEmpty()){
                    binding.textInputSenha.error= null
                    return true
                }else{
                    binding.textInputNome.error= "Prencha o seu nome!"
                    return false
                }
            }else{
                binding.textInputNome.error= "Prencha o seu nome!"
                return false
            }
        }else{
            binding.textInputNome.error= "Prencha o seu nome!"
            return false
        }
    }

    private fun inicializarEventosCliques() {
        binding.btnCadastrar.setOnClickListener {
            if(validarCampos()){
                cadastrarUsuario(nome,email,senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email,senha
        ).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful){
                val idUsuario = resultado.result.user?.uid
                if ( idUsuario!= null ){
                    val usuario= Usuario( nome,idUsuario,email )
                    salvarUsuarioFirestore(usuario)
                }
            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch (erroCredenciasInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciasInvalidas.printStackTrace()
                exibirMensage("E-mail incálido, digite um outro e-mail")
            }catch (erroUsuarioExistente: FirebaseAuthUserCollisionException){
                erroUsuarioExistente.printStackTrace()
                exibirMensage("E-mail já pertence a outro usúario")
            }catch (erroSenhaFraca: FirebaseAuthWeakPasswordException){
                erroSenhaFraca.printStackTrace()
                exibirMensage("Senha fraca, digite outra senha")
            }

        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {

        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensage("Sucesso ao fazer seu cadastro")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                exibirMensage("Erro ao fazer seu cadastro")
            }

    }


    private fun inicializarToobar() {
        val toobar = binding.includeToolbar.tbPrincipal
        setSupportActionBar( toobar )
        supportActionBar?.apply {
            title= "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}