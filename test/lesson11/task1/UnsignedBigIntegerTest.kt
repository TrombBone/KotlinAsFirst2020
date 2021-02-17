package lesson11.task1

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import java.lang.ArithmeticException

internal class UnsignedBigIntegerTest {

    @Test
    @Tag("8")
    fun plus() {
        assertEquals(UnsignedBigInteger(4), UnsignedBigInteger(2) + UnsignedBigInteger(2))
        assertEquals(UnsignedBigInteger("9087654330"), UnsignedBigInteger("9087654329") + UnsignedBigInteger(1))
        assertEquals(UnsignedBigInteger(2), UnsignedBigInteger(2) + UnsignedBigInteger("0000000"))
        assertEquals(UnsignedBigInteger(90876544), UnsignedBigInteger(1) + UnsignedBigInteger(90876543))
        assertEquals(
            UnsignedBigInteger("90876543291230303004"),
            UnsignedBigInteger("90876543291230303003") + UnsignedBigInteger(1)
        )
        assertEquals(
            UnsignedBigInteger("2147483648000"),
            UnsignedBigInteger("${Int.MAX_VALUE}999") + UnsignedBigInteger("1")
        )
        assertEquals(
            UnsignedBigInteger("4294967295998"),
            UnsignedBigInteger("${Int.MAX_VALUE}999") + UnsignedBigInteger("${Int.MAX_VALUE}999")
        )
        assertEquals(
            UnsignedBigInteger("90876543291230303004"),
            UnsignedBigInteger(1) + UnsignedBigInteger("90876543291230303003")
        )
        assertEquals(
            UnsignedBigInteger("908000000050000000007"),
            UnsignedBigInteger(1) + UnsignedBigInteger("908000000050000000006")
        )
        assertEquals(
            UnsignedBigInteger("128869304901888001"),
            UnsignedBigInteger(1) + UnsignedBigInteger("128869304901888000")
        )
    }

    @Test
    @Tag("8")
    fun minus() {
        assertEquals(UnsignedBigInteger(2), UnsignedBigInteger(4) - UnsignedBigInteger(2))
        assertEquals(UnsignedBigInteger("9087654329"), UnsignedBigInteger("9087654330") - UnsignedBigInteger(1))
        assertEquals(
            UnsignedBigInteger("4078814305"),
            UnsignedBigInteger("12668748897") - UnsignedBigInteger("8589934592")
        )
        assertEquals(
            UnsignedBigInteger(0),
            UnsignedBigInteger("${Int.MAX_VALUE}999") - UnsignedBigInteger("${Int.MAX_VALUE}999")
        )
        assertEquals(UnsignedBigInteger(908764976), UnsignedBigInteger(908765432) - UnsignedBigInteger(456))
        assertThrows(ArithmeticException::class.java) {
            UnsignedBigInteger(2) - UnsignedBigInteger(4)
        }
    }

    @Test
    @Tag("12")
    fun times() {
        assertEquals(
            UnsignedBigInteger("18446744073709551616"),
            UnsignedBigInteger("4294967296") * UnsignedBigInteger("4294967296")
        )
        assertEquals(UnsignedBigInteger(0), UnsignedBigInteger(0) * UnsignedBigInteger("4294967296"))
        assertEquals(
            UnsignedBigInteger("2764762067365105152"),
            UnsignedBigInteger(64362) * UnsignedBigInteger("42956434967296")
        )
    }

    @Test
    @Tag("16")
    fun div() {
        assertEquals(
            UnsignedBigInteger("4294967296"),
            UnsignedBigInteger("18446744073709551616") / UnsignedBigInteger("4294967296")
        )
        assertEquals(
            UnsignedBigInteger(6172839),
            UnsignedBigInteger(12345678) / UnsignedBigInteger(2)
        )
        assertEquals(
            UnsignedBigInteger(1447),
            UnsignedBigInteger("123456786518746529") / UnsignedBigInteger("85264963565376")
        )
        assertEquals(
            UnsignedBigInteger(0),
            UnsignedBigInteger("123456786518746529") / UnsignedBigInteger("8526674674744963565376")
        )
    }

    @Test
    @Tag("16")
    fun rem() {
        assertEquals(UnsignedBigInteger(5), UnsignedBigInteger(19) % UnsignedBigInteger(7))
        assertEquals(
            UnsignedBigInteger(0),
            UnsignedBigInteger("18446744073709551616") % UnsignedBigInteger("4294967296")
        )
    }

    @Test
    @Tag("8")
    fun equals() {
        assertEquals(UnsignedBigInteger(123456789), UnsignedBigInteger("123456789"))
    }

    @Test
    @Tag("8")
    fun compareTo() {
        assertTrue(UnsignedBigInteger(123456789) < UnsignedBigInteger("9876543210"))
        assertTrue(UnsignedBigInteger("9876543210") > UnsignedBigInteger(123456789))
    }

    @Test
    @Tag("8")
    fun toInt() {
        assertEquals(123456789, UnsignedBigInteger("123456789").toInt())
    }
}