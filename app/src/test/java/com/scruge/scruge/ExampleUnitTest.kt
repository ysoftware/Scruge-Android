package com.scruge.scruge

import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.dataformatting.isValidEmail
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    @Test
    fun formatRounding() {

        // currency
        assertEquals("10.0001", 10.0001.formatRounding(4, 4))
        assertEquals("10.1000", 10.1000.formatRounding(4, 4))
        assertEquals("10.0000", 10.0000.formatRounding(4, 4))

        // separation
        assertEquals("10 0001", 10.0001.formatRounding(4, 4, " "))

        // rounding
        assertEquals("10000.5", 10000.50.formatRounding())
        assertEquals("10000", 10000.04.formatRounding(0))
        assertEquals("10000", 10000.0.formatRounding(0))

        assertEquals("1.0000", 1.0.formatRounding(10, 4))
        assertEquals("1.000001", 1.000001.formatRounding(10, 4))
    }

    @Test
    fun format() {
        assertEquals("1", 1.0.formatDecimal())
        assertEquals("1.1", 1.1.formatDecimal())
        assertEquals("1.01", 1.0100.formatDecimal())

        assertEquals("1 000 000", 1000000.formatDecimal(" "))
        assertEquals("1000000", 1000000.formatDecimal(""))
        assertEquals("1 000 000.5", 1000000.5000.formatDecimal(" "))
    }

    @Test
    fun emailValidation() {
        val validEmails = """email@example.com
firstname.lastname@example.com
email@subdomain.example.com
firstname+lastname@example.com
email@123.123.123.123
email@[123.123.123.123]
1234567890@example.com
email@example-one.com
_______@example.com
email@example.name
email@example.museum
email@example.co.jp
firstname-lastname@example.com""".split("\n").map { it.trim() }

        var failedValid = 0
        validEmails.forEach {
            if (!it.isValidEmail()) {
                print("Should be valid: $it\n")
                failedValid += 1
            }
        }

        val invalidEmails = """tiny
plainaddress
#@%^%#$@#$@#.com
@example.com
Joe Smith <email@example.com>
email.example.com
email@example@example.com
.email@example.com
email.@example.com
email..email@example.com
あいうえお@example.com
email@example.com (Joe Smith)
email@example
email@-example.com
email@example..com
Abc..123@example.com""".split("\n").map { it.trim() }

        var failedInvalid = 0
        invalidEmails.forEach {
            if (it.isValidEmail()) {
                print("Should be invalid: $it\n")
                failedInvalid += 1
            }
        }

        assertEquals(0, failedInvalid + failedValid)
    }
}