package mezzari.torres.lucas.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
open class YearAdapter(private val context: Context) :
    CalendarAdapterView.CalendarAdapter<YearAdapter.YearViewHolder>() {
    override fun getColumnsCount(date: DateTime): Int {
        /*
         * Jan, Feb, Mar
         * Apr, May, Jun
         * Jul, Aug, Sep
         * Oct, Nov, Dec
         */
        return 3
    }

    override fun getRowsCount(date: DateTime): Int {
        /*
         * Jan, Feb, Mar
         * Apr, May, Jun
         * Jul, Aug, Sep
         * Oct, Nov, Dec
         */
        return 4
    }

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): YearViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return YearViewHolder(view)
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: YearViewHolder
    ) {
        var startDate = DateTime(date.year, 1, 1, 0, 0)
        startDate = startDate.plusMonths(row * 3 + column)
        onBindMonthViewHolder(startDate, date, holder)
    }

    open fun onBindMonthViewHolder(
        month: DateTime, date: DateTime, holder: YearViewHolder
    ) {
        (holder.view as? TextView)?.apply {
            text = month.toString("MMM")
        }
    }

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).withMonthOfYear(1).plusYears(1)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).withMonthOfYear(1).minusYears(1)
    }

    class YearViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}