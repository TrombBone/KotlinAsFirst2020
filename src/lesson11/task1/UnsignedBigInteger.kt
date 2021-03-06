package lesson11.task1

import lesson3.task1.digitNumber

/**
 * Класс "беззнаковое большое целое число".
 *
 * Общая сложность задания -- очень сложная, общая ценность в баллах -- 32.
 * Объект класса содержит целое число без знака произвольного размера
 * и поддерживает основные операции над такими числами, а именно:
 * сложение, вычитание (при вычитании большего числа из меньшего бросается исключение),
 * умножение, деление, остаток от деления,
 * преобразование в строку/из строки, преобразование в целое/из целого,
 * сравнение на равенство и неравенство
 */
class UnsignedBigInteger(private val unsignedBigInteger: MutableList<Int>) : Comparable<UnsignedBigInteger> {

    init {
        while (unsignedBigInteger.size > 1 && unsignedBigInteger[0] == 0) unsignedBigInteger.remove(0)
    }

    /**
     * Конструктор из строки
     */
    constructor(s: String) : this(toMListFromStr(s))

    /**
     * Конструктор из целого
     */
    constructor(i: Int) : this(toMListFromInt(i))

    companion object {
        fun toMListFromStr(s: String): MutableList<Int> {
            if (!s.matches(Regex("""\d+"""))) throw NumberFormatException("number contains unknown characters")
            val list = mutableListOf<Int>()
            for (i in s.indices) list.add(s[i] - '0')
            return list
        }

        fun toMListFromInt(number: Int): MutableList<Int> {
            val list = mutableListOf<Int>()
            var n = number
            for (i in 0 until digitNumber(number)) {
                list.add(n % 10)
                n /= 10
            }
            return list.asReversed()
        }
    }

    /**
     * Сложение
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
        val bigger = if (this > other) unsignedBigInteger else other.unsignedBigInteger
        val smaller = if (this < other) unsignedBigInteger else other.unsignedBigInteger
        val res = mutableListOf<Int>()
        var memory = 0
        for (i in 1..bigger.size) {
            val digit: Int = memory + bigger[bigger.size - i] + (smaller.getOrNull(smaller.size - i) ?: 0)
            res.add(digit % 10)
            memory = digit / 10
        }
        if (memory != 0) res.add(1)
        return UnsignedBigInteger(res.asReversed())
    }

    /**
     * Вычитание (бросить ArithmeticException, если this < other)
     */
    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        if (this < other) throw ArithmeticException("negative numbers are not allowed")
        val res = mutableListOf<Int>()
        var negativeMemory = 0
        for (i in 1..unsignedBigInteger.size) {
            var digit: Int = negativeMemory + unsignedBigInteger[unsignedBigInteger.size - i] -
                    (other.unsignedBigInteger.getOrNull(other.unsignedBigInteger.size - i) ?: 0)
            if (digit < 0) {
                digit += 10
                negativeMemory = -1
            } else negativeMemory = 0
            res.add(digit % 10)
        }
        return UnsignedBigInteger(res.asReversed())
    }

    /**
     * Умножение
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger {
        val bigger = if (this > other) unsignedBigInteger else other.unsignedBigInteger
        val smaller = if (this < other) unsignedBigInteger else other.unsignedBigInteger
        val terms = mutableListOf<UnsignedBigInteger>()
        for (j in 1..smaller.size) {
            var term = mutableListOf<Int>()
            var memory = 0
            for (i in 1..bigger.size) {
                val digit: Int = memory + bigger[bigger.size - i] * smaller[smaller.size - j]
                memory = digit / 10
                term.add(digit % 10)
            }
            if (memory != 0) term.add(memory)
            term = term.asReversed()
            for (k in 1 until j) term.add(0)
            terms.add(UnsignedBigInteger(term))
        }
        var res = UnsignedBigInteger(0)
        for (t in terms) res += t
        return res
    }

    /**
     * Деление
     */
    operator fun div(other: UnsignedBigInteger): UnsignedBigInteger {
        if (other == UnsignedBigInteger(0)) throw java.lang.ArithmeticException("cannot be divided by zero")
        if (other > this) return UnsignedBigInteger(0)
        val biggerSize = maxOf(unsignedBigInteger.size, other.unsignedBigInteger.size)
        val smallerSize = minOf(unsignedBigInteger.size, other.unsignedBigInteger.size)
        val res = mutableListOf<Int>()
        var number = mutableListOf<Int>()
        for (i in 0 until smallerSize) number.add(unsignedBigInteger[i])
        for (i in smallerSize..biggerSize) {
            var r = 0
            if (UnsignedBigInteger(number) < other) {
                number.add(if (i != biggerSize) unsignedBigInteger[i] else 0)
                res.add(0)
                continue
            }
            while (UnsignedBigInteger(number) >= other) {
                number = (UnsignedBigInteger(number) - other).unsignedBigInteger
                r++
            }
            res.add(r)
            number.add(if (i != biggerSize) unsignedBigInteger[i] else 0)
        }
        return UnsignedBigInteger(res)
    }

    /**
     * Взятие остатка
     */
    operator fun rem(other: UnsignedBigInteger): UnsignedBigInteger = this - this / other * other

    /**
     * Сравнение на равенство (по контракту Any.equals)
     */
    override fun equals(other: Any?): Boolean =
        other is UnsignedBigInteger && unsignedBigInteger == other.unsignedBigInteger

    /**
     * Сравнение на больше/меньше (по контракту Comparable.compareTo)
     */
    override fun compareTo(other: UnsignedBigInteger): Int {
        if (unsignedBigInteger.size > other.unsignedBigInteger.size) return 1
        if (unsignedBigInteger.size < other.unsignedBigInteger.size) return -1
        for (i in 0 until unsignedBigInteger.size) {
            if (unsignedBigInteger[i] > other.unsignedBigInteger[i]) return 1
            if (unsignedBigInteger[i] < other.unsignedBigInteger[i]) return -1
        }
        return 0
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String = buildString { for (i in unsignedBigInteger) append(i.toString()) }

    /**
     * Преобразование в целое
     * Если число не влезает в диапазон Int, бросить ArithmeticException
     */
    fun toInt(): Int = toString().toIntOrNull() ?: throw ArithmeticException("too big number")

    override fun hashCode(): Int = unsignedBigInteger.hashCode()
}