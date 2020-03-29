

fun spaceParser(): Parser<Char, String> {
    fun parseSpaces(sequence: List<Char>): ParseResult<Char, String> {
        if (sequence.isEmpty())
            return EmptySeq<Char>()
        return Parsed("",
            sequence.joinToString("").trimStart()
                .toList())
    }
    return Parser {
            seq ->
        parseSpaces(seq)
    }
}

fun charParser(symbol: Char): Parser<Char, Char> = getSatisfyParser { it == symbol }

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

fun whileNotStringParser(stopString: String): Parser<Char, String> {
    fun parseWhileNotString(sequence: List<Char>): Parsed<Char, String> {
        if (stopString.isEmpty()) {
            return Parsed("", sequence)
        }
        if (sequence.size < stopString.length) {
            return Parsed(sequence.joinToString(""), emptyList())
        }
        val stringSeq = sequence.joinToString("")
        val stringBeforeStop = stringSeq.substringBefore(stopString)
        if (stringBeforeStop.length == stringSeq.length)
            return Parsed(stringBeforeStop, emptyList())
        return Parsed(stringSeq.substringBefore(stopString),
            (stopString + stringSeq.substringAfter(stopString)).toList())
    }
    return Parser{
            seq ->
        parseWhileNotString(seq)
    }
}

fun <A> getSatisfyParser(predicate: (A) -> Boolean) : Parser<A, A>{
    fun parseSatisfy(sequence: List<A>): ParseResult<A, A> {
        if (sequence.isEmpty())
            return EmptySeq<A>()
        if (!predicate(sequence[0]))
            return PredicateFailure<A>(sequence[0])
        return Parsed<A, A>(sequence[0], sequence.drop(1))
    }
    return Parser({seq -> parseSatisfy(seq)})
}

fun someLettersParser(): Parser<Char, String> =
    somePredicateParser<Char> { c -> c.isLetter() }.map { it -> it.joinToString ("") }

fun someDigitParser(): Parser<Char, String> =
    somePredicateParser<Char> { c -> c.isDigit() }.map { it -> it.joinToString ("") }

// TODO:  Documentation
fun identificatorParser(): Parser<Char, String> =
    somePredicateParser<Char> {c -> c.isLetter() or c.isDigit() or (c == '_')}.map{it.joinToString("")}


fun <A> somePredicateParser(predicate: (A) -> Boolean): Parser<A, List<A>> {
    fun parseLetters(sequence: List<A>): ParseResult<A, List<A>> {
        if (sequence.isEmpty()) {
            return EmptySeq<Char>()
        }
        return Parsed(
            sequence.takeWhile(predicate),
            sequence.dropWhile(predicate))
    }
    return Parser{
            seq ->
        parseLetters(seq)
    }
}