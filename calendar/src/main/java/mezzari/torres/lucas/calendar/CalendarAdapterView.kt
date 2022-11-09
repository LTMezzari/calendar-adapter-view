package mezzari.torres.lucas.calendar

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.adapter.MonthAdapter
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
class CalendarAdapterView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attributeSet, defStyleAttr) {

    private val observer: DataSetObserver by lazy {
        object : DataSetObserver() {
            override fun onChanged() {
                updateView()
            }

            override fun onInvalidated() {
                removeAllViews()
                buildView()
            }
        }
    }

    var adapter: CalendarAdapter<*>? = null
        set(value) {
            field?.unregisterDataSetObserver(observer)
            field = value
            field?.registerDataSetObserver(observer)
        }

    var currentPageDate: DateTime = DateTime.now()
        set(value) {
            field = value
            adapter?.notifyDataSetChanged()
        }

    private val grid: GridLayout by lazy {
        GridLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            useDefaultMargins = true
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    var headerView: View? = null
    var footerView: View? = null

    init {
        loadAttributes(context, attributeSet)
        setupView()
        buildView()
    }

    private fun setupView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        adapter = MonthAdapter(context)
        setPadding(32)

        headerView = TextView(context).apply {
            text = currentPageDate.toString("MMMM yyyy")
            textAlignment = TEXT_ALIGNMENT_CENTER
        }

        //Header
        headerView?.run { addView(headerView) }
        //Calendar
        addView(grid)
        //Footer
        footerView?.run { addView(footerView) }
    }

    private fun loadAttributes(context: Context, attributeSet: AttributeSet?) {

    }

    private fun buildView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(currentPageDate)
        val rows = adapter.getRowsCount(currentPageDate)
        grid.apply {
            columnCount = columns
            rowCount = rows
        }

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val view = adapter.getView(row, column, currentPageDate, null, grid)
                grid.addView(view)
            }
        }
    }

    private fun updateView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(currentPageDate)
        val rows = adapter.getRowsCount(currentPageDate)
        var childIndex = 0
        val gridChildren = grid.children.toList()
        grid.removeAllViews()

        grid.apply {
            columnCount = columns
            rowCount = rows
        }

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val existingView =
                    gridChildren.takeIf { childIndex < gridChildren.count() }?.get(childIndex)
                val view = adapter.getView(row, column, currentPageDate, existingView, grid)
                childIndex++
                grid.addView(view)
            }
        }

        for (i in childIndex until grid.childCount) {
            grid.removeViewAt(i)
        }

        (headerView as? TextView)?.text = currentPageDate.toString("MMMM yyyy")
    }

    fun nextPage() {
        val next = adapter?.getNextPage(currentPageDate) ?: return
        currentPageDate = next
    }

    fun previousPage() {
        val previous = adapter?.getPreviousPage(currentPageDate) ?: return
        currentPageDate = previous
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