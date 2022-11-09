package mezzari.torres.lucas.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @author lucas.torres@dietbox.me
 * @author lucas.mezzari1@gmail.com
 *
 * @location Rio Grande do Sul, Brazil
 * @since 09/11/22
 */
internal class CalendarPagerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyle, defStyleAttr) {

    private val onPageChangedCallback: ViewPager2.OnPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val date = (when (position) {
                    0 -> adapter?.getPreviousPage(currentPageDate)
                    2 -> adapter?.getNextPage(currentPageDate)
                    else -> currentPageDate
                }) ?: return

                onPageChanged?.invoke(currentPageDate, date, position)
            }
        }
    }

    private val viewPager: ViewPager2 by lazy {
        ViewPager2(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            registerOnPageChangeCallback(onPageChangedCallback)
        }
    }

    private val recyclerView: RecyclerView by lazy {
        viewPager.getChildAt(0) as RecyclerView
    }

    private val pagerAdapter: CalendarPagerAdapter by lazy {
        CalendarPagerAdapter(context).apply {
            adapter = this@CalendarPagerView.adapter
            currentPageDate = this@CalendarPagerView.currentPageDate
        }
    }

    private val scrollBehaviour: CalendarScrollBehaviour by lazy {
        CalendarScrollBehaviour(
            pagerAdapter,
            recyclerView.layoutManager as LinearLayoutManager,
            onMoveForward = move@{
                val next = adapter?.getNextPage(currentPageDate) ?: return@move
                currentPageDate = next
            },
            onMoveBackwards = move@{
                val next = adapter?.getPreviousPage(currentPageDate) ?: return@move
                currentPageDate = next
            },
        )
    }

    internal var adapter: CalendarAdapterView.CalendarAdapter<*>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            pagerAdapter.adapter = value
            pagerAdapter.notifyDataSetChanged()
        }

    internal var currentPageDate: DateTime = DateTime.now()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            pagerAdapter.currentPageDate = value
            pagerAdapter.notifyDataSetChanged()
        }

    internal var onPageChanged: ((DateTime, DateTime, Int) -> Unit)? = null

    init {
        setupView()
    }

    private fun setupView() {
        addView(viewPager)
        viewPager.adapter = pagerAdapter
        recyclerView.addOnScrollListener(scrollBehaviour)

        viewPager.post {
            if (adapter?.getPreviousPage(currentPageDate) != null)
                viewPager.setCurrentItem(1, false)
        }
    }

    internal fun nextPage() {
//        val next = adapter?.getNextPage(currentPageDate) ?: return
        viewPager.setCurrentItem(2, true)
    }

    internal fun previousPage() {
//        val previous = adapter?.getPreviousPage(currentPageDate) ?: return
        viewPager.setCurrentItem(0, true)
    }

    internal fun update() {
        pagerAdapter.notifyItemChanged(1)
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun rebuild() {
        pagerAdapter.notifyDataSetChanged()
    }

    internal class CalendarPagerAdapter(private val context: Context) :
        RecyclerView.Adapter<CalendarPagerAdapter.CalendarViewHolder>() {

        var adapter: CalendarAdapterView.CalendarAdapter<*>? = null
        var currentPageDate: DateTime = DateTime.now()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
            return CalendarViewHolder(
                CalendarGridView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(0, 16, 0, 16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    useDefaultMargins = true
                }
            )
        }

        override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
            val adapter = adapter ?: return
            val grid = holder.calendarGrid
            val date = when (position) {
                0 -> {
                    adapter.getPreviousPage(currentPageDate) ?: currentPageDate
                }
                1 -> {
                    if (adapter.getPreviousPage(currentPageDate) != null) currentPageDate else adapter.getNextPage(
                        currentPageDate
                    ) ?: currentPageDate
                }
                2 -> {
                    adapter.getNextPage(currentPageDate) ?: return
                }
                else -> currentPageDate
            }

            if (date == currentPageDate) {
                grid.setupView(adapter, date)
                return
            }

            grid.post {
                grid.setupView(adapter, date)
            }
        }

        override fun getItemCount(): Int {
            var size = 1
            val previous = adapter?.getPreviousPage(currentPageDate)
            val next = adapter?.getNextPage(currentPageDate)
            if (previous != null)
                size++

            if (next != null)
                size++
            return size
        }

        internal class CalendarViewHolder(val calendarGrid: CalendarGridView) :
            RecyclerView.ViewHolder(calendarGrid)
    }

    internal class CalendarScrollBehaviour(
        private val adapter: CalendarPagerAdapter,
        private val layoutManager: LinearLayoutManager,
        private val onMoveForward: () -> Unit,
        private val onMoveBackwards: () -> Unit,
    ) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val itemCount = adapter.itemCount
            val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
            val lastItemVisible = layoutManager.findLastVisibleItemPosition()
            if (firstItemVisible == (itemCount - 1) && dx > 0) {
                onMoveForward()
                adapter.notifyItemChanged(1)
                recyclerView.scrollToPosition(1)
                adapter.notifyItemChanged(itemCount - 1)
            } else if (lastItemVisible == 0 && dx < 0) {
                onMoveBackwards()
                adapter.notifyItemChanged(1)
                recyclerView.scrollToPosition(1)
                adapter.notifyItemChanged(0)
            }
        }
    }
}