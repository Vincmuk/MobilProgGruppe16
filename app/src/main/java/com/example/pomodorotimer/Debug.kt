import timerx.Timer

class Debug {
    companion object {
        fun printTimerList(timerList: List<Timer>) {
            println("Debugging Timer List:")
            for ((index, timer) in timerList.withIndex()) {
                println("Timer $index - Start Time: ${timer.formattedStartTime}")
            }
        }
    }
}
