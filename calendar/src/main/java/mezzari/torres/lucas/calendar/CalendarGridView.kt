package mezzari.torres.lucas.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.core.view.children
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 09/11/22
 */
internal class CalendarGridView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStylesAttr: Int = 0
) : GridLayout(context, attributeSet, defStylesAttr) {

    private var mAdapter: CalendarAdapterView.CalendarAdapter<*>? = null
    var adapter: CalendarAdapterView.CalendarAdapter<*>?
        get() = mAdapter
        set(value) {
            mAdapter = value
            rebuild()
        }

    private var mDate: DateTime = DateTime.now()
    var date: DateTime
        get() = mDate
        set(value) {
            mDate = value
            changeView()
        }

    init {
        if (this.isInEditMode)
            buildView()
    }

    internal fun setupView(adapter: CalendarAdapterView.CalendarAdapter<*>, date: DateTime) {
        mAdapter = adapter
        mDate = date
        changeView()
    }

    internal fun changeView() {
        if (childCount > 0) {
            updateView()
            return
        }
        buildView()
    }

    internal fun buildView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(date)
        val rows = adapter.getRowsCount(date)

        columnCount = columns
        rowCount = rows

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val view = adapter.getView(row, column, date, null, this)
                addView(view)
            }
        }
    }

    internal fun updateView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(date)
        val rows = adapter.getRowsCount(date)
        var childIndex = 0
        val gridChildren = children.toList()
        removeAllViews()

        columnCount = columns
        rowCount = rows

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val existingView =
                    gridChildren.takeIf { childIndex < gridChildren.count() }?.get(childIndex)
                val view = adapter.getView(row, column, date, existingView, this)
                childIndex++
                addView(view)
            }
        }

        for (i in childIndex until childCount) {
            removeViewAt(i)
        }
    }

    internal fun rebuild() {
        removeAllViews()
        buildView()
    }
}