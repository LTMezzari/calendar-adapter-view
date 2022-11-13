package mezzari.torres.lucas.calendar.manager

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 10/11/22
 */
internal class PagedLayoutManager(context: Context) : CalendarAdapterView.LayoutManager {
    override var adapter: CalendarAdapterView.Adapter<*>?
        get() = pagerView.adapter
        set(value) {
            pagerView.adapter = value
        }
    override var currentPageDate: DateTime
        get() = pagerView.currentPageDate
        set(value) {
            pagerView.currentPageDate = value
        }

    override var onPageChanged: ((DateTime, Int) -> Unit)?
        get() = pagerView.onPageChanged
        set(value) {
            pagerView.onPageChanged = value
        }

    private val pagerView: CalendarPagerView by lazy {
        CalendarPagerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun nextPage() {
        pagerView.nextPage()
    }

    override fun previousPage() {
        pagerView.previousPage()
    }

    override fun update() {
        pagerView.update()
    }

    override fun rebuild() {
        pagerView.rebuild()
    }

    override fun getView(): View {
        return pagerView
    }
}