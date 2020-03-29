
fun parseSQL(sqlString: String): SQLEntity? {
    var currentString = sqlString
    val mainParseResult = parseSQLMainPart(currentString) ?: return null
    currentString = mainParseResult.second
    val sqlEntity = mainParseResult.first
    val whereParseResult= parseWhereSQL(currentString)
    if (whereParseResult != null) {
        sqlEntity.whereCondition = whereParseResult.first
        currentString = whereParseResult.second
    }
    val skipParseResult = parseSkipSQL(currentString)
    if (skipParseResult != null) {
        sqlEntity.skipValue = skipParseResult.first
        currentString = skipParseResult.second
    }

    val limitParseResult = parseLimitSQL(currentString)
    if (limitParseResult != null) {
        sqlEntity.limitValue = limitParseResult.first
        currentString = limitParseResult.second
    }
    return sqlEntity
}

data class SQLEntity(val tableName: String, val fieldNames: List<String>) {
    var whereCondition: WhereCondition? = null
    var skipValue: Int? = null
    var limitValue: Int? = null
    override fun toString(): String {
        return """tableName: $tableName, fieldNames: $fieldNames
            skipValue: ${skipValue ?: "null"}
            limitValue: ${limitValue ?: "null"}
            whereCondition: ${whereCondition ?: "null"}
        """.trimMargin()
    }
}

data class WhereCondition(val field: String, val value: Int, val operation: String)

// Parse SELECT <some_fields> FROM <table_name>
fun parseSQLMainPart(sqlString: String): Pair<SQLEntity, String>? {
    val parseBase = stringParser("SELECT")
        .and(whileNotStringParser("FROM"))
        .andMore(stringParser("FROM"))
        .andMore(spaceParser())
        .andMore(identificatorParser())
        .parse(sqlString.toList())
        .errorToNull() ?: return null
    val fieldNames = parseBase.result[1]
    val tableName = parseBase.result[4]
    val unParsedString = parseBase.remainder.joinToString("")
    // Parse fieldNames <name1>, <name2>, <name3> OR *
    val separatedNames = fieldNames.split(',').map{it.trim()}
    var isSelectAll: Boolean = false

    if (separatedNames.size == 1 && separatedNames[0] == "*")
        isSelectAll = true
    if (!isSelectAll) {
        separatedNames.forEach {
            if (!it.all { c -> c.isLetter() })
                return null
        }
    }

    // Check if tableName is sequence of letters
    if (!tableName.all { it.isLetter() })
        return null

    return Pair(SQLEntity(tableName, separatedNames), unParsedString)
}

// TODO: Parse boolean arithmetic
fun parseWhereSQL(sqlString: String): Pair<WhereCondition, String>? {
    val parseResult =
        spaceParser()
            .and(stringParser("WHERE"))
            .andMore(spaceParser())
            .andMore(identificatorParser())
            .andMore(spaceParser())
            .andMore(stringParser("<>").or(stringParser("<").or(stringParser(">"))))
            .andMore(spaceParser())
            .andMore(someDigitParser())
            .parse(sqlString.toList())
            .errorToNull() ?: return null
    return Pair(
        WhereCondition(
            field = parseResult.result[3],
            value = parseResult.result[7].toInt(),
            operation = parseResult.result[5]
        ),
        parseResult.remainder.joinToString("")
    )
}


fun parseResultBounds(sqlString: String, command: String): Pair<Int, String>? {
    val parseResult =
        spaceParser()
            .and(stringParser(command))
            .andMore(spaceParser())
            .andMore(someDigitParser())
            .parse(sqlString.toList())
            .errorToNull() ?: return null
    return Pair(
        parseResult.result[3].toInt(),
        parseResult.remainder.joinToString("")
    )
}

fun parseSkipSQL(sqlString: String): Pair<Int, String>? =
    parseResultBounds(sqlString, "SKIP")

fun parseLimitSQL(sqlString: String): Pair<Int, String>? =
    parseResultBounds(sqlString, "LIMIT")
