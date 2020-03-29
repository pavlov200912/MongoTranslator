import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ParsersUtilsKtTest {

    @Test
    fun testSpaceParser() {
        assertEquals(Parsed<Char, String>("", listOf()),
            spaceParser().parse("   ".toList()))
        assertEquals(Parsed<Char, String>("", listOf('a', 'b', 'c')),
            spaceParser().parse("   abc".toList()))
        assertEquals(Parsed<Char, String>("", "abc asd a v b asd".toList()),
            spaceParser().parse("   abc asd a v b asd".toList()))

        assert(spaceParser().parse(emptyList()) is ParseError)
        assert(spaceParser().parse("asd".toList()) is ParseError)
        assert(spaceParser().parse("a s d".toList()) is ParseError)
    }

    @Test
    fun testStringParser() {
        assertEquals(Parsed<Char, String>("string", listOf()),
            stringParser("string").parse("string".toList()))
        assertEquals(Parsed<Char, String>("string", "someother".toList()),
            stringParser("string").parse("stringsomeother".toList()))
        assert(stringParser("string").parse("stingstring".toList()) is ParseError)
    }

    @Test
    fun testWhileNotStringParser() {
        assertEquals(Parsed<Char, String>("", "string".toList()),
            whileNotStringParser("string").parse("string".toList()))
        assertEquals(Parsed<Char, String>("someother", "string".toList()),
            whileNotStringParser("string").parse("someotherstring".toList()))
        assertEquals(Parsed<Char, String>("somtotherstRing", "".toList()),
            whileNotStringParser("string").parse("somtotherstRing".toList()))
    }

    @Test
    fun testSomeDigitParser() {
        assertEquals(Parsed<Char, String>("12345", "rest".toList()),
            someDigitParser().parse("12345rest".toList()))
        assertEquals(Parsed<Char, String>("12345", "".toList()),
            someDigitParser().parse("12345".toList()))
        assert(someDigitParser().parse("a123".toList()) is ParseError)
    }

    @Test
    fun testIdentifierParser() {
        assertEquals(Parsed<Char, String>("abcd_12345", "*rest".toList()),
            identifierParser().parse("abcd_12345*rest".toList()))
        assertEquals(Parsed<Char, String>("__12345AAA", "".toList()),
            identifierParser().parse("__12345AAA".toList()))
        assert(identifierParser().parse("*a123".toList()) is ParseError)
    }
}