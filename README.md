# calendar-adapter-view
A Calendar View made to allow the user full control over it's views, dates, columns and controls.

[![](https://jitpack.io/v/LTMezzari/calendar-adapter-view.svg)](https://jitpack.io/#LTMezzari/calendar-adapter-view)

Get the library [here](https://jitpack.io/#LTMezzari/calendar-adapter-view)

The XML Declaration of the calendar looks like this:

```xml
<mezzari.torres.lucas.calendar.CalendarAdapterView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:calendar_type="month|week|year|custom"
                    app:header_layout="@layout/layout_calendar_header"
                    app:footer_layout="@layout_calendar_footer" />
```

You can add a header and a footer layout to the calendar. And the calendar_type will use a base adapter.

To create a new adapter you just need to create a class and extend ``CalendarAdapterView.Adapter`` passing a ``CalendarAdapterView.ViewHolder`` as type. There are base adapters with prepared functionallity for the **week**, **month** and **year** types of calendar.

By default the calendar will utilize a ``PagedLayoutManager`` to place each calendar in it's on page but you can create your own ``CalendarAdapterView.LayoutManager`` and override the functionallity.
