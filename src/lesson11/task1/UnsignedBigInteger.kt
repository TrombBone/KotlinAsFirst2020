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
class UnsignedBigInteger : Comparable<UnsignedBigInteger> {

    private val unsignedBigInteger: MutableList<Int> = mutableListOf()

    /**
     * Конструктор из строки
     */
    constructor(s: String) {
        if (!s.matches(Regex("""\d+"""))) throw NumberFormatException("number contains unknown characters")
        try {
            unsignedBigInteger.add(s.toInt())
        } catch (e: NumberFormatException) {
            var postStr = Regex("""^(0+)""").replace(s, "")
            while (postStr.isNotEmpty()) {
                val startIntStr = try {
                    postStr.substring(0, if (postStr.length > 10) 10 else postStr.length).toInt()
                } catch (e: NumberFormatException) {
                    postStr.substring(0, 9).toInt()
                }
                unsignedBigInteger.add(startIntStr)
                if (digitNumber(startIntStr) - 1 == postStr.lastIndex) break
                else postStr = postStr.substring(digitNumber(startIntStr), postStr.length)
                while (postStr.matches(Regex("""0\d*"""))) {
                    unsignedBigInteger.add(0)
                    postStr = postStr.replaceFirst("0", "")
                }
            }
        }
    }

    /**
     * Конструктор из целого
     */
    constructor(i: Int) {
        unsignedBigInteger.add(i)
    }

    /**
     * Сложение
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger {
//        println("this unsignedBigInteger: $this")
//        println("other unsignedBigInteger: $other")
        val biggerSize = maxOf(toString().length, other.toString().length)
        val smallerSize = minOf(toString().length, other.toString().length)
        var res = ""
        var memory = 0
        for (i in 1..biggerSize) {
            val digit: Int = try {
                getValue(biggerSize - 1).toString() //условие try, toString() для ориентации
                getValue(biggerSize - i) + if (i >= smallerSize + 1) 0 else other.getValue(smallerSize - i)
            } catch (e: StringIndexOutOfBoundsException) {
                other.getValue(biggerSize - i) + if (i >= smallerSize + 1) 0 else getValue(smallerSize - i)
            } + memory
            memory = 0
            if (digitNumber(digit) > 1) memory++
            res = "${digit % 10}" + res
        }
        if (memory != 0) res = "1$res"
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
            var digit: Int = try {
                getValue(biggerSize - 1).toString()
                getValue(biggerSize - i) - if (i >= smallerSize + 1) 0 else other.getValue(smallerSize - i)
            } catch (e: StringIndexOutOfBoundsException) {
                other.getValue(biggerSize - i) - if (i >= smallerSize + 1) 0 else getValue(smallerSize - i)
            } + negativeMemory
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
                val digit: Int = try {
                    getValue(biggerSize - 1).toString()
                    getValue(biggerSize - i) * other.getValue(smallerSize - j)
                } catch (e: StringIndexOutOfBoundsException) {
                    other.getValue(biggerSize - i) * getValue(smallerSize - j)
                } + memory
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
    override fun equals(other: Any?): Boolean {
        if (other !is UnsignedBigInteger || toString().length != other.toString().length) return false
        for (i in 0..toString().lastIndex) if (toString()[i] != other.toString()[i]) return false
        return true
    }

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
    fun getValue(i: Int): Int = (toString()[i] - '0')
    override fun hashCode(): Int = unsignedBigInteger.hashCode()
}