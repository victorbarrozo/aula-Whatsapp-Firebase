package com.victorbarrozo.whatsappfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.victorbarrozo.whatsappfirebase.databinding.ActivityCadastroBinding
import com.victorbarrozo.whatsappfirebase.databinding.ActivityLoginBinding
import com.victorbarrozo.whatsappfirebase.databinding.ActivityMainBinding
import com.victorbarrozo.whatsappfirebase.utils.exibirMensage

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var email: String
    private lateinit var senha: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        inicializarEventosClique()
        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual!= null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validarCampos(): Boolean {

        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()) {
            binding.textInputLoginEmail.error = null
            if (senha.isNotEmpty()) {
                binding.textInputLoginSenha.error = null
                return true
            } else {
                binding.textInputLoginEmail.error = "Preencha o email"
                return false
            }
        } else {
            binding.textInputLoginSenha.error = "Preencha a senha"
            return false
        }
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
            }
        }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email,senha)
            .addOnSuccessListener {
                exibirMensage("Logado com sucesso!")
                startActivity(Intent(this, MainActivity::class.java))
            }.addOnFailureListener { erro ->
                try {
                    throw erro
                }catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException){
                    exibirMensage("E-mail não cadastrado")
                }
                catch (erroCredenciasInvalido: FirebaseAuthInvalidCredentialsException){
                    exibirMensage("E-mail ou senha invalida")
                }

            }
    }
}