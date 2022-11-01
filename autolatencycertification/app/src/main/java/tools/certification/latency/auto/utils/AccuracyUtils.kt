package tools.certification.latency.auto.utils

object AccuracyUtils {
    private const val WEATHER_KEY = "What is weather now?"
    private const val WEATHER_VALUES =
        "weather degree degrees rain sunny cloud rainbow temperature pressure overcast shower fair " +
                "sunrise dry tornado sunset humidity cold heat wind cloudy heat wave fog breeze humid " +
                "lightning blustery humidity thunder snow heat index thunderstorm downpour drought tropical " +
                "water cycle temperate moisture drizzle warm hail icicle climate storm flood muggy gale flash flood " +
                "atmosphere cold front mist isobar cold snap condensation forecast ice storm freeze barometric gust snowfall " +
                "whirlwind hurricane cyclone air balmy avalanche air pressure frost blizzard spring smog ozone fall winter " +
                "outlook summer storm surge rain gage low pressure wind chill sleet sky dew surge monsoon permafrost " +
                "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45"

    private const val TIME_KEY = "What time is it?"
    private const val TIME_VALUES =
        "PM AM o'clock one two three four five six seven eight night ten eleven twelve thirteen fourteen " +
                "fifteen sixteen seventeen eighteen nineteen twenty thirty forty fifty half quarter hour past "

    private const val DAY_KEY = "What day is today?"
    private const val DAY_VALUES =
        "Monday Tuesday Wednesday Friday Saturday Sunday January February March April May June July August " +
                "September October November December first second third four five six seven eight night ten eleven twelve " +
                "thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty thirty"

    private const val KEY_ERROR = "sorry"

    val map: Map<String, Set<String>> = mutableMapOf<String, Set<String>>().also {
        it[WEATHER_KEY] = WEATHER_VALUES.split(" ").toSet()
        it[TIME_KEY] = TIME_VALUES.split(" ").toSet()
        it[DAY_KEY] = DAY_VALUES.split(" ").toSet()
    }
}
