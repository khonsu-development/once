package eu.khonsu.once

class Amount {
    companion object {
        @JvmStatic
        fun exactly(numberOfTimes: Int): CountChecker = CountChecker { count: Int -> numberOfTimes == count }

        @JvmStatic
        fun moreThan(numberOfTimes: Int): CountChecker = CountChecker { count: Int -> count > numberOfTimes }

        @JvmStatic
        fun lessThan(numberOfTimes: Int): CountChecker = CountChecker { count: Int -> count < numberOfTimes }
    }
}
