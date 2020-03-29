data class Parser<Token, T>(val parse: (List<Token>) -> ParseResult<Token, T>)

// ParseResult is ParseError or Parsed(parsedValue, unParsedTail)
sealed class ParseResult<out Token,  out T>

abstract class ParseError: ParseResult<Nothing, Nothing>()

data class Parsed<Token, T>(val result: T, val remainder: List<Token>): ParseResult<Token, T>()


fun <Token, T> ParseResult<Token, T>.toParsedOrThrow() = when (this) {
    is Parsed -> this
    is ParseError -> throw ParseException(
        this
    )
}


fun <Token, T> ParseResult<Token, T>.errorToNull(): Parsed<Token, T>? {
    when(this) {
        is ParseError -> return null
        is Parsed -> return  this
    }
}

// Make Parser<Token> Functor by adding map function
// Converts Parser for type A to Parser for type B
fun <Token, A, B> Parser<Token, A>.map(transform: (A) -> B) : Parser<Token, B> =
    // (a -> b) -> Parser<A> -> Parser<B>
    Parser<Token, B>({sequence ->
        val firstResult = this.parse(sequence)
        when(firstResult) {
            is ParseError -> firstResult
            is Parsed     -> Parsed(transform(firstResult.result), firstResult.remainder)
        }
    })

// Make Parser<Token> Applicative by adding pure + app function
// returns Parser which accepting value on any input
fun <Token, A> pure(value: A): Parser<Token, A> =
    Parser{ seq: List<Token> ->
        Parsed<Token, A> (value, seq)
    }

// Applicative Parsers is concept from Functional Programming language as Haskell
fun <Token, A, B> Parser<Token, (A) -> B>.app(parser: Parser<Token, A>): Parser<Token, B> {
    return Parser<Token, B> {
            seq ->
        val firstResult = this.parse(seq)
        when(firstResult) {
            is ParseError -> firstResult
            is Parsed     -> {
                val function = (firstResult).result
                val secondSeq =  (firstResult).remainder
                val secondResult = parser.parse(secondSeq)
                when (secondResult) {
                    is ParseError -> secondResult
                    is Parsed -> {
                        Parsed(function(secondResult.result), secondResult.remainder)
                    }
                }
            }
        }
    }
}


// Alternative Parser bring OR semantic to parsers
// parser1 OR parser2 OR parser3 returns result of first success parsing
fun <Token, A> Parser<Token, A>.or(parser: Parser<Token, A>): Parser<Token, A> {
    return Parser {
        seq: List<Token> ->
        val firstResult = this.parse(seq)
        when(firstResult) {
            is ParseError -> parser.parse(seq)
            is Parsed     -> firstResult
        }
    }
}

fun <Token, A> Parser<Token, A>.and(parser: Parser<Token, A>): Parser<Token, MutableList<A>> {
    fun partialList(a: A) = {b: A -> mutableListOf(a, b) }
    return this.map {partialList(it)}.app(parser)
}

fun <Token, A> Parser<Token, MutableList<A>>.andMore(parser: Parser<Token, A>): Parser<Token, MutableList<A>> {
    fun append(list: MutableList<A>): (A) -> MutableList<A> {
        return {
            b: A ->
            list.add(b)
            list
        }
    }
    return this.map { append(it)}.app(parser)
}