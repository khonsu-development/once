package eu.khonsu.once

class Amount {
    companion object {
        @JvmStatic
        fun exactly(numberOfTimes: Int): CountChecker {
            return CountChecker { count: Int -> numberOfTimes == count }
        }

        @JvmStatic
        fun moreThan(numberOfTimes: Int): CountChecker {
            return CountChecker { count: Int -> count > numberOfTimes }
        }

        @JvmStatic
        fun lessThan(numberOfTimes: Int): CountChecker {
            return CountChecker { count: Int -> count < numberOfTimes }
        }
    }
}
