package com.example.verbumteste

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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


    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerBanners)
        val dotsLayout = view.findViewById<LinearLayout>(R.id.dotsIndicator)

        val images = listOf(R.drawable.bannerpercy, R.drawable.tartarugas, R.drawable.biblioteca2)
        viewPager.adapter = BannerAdapter(images)

        viewPager.offscreenPageLimit = 3

        val nextItemVisiblePx = 30
        val currentItemHorizontalMarginPx = 40

        val pageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(nextItemVisiblePx))
            addTransformer { page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f // Banner central maior
            }
        }
        viewPager.setPageTransformer(pageTransformer)

        val dots = arrayOfNulls<ImageView>(images.size)
        for (i in images.indices) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_indicator))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dotsLayout.addView(dots[i], params)
        }

        dots[0]?.isSelected = true

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in dots.indices) {
                    dots[i]?.isSelected = (i == position)
                }
            }
        })

        runnable = object : Runnable {
            override fun run() {
                val current = viewPager.currentItem
                val next = if (current == images.size - 1) 0 else current + 1
                viewPager.setCurrentItem(next, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }
}

class BannerAdapter(private val images: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewBanner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount() = images.size
}