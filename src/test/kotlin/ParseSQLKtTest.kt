import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ParseSQLKtTest {

    @Test
    fun testParseSQL() {
        val sql1 = SQLEntity(
                tableName = "table",
                fieldNames = listOf("field1","field2")
            )
        sql1.limitValue = 10
        sql1.skipValue = 15
        sql1.whereCondition = WhereCondition(
            field = "field3",
            value = 15,
            operation = ">"
        )
        assertEquals(
            sql1,
            parseSQL(
                "SELECT field1, field2 FROM table WHERE field3 > 15 SKIP 15 LIMIT 10"
            )
        )
        val sql2 = SQLEntity(tableName = "collection",
            fieldNames = listOf("*"))
        sql2.skipValue = 5
        sql2.limitValue = 10
        assertEquals(
            sql2,
            parseSQL(
                "SELECT * FROM collection SKIP 5 LIMIT 10"
            )
        )

        val sql3 = SQLEntity(tableName = "collection",
            fieldNames = listOf("name", "surname"))
        assertEquals(
            sql3,
            parseSQL(
                "SELECT name, surname FROM collection"
            )
        )
    }

    @Test
    fun testParseSQLMainPart() {
        assertEquals(Pair(SQLEntity("table", listOf("*")), ""),
            parseSQLMainPart("SELECT * FROM table"))

        assertEquals(Pair(SQLEntity("table", listOf("*")), " LIMIT 10"),
            parseSQLMainPart("SELECT * FROM table LIMIT 10"))

        assertEquals(Pair(SQLEntity("table", listOf("name")), ""),
            parseSQLMainPart("SELECT name FROM table"))

        assertEquals(Pair(SQLEntity("table", listOf("field_1")), " somestr"),
            parseSQLMainPart("SELECT field_1 FROM table somestr"))


        assertEquals(Pair(SQLEntity("table", listOf("field_1", "field_2")), ""),
            parseSQLMainPart("SELECT field_1, field_2 FROM table"))

        assertEquals(Pair(SQLEntity("table", listOf("field_1", "field_2", "field_3", "field_4", "field_5")), ""),
            parseSQLMainPart("SELECT field_1, field_2,field_3,  field_4,     field_5 FROM table"))

        assertNull(parseSQLMainPart("SELECT FROM table"))
        assertNull(parseSQLMainPart("SELECT *, field FROM table"))
        assertNull(parseSQLMainPart("SELECT * table"))
        assertNull(parseSQLMainPart("SELECT * FROMtable"))
        assertNull(parseSQLMainPart("SELECT* FROM table"))
        assertNull(parseSQLMainPart("SELECT* FORM table"))

    }

    @Test
    fun testParseWhereSQL() {
        assertEquals(Pair(WhereCondition("field", 10, "<>"), ""),
            parseWhereSQL(" WHERE field <> 10"))

        assertEquals(Pair(WhereCondition("some_name", 123, "<"), ""),
            parseWhereSQL(" WHERE some_name < 123"))

        assertEquals(Pair(WhereCondition("FiEld_NamE", 1120, ">"), ""),
            parseWhereSQL(" WHERE FiEld_NamE > 1120"))


        assertEquals(Pair(WhereCondition("field", 10, "<>"), " SKIP LIMIT SKIP"),
            parseWhereSQL(" WHERE field <> 10 SKIP LIMIT SKIP"))

        assertNull(parseWhereSQL("WHERE field <> 10"))
        assertNull(parseWhereSQL(" WHERO field <> 10"))
        assertNull(parseWhereSQL(" WHERE field += 10"))
        assertNull(parseWhereSQL(" WHERE field <> abv"))

    }

    @Test
    fun testParseSkipSQL() {
        assertEquals(Pair(10, ""),
            parseSkipSQL(" SKIP 10"))

        assertEquals(Pair(12310, ""),
            parseSkipSQL(" SKIP 0012310"))

        assertEquals(Pair(10, " somestring"),
            parseSkipSQL(" SKIP 10 somestring"))

        assertNull(parseSkipSQL("SKIP 10"))
        assertNull(parseSkipSQL(" SKIP a10"))
        assertNull(parseSkipSQL(" SPIK 10"))

    }

    @Test
    fun testParseLimitSQL() {
        assertEquals(Pair(10, ""),
            parseLimitSQL(" LIMIT 10"))

        assertEquals(Pair(12310, ""),
            parseLimitSQL(" LIMIT 0012310"))

        assertEquals(Pair(10, " somestring"),
            parseLimitSQL(" LIMIT 10 somestring"))

        assertNull(parseLimitSQL("LIMIT 10"))
        assertNull(parseLimitSQL(" LIMIT a10"))
        assertNull(parseLimitSQL(" LIMTI 10"))
    }
}