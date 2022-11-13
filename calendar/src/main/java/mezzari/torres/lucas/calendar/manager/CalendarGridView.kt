package mezzari.torres.lucas.calendar.manager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 09/11/22
 */
internal class CalendarGridView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStylesAttr: Int = 0
) : LinearLayoutCompat(context, attributeSet, defStylesAttr) {

    private var mAdapter: CalendarAdapterView.Adapter<*>? = null
    var adapter: CalendarAdapterView.Adapter<*>?
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
        orientation = VERTICAL
        if (this.isInEditMode)
            buildView()
    }

    internal fun setupView(adapter: CalendarAdapterView.Adapter<*>, date: DateTime) {
        mAdapter = adapter
        mDate = date
        rebuild()
    }

    private fun changeView() {
        if (childCount > 0) {
            updateView()
            return
        }
        buildView()
    }

    private fun buildView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(date)
        val rows = adapter.getRowsCount(date)

        weightSum = rows.toFloat()
        for (row in 0 until rows) {
            val line = createRow(columns.toFloat())
            for (column in 0 until columns) {
                val view = adapter.getView(row, column, date, null, this)
                placeView(view, line)
            }
            addView(line)
        }
    }

    private fun updateView() {
        val adapter = adapter ?: return
        val columns = adapter.getColumnsCount(date)
        val rows = adapter.getRowsCount(date)
        var childIndex = 0
        val calendarViews = arrayListOf<View>()
        children.forEach {
            if (it !is LinearLayout) return@forEach
            calendarViews.addAll(it.children)
            it.removeAllViews()
        }
        removeAllViews()

        weightSum = rows.toFloat()
        for (row in 0 until rows) {
            val line = createRow(columns.toFloat())
            for (column in 0 until columns) {
                val existingView =
                    calendarViews.takeIf { childIndex < calendarViews.count() }?.get(childIndex)
                val view = adapter.getView(row, column, date, existingView, this)
                childIndex++
                placeView(view, line)
            }
            addView(line)
        }
    }

    private fun rebuild() {
        removeAllViews()
        buildView()
    }

    private fun createRow(weightSum: Float): LinearLayout {
        return LinearLayout(context).also {
            it.orientation = LinearLayout.HORIZONTAL
            it.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.weightSum = weightSum
        }
    }

    private fun placeView(view: View, container: LinearLayout) {
        val wrapper = FrameLayout(context).apply {
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).also {
                it.weight = 1f
            }
        }
        wrapper.addView(view)
        container.addView(wrapper)
    }
}