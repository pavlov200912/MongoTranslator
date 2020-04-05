// Can be more specific exceptions here
class ParseException(val errorResult: ParseError) : Exception("Could not parse input: $errorResult")

class EmptySeq<Token> : ParseError()

data class ParseExceptionMessage(val message: String): ParseError()