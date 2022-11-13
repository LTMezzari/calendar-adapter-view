package mezzari.torres.lucas.calendar

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.adapter.month.DefaultMonthAdapter
import mezzari.torres.lucas.calendar.adapter.week.DefaultWeekAdapter
import mezzari.torres.lucas.calendar.adapter.year.DefaultYearAdapter
import org.joda.time.DateTime
import androidx.core.content.res.use
import mezzari.torres.lucas.calendar.manager.CalendarGridView
import mezzari.torres.lucas.calendar.manager.PagedLayoutManager

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
                mLayoutManager.update()
            }

            override fun onInvalidated() {
                mLayoutManager.rebuild()
            }
        }
    }

    private val listener: (DateTime, Int) -> Unit = { date, _ ->
        onCalendarPageChanged?.invoke(this, headerView, footerView, date)
    }

    var adapter: Adapter<*>?
        get() = mLayoutManager.adapter
        set(value) {
            mLayoutManager.adapter?.unregisterDataSetObserver(observer)
            mLayoutManager.adapter = value
            mLayoutManager.adapter?.registerDataSetObserver(observer)
        }

    var currentPageDate: DateTime
        get() = mLayoutManager.currentPageDate
        set(value) {
            mLayoutManager.currentPageDate = value
            adapter?.notifyDataSetChanged()
        }

    private var mLayoutManager: LayoutManager = PagedLayoutManager(context)
    var layoutManager: LayoutManager
        get() = mLayoutManager
        set(value) {
            mLayoutManager.also {
                removeView(it.getView())
            }
            mLayoutManager = value
            mLayoutManager.onPageChanged = listener
            if (childCount == 0) {
                addView(mLayoutManager.getView())
                return
            }
            addView(mLayoutManager.getView(), 1)
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
            mFooterView?.run {
                addView(this, childCount)
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
                        -1 -> null
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

        mLayoutManager.onPageChanged = listener

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
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                adapter = this@CalendarAdapterView.adapter
                date = this@CalendarAdapterView.currentPageDate
            })
            return
        }
        addView(mLayoutManager.getView())
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
        mLayoutManager.nextPage()
    }

    fun previousPage() {
        mLayoutManager.previousPage()
    }

    interface LayoutManager {
        var adapter: Adapter<*>?

        var currentPageDate: DateTime

        var onPageChanged: ((DateTime, Int) -> Unit)?

        fun nextPage()

        fun previousPage()

        fun update()

        fun rebuild()

        fun getView(): View
    }

    interface Adapter<T : ViewHolder> {
        fun getColumnsCount(date: DateTime): Int

        fun getRowsCount(date: DateTime): Int

        fun getView(
            row: Int,
            column: Int,
            date: DateTime,
            view: View?,
            container: ViewGroup?
        ): View

        fun onCreateViewHolder(viewType: Int, container: ViewGroup?): T

        fun onBindViewHolder(row: Int, column: Int, date: DateTime, holder: T)

        fun getNextPage(current: DateTime): DateTime?

        fun getPreviousPage(current: DateTime): DateTime?

        fun getItemViewType(row: Int, column: Int): Int

        fun notifyDataSetChanged()

        fun notifyDataSetInvalidated()

        fun registerDataSetObserver(observer: DataSetObserver?)

        fun unregisterDataSetObserver(observer: DataSetObserver?)
    }

    abstract class CalendarAdapter<T : ViewHolder> : BaseAdapter(), Adapter<T> {
        override fun getItemViewType(row: Int, column: Int): Int {
            return 0
        }

        override fun getView(
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