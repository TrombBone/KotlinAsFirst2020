package lesson11.task1

import lesson3.task1.digitNumber
import kotlin.math.pow

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
    private var fromConstructor = false

//    init {
//        if (!fromConstructor) {
//            for (i in 0 until unsignedBigInteger.size - 1) {
//                while (unsignedBigInteger[i] * 10 + unsignedBigInteger[i + 1] > 0) {
//                    unsignedBigInteger[i] = unsignedBigInteger[i] * 10 + unsignedBigInteger[i + 1]
//                    unsignedBigInteger.removeAt(i + 1)
//                }
//                //break
//            }
//        }
//        val str = "12345"
//        str.reversed()
//    }

    /**
     * Конструктор из строки
     */
    constructor(s: String) : this(toMList(s)) {
        fromConstructor = true
        if (!s.matches(Regex("""\d+"""))) throw NumberFormatException("number contains unknown characters")
    }

    companion object {
        fun toMList(s: String): MutableList<Int> {
            val list = mutableListOf<Int>()
            s.toIntOrNull()?.let { list.add(it) } ?: run {
                var postStr = Regex("""^(0+)""").replace(s, "")
                while (postStr.isNotEmpty()) {
                    val startIntStr =
                        postStr.substring(0, if (postStr.length > 10) 10 else postStr.length).toIntOrNull()
                            ?: postStr.substring(0, 9).toInt()
                    list.add(startIntStr)
                    if (digitNumber(startIntStr) - 1 == postStr.lastIndex) break
                    else postStr = postStr.substring(digitNumber(startIntStr), postStr.length)
                    while (postStr.matches(Regex("""0\d*"""))) {
                        list.add(0)
                        postStr = postStr.replaceFirst("0", "")
                    }
                }
            }
            return list
        }
    }

    /**
     * Конструктор из целого
     */
    constructor(i: Int) : this(mutableListOf(i)) {
        fromConstructor = true
    }

    /**
     * Сложение
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
//        println("this unsignedBigInteger: $this")
//        println("other unsignedBigInteger: $other")
        val biggerSize = maxOf(toString().length, other.toString().length)
        val smallerSize = minOf(toString().length, other.toString().length)
        var res = "" //mutableListOf<Int>()
        var memory = 0
        for (i in 1..biggerSize) {
            val digit: Int = memory + if (toString().length == biggerSize)
                getValue(biggerSize - i) + if (i >= smallerSize + 1) 0 else other.getValue(smallerSize - i)
            else other.getValue(biggerSize - i) + if (i >= smallerSize + 1) 0 else getValue(smallerSize - i)
            memory = 0
            if (digitNumber(digit) > 1) memory++
            res = "${digit % 10}" + res //.add(0, digit % 10)
        }
        if (memory != 0) res = "1$res" //.add(0, 1)
        return UnsignedBigInteger(res)
    }

    /**
     * Вычитание (бросить ArithmeticException, если this < other)
     */
    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        if (this < other) throw ArithmeticException("negative numbers are not allowed")
        val biggerSize = maxOf(toString().length, other.toString().length)
        val smallerSize = minOf(toString().length, other.toString().length)
        var res = ""
        var negativeMemory = 0
        for (i in 1..biggerSize) {
            var digit: Int = negativeMemory + if (toString().length == biggerSize)
                getValue(biggerSize - i) - if (i >= smallerSize + 1) 0 else other.getValue(smallerSize - i)
            else other.getValue(biggerSize - i) - if (i >= smallerSize + 1) 0 else getValue(smallerSize - i)
            negativeMemory = 0
            if (digit < 0) {
                digit += 10
                negativeMemory = -1
            }
            res = "${digit % 10}" + res
        }
        return UnsignedBigInteger(res)
    }

    /**
     * Умножение
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger {
        val biggerSize = maxOf(toString().length, other.toString().length)
        val smallerSize = minOf(toString().length, other.toString().length)
        val zeros = Regex("""(0*)$""").find(this.toString())?.value ?: "" +
        (Regex("""(0*)$""").find(other.toString())?.value ?: "")
        var res: String
        var memory = 0
        val terms = mutableListOf<UnsignedBigInteger>()
        for (j in 1..smallerSize) {
            var term = ""
            for (i in 1..biggerSize) {
                val digit: Int = memory + if (toString().length == biggerSize)
                    getValue(biggerSize - i) * other.getValue(smallerSize - j)
                else other.getValue(biggerSize - i) * getValue(smallerSize - j)
                memory = 0
                if (digitNumber(digit) > 1) memory += digit / 10
                term = "${digit % 10}" + term
            }
            if (memory != 0) term = "$memory$term"
            term += "0".repeat(j - 1)
            memory = 0
            terms.add(UnsignedBigInteger(term))
        }
        var uBIs = UnsignedBigInteger(0)
        for (t in terms) uBIs += t
        res = uBIs.toString()
        res = "$res$zeros"
        return UnsignedBigInteger(res)
    }

    /**
     * Деление
     */
    operator fun div(other: UnsignedBigInteger): UnsignedBigInteger {
        if (other == UnsignedBigInteger(0)) throw java.lang.ArithmeticException("cannot be divided by zero")
        if (other > this) return UnsignedBigInteger(0)
        val biggerSize = maxOf(toString().length, other.toString().length)
        val smallerSize = minOf(toString().length, other.toString().length)
        var res = ""
        var number = toString().substring(0, smallerSize)
        for (i in smallerSize..biggerSize) {
            var r = 0
            if (UnsignedBigInteger(number) < other) {
                number += if (i != biggerSize) getValue(i).toString() else 0
                res += "0"
                continue
            }
            while (other * UnsignedBigInteger(r) <= UnsignedBigInteger(number)) r++
            res += --r
            number = (UnsignedBigInteger(number) - UnsignedBigInteger(r) * other).toString() +
                    if (i != biggerSize) getValue(i).toString() else 0
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
        other is UnsignedBigInteger && (unsignedBigInteger == other.unsignedBigInteger)

    /**
     * Сравнение на больше/меньше (по контракту Comparable.compareTo)
     */
    override fun compareTo(other: UnsignedBigInteger): Int {
        if (toString().length > other.toString().length) return 1
        if (toString().length < other.toString().length) return -1
        for (i in 0..toString().lastIndex) {
            if (getValue(i) > other.getValue(i)) return 1
            if (getValue(i) < other.getValue(i)) return -1
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
    fun toInt(): Int =
        if (unsignedBigInteger.size > 1) throw ArithmeticException("too big number") else unsignedBigInteger[0]

    /**
     * Получение цифры по индексу
     */
    fun getValue(i: Int): Int {
        var count = 0
        for (j in 0 until unsignedBigInteger.size)
            for (k in 0 until digitNumber(unsignedBigInteger[j])) {
                if (count == i) return (unsignedBigInteger[j] / 10.0.pow(digitNumber(unsignedBigInteger[j]) - 1 - k)).toInt() % 10
                count++
            }
        throw IndexOutOfBoundsException("nonexistent index")
    }

    override fun hashCode(): Int = unsignedBigInteger.hashCode()
}