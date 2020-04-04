// Parse (' ')+
fun spaceParser(): Parser<Char, String> {
    fun parseSpaces(sequence: List<Char>): ParseResult<Char, String> {
        if (sequence.isEmpty())
            return EmptySeq<Char>()
        if (sequence[0] != ' ')
            return ParseExceptionMessage("space wasn't found in ${sequence}")
        return Parsed("",
            sequence.joinToString("").trimStart()
                .toList())
    }
    return Parser {
            seq ->
        parseSpaces(seq)
    }
}

// Parse string*  : eats string from beginning and returns everything after
fun stringParser(string: String): Parser<Char, String> {
    fun parseString(sequence: List<Char>): ParseResult<Char, String> {
        if (sequence.joinToString("").startsWith(string)) {
            return Parsed(string, sequence.drop(string.length))
        }
        return ParseExceptionMessage("parse string: $string error on input: ${sequence.joinToString("")}")
    }
    return Parser {
            chars -> parseString(chars)
    }
}

// Parse *string : eats everything before string and returns it
// Returns input untouched if didn't found the stopString
fun whileNotStringParser(stopString: String): Parser<Char, String> {
    fun parseWhileNotString(sequence: List<Char>): Parsed<Char, String> {
        if (stopString.isEmpty()) {
            return Parsed("", sequence)
        }
        val stringSeq = sequence.joinToString("")
        val stringBeforeStop = stringSeq.substringBefore(stopString)
        if (stringBeforeStop.length == stringSeq.length)
            return Parsed(stringBeforeStop, emptyList())
        return Parsed(stringBeforeStop,
            (stopString + stringSeq.substringAfter(stopString)).toList())
    }
    return Parser{
            seq ->
        parseWhileNotString(seq)
    }
}


// Parse (\d)+  : eats digits sequence and returns the rest
// Not (\d)* ! Should be some digit in the beginning
fun someDigitParser(): Parser<Char, String> =
    somePredicateParser<Char> { c -> c.isDigit() }.map { it -> it.joinToString ("") }

// Parse ([a-z\\d_])+ : eats digits or letters or _ sequence and returns the rest
// Not ([a-z\\d_])* ! Should be some digit or letter or _ in the beginning
fun identifierParser(): Parser<Char, String> =
    somePredicateParser<Char> {c -> c.isLetter() or c.isDigit() or (c == '_')}.map{it.joinToString("")}


// Parse values of type A in List<A> while predicate is truth
// Require predicate from first element of sequence to be true
fun <A> somePredicateParser(predicate: (A) -> Boolean): Parser<A, List<A>> {
    fun parseLetters(sequence: List<A>): ParseResult<A, List<A>> {
        if (sequence.isEmpty()) {
            return EmptySeq<Char>()
        }
        if (!predicate(sequence[0]))
            return ParseExceptionMessage("some predicate's true value expected but not found ${sequence}")
        return Parsed(
            sequence.takeWhile(predicate),
            sequence.dropWhile(predicate))
    }
    return Parser{
            seq ->
        parseLetters(seq)
    }
}