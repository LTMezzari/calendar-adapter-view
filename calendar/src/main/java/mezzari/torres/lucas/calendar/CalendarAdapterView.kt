package mezzari.torres.lucas.calendar

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.adapter.month.DefaultMonthAdapter
import mezzari.torres.lucas.calendar.adapter.week.DefaultWeekAdapter
import mezzari.torres.lucas.calendar.adapter.year.DefaultYearAdapter
import org.joda.time.DateTime
import androidx.core.content.res.use

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
class CalendarAdapterView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attributeSet, defStyleAttr) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val observer: DataSetObserver by lazy {
        object : DataSetObserver() {
            override fun onChanged() {
                pager.update()
            }

            override fun onInvalidated() {
                pager.rebuild()
            }
        }
    }

    var adapter: CalendarAdapter<*>?
        get() = pager.adapter
        set(value) {
            pager.adapter?.unregisterDataSetObserver(observer)
            pager.adapter = value
            pager.adapter?.registerDataSetObserver(observer)
        }

    var currentPageDate: DateTime
        get() = pager.currentPageDate
        set(value) {
            pager.currentPageDate = value
            adapter?.notifyDataSetChanged()
        }

    private val pager: CalendarPagerView by lazy {
        CalendarPagerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            gravity = Gravity.CENTER_HORIZONTAL
            onPageChanged = { _, new, _ ->
                onCalendarPageChanged?.invoke(this@CalendarAdapterView, headerView, footerView, new)
            }
        }
    }

    @LayoutRes
    private var headerLayout: Int = -1
    private var mHeaderView: View? = null
    var headerView: View?
        get() = mHeaderView
        set(value) {
            mHeaderView?.run {
                removeViewAt(0)
            }
            mHeaderView = value
            mHeaderView?.run {
                addView(this, 0)
            }
        }

    @LayoutRes
    private var footerLayout: Int = -1
    private var mFooterView: View? = null
    var footerView: View?
        get() = mFooterView
        set(value) {
            mFooterView?.takeIf { childCount >= 3 }?.run {
                removeViewAt(2)
            }
            mFooterView = value
            mFooterView?.takeIf { childCount >= 3 }?.run {
                addView(this, 2)
            }
        }

    var onCalendarPageChanged: ((CalendarAdapterView, View?, View?, DateTime) -> Unit)? = null
        set(value) {
            field = value
            onCalendarPageChanged?.invoke(this, headerView, footerView, currentPageDate)
        }

    init {
        loadAttributes(context, attributeSet)
        setupView()
    }

    private fun loadAttributes(context: Context, attributeSet: AttributeSet?) {
        try {
            context.obtainStyledAttributes(attributeSet, R.styleable.CalendarAdapterView, 0, 0)
                .use { typedArray ->
                    val adapterEnum =
                        typedArray.getInt(R.styleable.CalendarAdapterView_calendar_type, 0)
                    adapter = when (adapterEnum) {
                        1 -> DefaultWeekAdapter(context)
                        2 -> DefaultYearAdapter(context)
                        else -> DefaultMonthAdapter(context)
                    }

                    headerLayout =
                        typedArray.getResourceId(R.styleable.CalendarAdapterView_header_layout, -1)
                    footerLayout =
                        typedArray.getResourceId(R.styleable.CalendarAdapterView_footer_layout, -1)
                }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }
    }

    private fun setupView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(32)

        addHeader()
        addBody()
        addFooter()
    }

    private fun addHeader() {
        inflateAndPlaceView(mHeaderView, headerLayout) {
            mHeaderView = it
        }
    }

    private fun addBody() {
        if (this.isInEditMode) {
            addView(CalendarGridView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 16)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                useDefaultMargins = true
                gravity = Gravity.CENTER_HORIZONTAL
                adapter = this@CalendarAdapterView.adapter
                date = this@CalendarAdapterView.currentPageDate
            })
            return
        }
        addView(pager)
    }

    private fun addFooter() {
        inflateAndPlaceView(mFooterView, footerLayout) {
            mFooterView = it
        }
    }

    private fun inflateAndPlaceView(view: View?, layout: Int, save: (view: View?) -> Unit) {
        var newView = view
        if (view == null && layout != -1) {
            newView = inflater.inflate(layout, this, false)
            save(newView)
        }

        newView?.run {
            addView(newView)
            return
        }
    }

    fun nextPage() {
        pager.nextPage()
    }

    fun previousPage() {
        pager.previousPage()
    }

    abstract class CalendarAdapter<T : ViewHolder> : BaseAdapter() {
        abstract fun getColumnsCount(date: DateTime): Int

        abstract fun getRowsCount(date: DateTime): Int

        abstract fun onCreateViewHolder(viewType: Int, container: ViewGroup?): T

        abstract fun onBindViewHolder(row: Int, column: Int, date: DateTime, holder: T)

        abstract fun getNextPage(current: DateTime): DateTime?

        abstract fun getPreviousPage(current: DateTime): DateTime?

        open fun getItemViewType(row: Int, column: Int): Int {
            return 0
        }

        open fun getView(
            row: Int,
            column: Int,
            date: DateTime,
            view: View?,
            container: ViewGroup?
        ): View {
            val viewType = getItemViewType(row, column)
            val holder: T = (view?.tag as? T).takeIf { viewType == it?.viewType }
                ?: onCreateViewHolder(viewType, container).also {
                    it.view.tag = it
                    it.viewType = viewType
                }

            onBindViewHolder(row, column, date, holder)
            holder.view.layoutParams =
                GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column)).apply {
                    setGravity(Gravity.CENTER_HORIZONTAL)
                }

            return holder.view
        }

        override fun getView(position: Int, view: View?, container: ViewGroup?): View? {
            return null
        }

        override fun getCount(): Int {
            return 0
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getItemViewType(position: Int): Int {
            return -1
        }
    }

    abstract class ViewHolder(val view: View) {
        var viewType: Int = 0
    }
}