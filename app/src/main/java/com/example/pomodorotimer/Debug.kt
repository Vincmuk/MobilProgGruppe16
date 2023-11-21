import timerx.Timer

class Debug {
    companion object {
        fun printTimerList(timerList: List<Timer>): String {
            val stringBuilder = StringBuilder("Debugging Timer List:\n")
            for ((index, timer) in timerList.withIndex()) {
                stringBuilder.append("Timer $index - Start Time: ${timer.formattedStartTime} - Remaining Time: ${timer.remainingFormattedTime}\n")
            }
            return stringBuilder.toString()
        }
    }
}

