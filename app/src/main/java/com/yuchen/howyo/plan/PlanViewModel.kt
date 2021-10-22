package com.yuchen.howyo.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.Logger

class PlanViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    //Plan data
    private val _plan = MutableLiveData<Plan>()

    val plan: LiveData<Plan>
        get() = _plan

    //Days list of plans
    private val _days = MutableLiveData<List<Day>>()

    val days: LiveData<List<Day>>
        get() = _days

    //Schedule list of days
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    var selectedDayPosition = MutableLiveData<Int>()

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Schedule>()

    val navigateToDetail: LiveData<Schedule>
        get() = _navigateToDetail

    // Handle navigation to map mode
    private val _navigateToMapMode = MutableLiveData<List<Day>>()

    val navigateToMapMode: LiveData<List<Day>>
        get() = _navigateToMapMode

    init {
        _plan.value = Plan(
            "aabb",
            "ione0213",
            listOf("Alex111", "Joe987"),
            "let's go Japan",
            "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81",
            1634601600000,
            1634688000000
        )

        _days.value = listOf(
            Day("001", "987", 0),
            Day("002", "987", 1),
            Day("003", "987", 2),
            Day("004", "987", 3),
            Day("005", "987", 4),
            Day("006", "987", 5),
            Day("007", "987", 6),
            Day("008", "987", 7),
        )

        _schedules.value = listOf(
            Schedule(
                dayId = "001",
                scheduleType = "traffic",
                title = "train to kyoto",
                listOf(
                    "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81",
                    "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81",
                    "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/Tokyo-Subway-Ticket.jpeg?alt=media&token=a2ea35b5-0e24-4f12-a35c-fd8f740b35ac"
                ),
//                latitude = 135.75791610883104,
//                longitude = 34.98586570883951,
                startTime = 1634599800,
                endTime = 1634605200,
                from = "Osaka",
                to = "Kyoto",
                position = 0
            ),
            Schedule(
                dayId = "001",
                scheduleType = "hotel",
                title = "Check in",
                listOf(
                    "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81",
                    "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/Tokyo-Subway-Ticket.jpeg?alt=media&token=a2ea35b5-0e24-4f12-a35c-fd8f740b35ac"
                ),
                latitude = 135.77027806381324,
                longitude = 35.009169898670926,
                startTime = 1634616900,
                endTime = 1634625000,
                from = "Kyoto Station",
                to = "THE PRIME POD KYOTO",
                position = 1
            ),
            Schedule(
                dayId = "001",
                scheduleType = "hotel",
                title = "Check in",
                latitude = 135.77027806381324,
                longitude = 35.009169898670926,
                startTime = 1634616900,
                endTime = 1634625000,
                from = "Kyoto Station",
                to = "THE PRIME POD KYOTO",
                position = 2
            ),
            Schedule(
                dayId = "001",
                scheduleType = "hotel",
                title = "Check in",
                latitude = 135.77027806381324,
                longitude = 35.009169898670926,
                startTime = 1634616900,
                endTime = 1634625000,
                from = "Kyoto Station",
                to = "THE PRIME POD KYOTO",
                position = 3
            ),
            Schedule(
                dayId = "001",
                scheduleType = "hotel",
                title = "Check in",
                latitude = 135.77027806381324,
                longitude = 35.009169898670926,
                startTime = 1634616900,
                endTime = 1634625000,
                from = "Kyoto Station",
                to = "THE PRIME POD KYOTO",
                position = 4
            ),
            Schedule(
                dayId = "001",
                scheduleType = "hotel",
                title = "Check in",
                latitude = 135.77027806381324,
                longitude = 35.009169898670926,
                startTime = 1634616900,
                endTime = 1634625000,
                from = "Kyoto Station",
                to = "THE PRIME POD KYOTO",
                position = 5
            )
        )
    }

    fun selectDay(position: Int) {
        Logger.i("selectDay@@@@@@@@@@")
        selectedDayPosition.value = position
    }

    fun navigateToDetail(schedule: Schedule) {
        Logger.i("navigateToDetail@@@@@@@@@@")
        _navigateToDetail.value = schedule
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun navigateToMapMode() {
        _navigateToMapMode.value = days.value
    }

    fun onMapModeNavigated() {
        _navigateToMapMode.value = null
    }
}