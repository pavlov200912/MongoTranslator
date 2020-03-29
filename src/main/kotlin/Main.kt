fun main() {
    /*val str = "SELECT name, surname FROM collection"
    val str2 = "SELECT * FROM customers WHERE age > 22"
    val reg = Regex("SELECT")
    val res =
        stringParser("SELECT")
        .and(whileNotStringParser("FROM"))
        .andMore(stringParser("FROM"))
        .andMore(spaceParser())
        .andMore(someLettersParser())
        .parse(str2.toList())
*/
    //println(res)

    println(parseSQL( "SELECT * FROM customers WHERE age > 22  LIMIT 12") ?: "")
}
