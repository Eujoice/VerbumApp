package com.example.verbumteste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

class FragmentAcervo : Fragment(R.layout.fragment_acervo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerBanners)
        val dotsLayout = view.findViewById<LinearLayout>(R.id.dotsIndicator)

        val images = listOf(R.drawable.bannerpercy, R.drawable.tartarugasatelaembaixobanner, R.drawable.bibliotecadameianoitebanner)

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

//arrumar as bolinhas em baixo dos banners
        val dots = arrayOfNulls<ImageView>(images.size)
        for (i in images.indices) {
            dots[i] = ImageView(requireContext()).apply {
                setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_indicator))
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(3, 0, 3, 0) }
                layoutParams = params
            }
            dotsLayout.addView(dots[i])
        }
        dots[0]?.isSelected = true

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val realIndex = position % images.size
                for (i in dots.indices) {
                    dots[i]?.isSelected = (i == realIndex)
                }
            }
        })
    }
}

class BannerAdapter(private val images: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

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