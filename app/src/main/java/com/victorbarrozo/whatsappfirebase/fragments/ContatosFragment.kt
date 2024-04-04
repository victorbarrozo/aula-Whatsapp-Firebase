package com.victorbarrozo.whatsappfirebase.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.victorbarrozo.whatsappfirebase.R
import com.victorbarrozo.whatsappfirebase.databinding.ActivityPerfilBinding
import com.victorbarrozo.whatsappfirebase.databinding.FragmentContatosBinding
import com.victorbarrozo.whatsappfirebase.model.Usuario


class ContatosFragment : Fragment() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var binding: FragmentContatosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        firestore.collection( "usuarios" )
            .addSnapshotListener { querySnapshot, erro ->
                val documentos = querySnapshot?.documents

                documentos?.forEach {documentSnapshot ->

                    val usuario = documentSnapshot.toObject( Usuario::class.java )
                    Log.i( "test_contatos", "test nome: ${usuario?.nome}" )
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}