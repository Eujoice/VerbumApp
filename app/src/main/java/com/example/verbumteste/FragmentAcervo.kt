package com.example.verbumteste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.verbumteste.databinding.FragmentAcervoBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log

class FragmentAcervo : Fragment(R.layout.fragment_acervo) {

    private var _binding: FragmentAcervoBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAcervoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerBanners)
        val dotsLayout = view.findViewById<LinearLayout>(R.id.dotsIndicator)

        val images = listOf(
            R.drawable.bannerpercy,
            R.drawable.tartarugasatelaembaixobanner,
            R.drawable.bibliotecadameianoitebanner
        )
        val listaGeneros = listOf(
            Genero("Romance", "#E0339C"),
            Genero("Ficção", "#6FBE3E"),
            Genero("Clássicos", "#E1392E"),
            Genero("Suspense", "#5B4FE0")
        )

        val geroAdapter = GeneroAdapter2(listaGeneros) { genero ->
            Toast.makeText(requireContext(), "Gênero: ${genero.nome}", Toast.LENGTH_SHORT).show()
        }


        binding.recyclerGeneros.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGeneros.adapter = geroAdapter
        binding.recyclerGeneros.isNestedScrollingEnabled = false

        binding.recyclerGeneros.isNestedScrollingEnabled = false

        binding.chipPrincipal.setOnClickListener {
            binding.chipPrincipal.setBackgroundResource(R.drawable.gb_chip_selecionado)
            binding.chipPrincipal.setTextColor(Color.WHITE)
            binding.chipGeneros.setBackgroundResource(R.drawable.bg_chip_nao_selecionado)
            binding.chipGeneros.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_default))
            binding.layoutConteudoPrincipal.visibility = View.VISIBLE
            binding.recyclerGeneros.visibility = View.GONE
        }

        binding.chipGeneros.setOnClickListener {
            try {
                binding.chipGeneros.setBackgroundResource(R.drawable.gb_chip_selecionado)
                binding.chipGeneros.setTextColor(Color.WHITE)
                binding.chipPrincipal.setBackgroundResource(R.drawable.bg_chip_nao_selecionado)
                binding.chipPrincipal.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_default))

                binding.layoutConteudoPrincipal.visibility = View.GONE
                binding.recyclerGeneros.visibility = View.VISIBLE
            } catch (t: Throwable) {
                // Se falhar antes da renderização, avisa na tela o erro exato
                Log.e("FragmentAcervo", "Erro no clique de gêneros", t)
                Toast.makeText(requireContext(), "Erro: ${t.javaClass.simpleName}", Toast.LENGTH_LONG).show()
            }
        }

        viewPager.adapter = BannerAdapter(images) { realIndex ->
            val tituloLivroBuscado = when (realIndex) {
                0 -> "Percy Jackson e os Olimpianos: O Ladrão de Raios"
                1 -> "Tartarugas Até Lá Embaixo"
                2 -> "A Biblioteca da Meia-Noite"
                else -> ""
            }

            if (tituloLivroBuscado.isNotEmpty()) {
                db.collection("obras")
                    .whereEqualTo("titulo", tituloLivroBuscado)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val livroClicado = result.documents.first().toObject(Livro::class.java)

                            if (livroClicado != null) {
                                val bundle = Bundle().apply {
                                    putSerializable("CHAVE_LIVRO", livroClicado)
                                }
                                findNavController().navigate(R.id.action_fragment_acervo_to_detalhesLivroFragment, bundle)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Livro do banner não encontrado!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao carregar livro do banner", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        viewPager.offscreenPageLimit = 3

        val startPosition = (BannerAdapter.FAKE_SIZE / 2) - (BannerAdapter.FAKE_SIZE / 2 % images.size)
        viewPager.setCurrentItem(startPosition, false)

        val transformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(5))
            addTransformer { page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
        }
        viewPager.setPageTransformer(transformer)

        val sizePx = (8 * resources.displayMetrics.density).toInt()
        val dots = arrayOfNulls<ImageView>(images.size)
        dotsLayout.removeAllViews()

        for (i in images.indices) {
            dots[i] = ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive
                    )
                )
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx)
                    .apply { setMargins(8, 0, 8, 0) }
            }
            dotsLayout.addView(dots[i])
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val realIndex = position % images.size
                for (i in dots.indices) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            if (i == realIndex) R.drawable.dot_active else R.drawable.dot_inactive
                        )
                    )
                }
            }
        })

        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_acervo_to_pesquisaFragment)
        }

        carregarLivros()
    }

    class BannerAdapter(private val images: List<Int>,
                        private val onBannerClick: (Int) -> Unit
    ) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

        companion object {
            const val FAKE_SIZE = 10_000
        }

        class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageViewBanner)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
            return BannerViewHolder(view)
        }

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            val realIndex = position % images.size
            holder.imageView.setImageResource(images[realIndex])

            holder.itemView.setOnClickListener {
                onBannerClick(realIndex)
            }
        }

        override fun getItemCount() = FAKE_SIZE
    }

    private fun carregarLivros() {
        db.collection("obras")
            .get()
            .addOnSuccessListener { result ->
                val livros = result.documents.mapNotNull { it.toObject(Livro::class.java) }
                val porGenero = livros.groupBy { it.genero }
                val generos = porGenero.keys.toSortedSet().toList()

                configurarRecycler(binding.recyclerGenero1, binding.txtNomeGenero1, generos.getOrNull(0), porGenero)
                configurarRecycler(binding.recyclerGenero2, binding.txtNomeGenero2, generos.getOrNull(1), porGenero)
                configurarRecycler(binding.recyclerGenero3, binding.txtNomeGenero3, generos.getOrNull(2), porGenero)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarRecycler(
        recycler: RecyclerView,
        titulo: TextView,
        genero: String?,
        porGenero: Map<String, List<Livro>>
    ) {
        val livros = porGenero[genero]
        if(genero == null || livros.isNullOrEmpty()) {
            titulo.visibility = View.GONE
            recycler.visibility = View.GONE
            return
        }

        titulo.text = genero
        titulo.visibility = View.VISIBLE
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recycler.adapter = LivroAdapter(livros, onItemClick = { livroClicado ->
            val bundle = Bundle().apply {
                putSerializable("CHAVE_LIVRO", livroClicado)
            }
            findNavController().navigate(R.id.action_fragment_acervo_to_detalhesLivroFragment, bundle)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class GeneroAdapter2(
    private val listaGeneros: List<Genero>,
    private val onGeneroClick: (Genero) -> Unit
) : RecyclerView.Adapter<GeneroAdapter2.GeneroViewHolder>() {

    inner class GeneroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bg: View = view.findViewById(R.id.bgGenero)
        val tvNome: TextView = view.findViewById(R.id.tvNomeGenero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genero, parent, false)
        return GeneroViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeneroViewHolder, position: Int) {
        val genero = listaGeneros[position]
        holder.tvNome.text = genero.nome

        try {
            val formatoArredondado = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 18 * holder.itemView.resources.displayMetrics.density
                setColor(Color.parseColor(genero.cor))
            }
            holder.bg.background = formatoArredondado
        } catch (e: Exception) {
            Log.e("GeneroAdapter2", "Falha de renderização: ${e.message}")
            holder.bg.setBackgroundColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener { onGeneroClick(genero) }
    }

    override fun getItemCount(): Int = listaGeneros.size
}