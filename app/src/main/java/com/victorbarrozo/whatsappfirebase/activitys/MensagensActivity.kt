package com.victorbarrozo.whatsappfirebase.activitys

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.victorbarrozo.whatsappfirebase.R
import com.victorbarrozo.whatsappfirebase.adapters.ConversasAdapter
import com.victorbarrozo.whatsappfirebase.databinding.ActivityMensagensBinding
import com.victorbarrozo.whatsappfirebase.databinding.ActivityPerfilBinding
import com.victorbarrozo.whatsappfirebase.model.Mensagem
import com.victorbarrozo.whatsappfirebase.model.Usuario
import com.victorbarrozo.whatsappfirebase.utils.Constantes
import com.victorbarrozo.whatsappfirebase.utils.exibirMensage

class MensagensActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
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
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosDestinatario()
        inicializarToobar()
        inicializarEventosClique()
        inicilizarRecyclerView()
        inicializarListeners()
    }

    private fun inicilizarRecyclerView() {
        with( binding ) {
            conversasAdapter = ConversasAdapter()
            rvMensagem.adapter =  conversasAdapter
            rvMensagem.layoutManager = LinearLayoutManager ( applicationContext )

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if ( idUsuarioRemetente!= null && idUsuarioDestinatario!= null ) {
            listenerRegistration = firestore
                .collection(Constantes.BD_MENSAGENS)
                .document( idUsuarioRemetente )
                .collection( idUsuarioDestinatario )
                .orderBy( "data", Query.Direction.ASCENDING )
                .addSnapshotListener { querySnapshot, erro ->

                    val listaMensagem = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents
                    documentos?.forEach {documentSnapshot ->

                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                        if ( mensagem != null ) {
                            listaMensagem.add( mensagem )
                            Log.i("exibicao_mensagem", mensagem.mensagem)
                        }
                    }
                    if (listaMensagem.isNotEmpty()){
                        conversasAdapter.adicionarLista( listaMensagem )
                    }
                }

        }
    }

    private fun salvarMensagem( textMensagem: String ) {

        if ( textMensagem.isNotEmpty() ) {
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if ( idUsuarioRemetente!= null && idUsuarioDestinatario!= null ) {
                val mensagem = Mensagem(
                    idUsuarioRemetente, textMensagem
                )
                //Salvar para o remetente
                salvarMensagemFirestore(
                    idUsuarioRemetente,idUsuarioDestinatario, mensagem
                )

                //Salvar para o destinatário
                salvarMensagemFirestore(
                    idUsuarioDestinatario ,idUsuarioRemetente, mensagem
                )

                binding.editMensagem.setText("")
            }
        }

    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String , idUsuarioDestinatario: String, mensagem: Mensagem
    ) {
        firestore
            .collection( Constantes.BD_MENSAGENS )
            .document( idUsuarioRemetente )
            .collection(idUsuarioDestinatario)
            .add( mensagem )
            .addOnFailureListener {
                exibirMensage("Erro ao enviar mensagem")
            }
    }

    private fun inicializarEventosClique() {
        binding.fabEnviar.setOnClickListener {

            val mensagem =  binding.editMensagem.text.toString()
            salvarMensagem( mensagem )
        }
    }

    private fun inicializarToobar() {
        val toobar = binding.tbMensagens
        setSupportActionBar( toobar )
        supportActionBar?.apply {
            title = ""
            if ( dadosDestinatario!= null ){
                binding.txtNome.text = dadosDestinatario!!.nome
                Picasso.get().load( dadosDestinatario!!.foto ).into( binding.imgFotoPerfil )
            }
            setDisplayHomeAsUpEnabled( true )
        }
    }

    private fun recuperarDadosDestinatario() {

        val extras = intent.extras
        if ( extras != null ) {

            val origem = extras.getString( Constantes.ORIGEM )
            if ( origem == Constantes.ORIGEM_CONTATO ){


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    dadosDestinatario = extras.getParcelable(
                        Constantes
                            .DADOS_DESTINTARIO, Usuario::class.java)
                }else{
                    dadosDestinatario = extras.getParcelable(Constantes.DADOS_DESTINTARIO)
                }

            }else if (origem == Constantes.ORIGEM_CONVERSA){
                //Recupera os dados da conversa



            }

        }

    }
}