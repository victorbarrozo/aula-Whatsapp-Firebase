package com.victorbarrozo.whatsappfirebase

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.victorbarrozo.whatsappfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToobar()

    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Realmente deseja sair?")
            .setNegativeButton("Não") { dialog, posicao -> }
            .setPositiveButton("sim") { dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }.create().show()
    }

    private fun inicializarToobar() {
        val toolbar = binding.tbMain.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.itemPerfil -> {
                            startActivity(Intent(applicationContext, PerfilActivity::class.java))
                        }

                        R.id.itemSair -> {
                            deslogarUsuario()
                        }
                    }
                    return true
                }

            }
        )
    }
}