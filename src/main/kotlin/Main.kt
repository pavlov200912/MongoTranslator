import java.lang.Error

fun main() {
    val sqlString = readLine() ?: throw Error("Input error")
    val res = parseSQL(sqlString) ?: throw Error("Can't parse SQLQuery")
    println(buildMongo(res))
}

fun buildMongo(sql: SQLEntity): String{
    var mongoQuery = "db."
    mongoQuery += sql.tableName + ".find("
    var mongoWhere = "{}"
    if (sql.whereCondition != null) {
        val op = when(sql.whereCondition!!.operation) {
            "<>" -> "ne"
            "<" -> "lt"
            ">" -> "gt"
            else -> ""
        }
        mongoWhere = "{ ${sql.whereCondition!!.field}: {\$$op: ${sql.whereCondition!!.value}} }"
    }
    mongoQuery += mongoWhere
    var mongoFields = ""
    if (sql.fieldNames != listOf("*")) {
        mongoFields += ", {"
        for (field in sql.fieldNames) {
            mongoFields += field + ": 1, "
        }
        mongoFields = mongoFields.dropLast(2)
        mongoFields += "}"
    }
    mongoQuery += mongoFields + ")"
    if (sql.skipValue != null) {
        mongoQuery += ".skip(${sql.skipValue})"
    }
    if (sql.limitValue != null) {
        mongoQuery += ".limit(${sql.limitValue})"
    }
    return mongoQuery
}