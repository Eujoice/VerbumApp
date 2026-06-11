package com.example.verbumteste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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

class FragmentAcervo : Fragment(R.layout.fragment_acervo) {

    private var _binding: FragmentAcervoBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private lateinit var livroAdapter: LivroAdapter

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

        viewPager.adapter = BannerAdapter(images)
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

        // Chamando funções para o banco de dados buscar a lista de livros
        initRecyclerViewLivro(emptyList())
        view.postDelayed({
            buscarLivrosFirestore("Tecnologia")
            buscarLivrosFirestore("Fantasia")
        }, 1000)
    }

    class BannerAdapter(private val images: List<Int>) :
        RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

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
            holder.imageView.setImageResource(images[position % images.size])
        }

        override fun getItemCount() = FAKE_SIZE
    }

    private fun initRecyclerViewLivro(livroList: List<Livro>) {
        livroAdapter = LivroAdapter(livroList)
        binding.recyclerGeneros.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerGeneros.setHasFixedSize(true)
        binding.recyclerGeneros.adapter = livroAdapter
    }

    private fun buscarLivrosFirestore(generoDesejado: String) {
        db.collection("obras")
            .whereEqualTo("genero", generoDesejado)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val listaDeLivros = mutableListOf<Livro>()

                for (documento in queryDocumentSnapshots) {
                    // Converte o documento mapeando os campos para a classe Livro
                    val livro = documento.toObject(Livro::class.java)

                    if (livro != null) {
                        listaDeLivros.add(livro)
                    }

                    val secoes = listaDeLivros
                        .groupBy { it.genero }
                        .map { (genero, lista) -> GeneroSecao(genero, lista) }
                        .sortedBy { it.genero } // ordem alfabética

                    binding.recyclerGeneros.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerGeneros.adapter = GeneroAdapter(secoes)
                }

                livroAdapter.atualizarLista(listaDeLivros)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}