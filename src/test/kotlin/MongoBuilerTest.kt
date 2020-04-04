import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MongoBuilerTest {
    @Test
    fun test_builder(){
        val sql1 = SQLEntity(tableName = "collection",
            fieldNames = listOf("name", "surname"))
        assertEquals("db.collection.find({}, {name: 1, surname: 1})",
            buildMongo(sql1))
        val sql2 = SQLEntity(tableName = "customers", fieldNames = listOf("*"))
        sql2.whereCondition = WhereCondition(field = "age", value = 22, operation = ">")
        assertEquals("db.customers.find({ age: {\$gt: 22} })",
            buildMongo(sql2))
        val sql3 = SQLEntity(tableName = "collection", fieldNames = listOf("*"))
        sql3.limitValue = 10
        sql3.skipValue = 5
        assertEquals("db.collection.find({}).skip(5).limit(10)",
            buildMongo(sql3))
        val sqlFull = SQLEntity(tableName = "my_table",
            fieldNames = listOf("field1", "field2", "field3"))
        sqlFull.whereCondition = WhereCondition(field = "field4", value = 23, operation = "<>")
        sqlFull.skipValue = 12
        sqlFull.limitValue = 22
        assertEquals("db.my_table.find({ field4: {\$ne: 23} }, {field1: 1, field2: 1, field3: 1}).skip(12).limit(22)",
            buildMongo(sqlFull))
    }
}