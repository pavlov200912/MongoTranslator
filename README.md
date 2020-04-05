 # Mongo Translator
 
 Преобразует SELECT SQL запросы в диалект mongo. 
 
 Используются свои Applicative & Alternative Parsers (концепции парсеров из функционального программирования, 
 успешно ложатся на Kotlin) 
 
 Парсятся не все SQL запросы, а только часть, удовлетворяющая грамматике
 
 ```
<spaces> -> (' ')+
 <space*> -> (' ')*
```
Запрос разбивается на главную и хвостовую часть
```
 <query> -> <main query><tail query>
``` 
Главная - SELECT и FROM
```
<main query> -> SELECT<spaces><fields><spaces>FROM<spaces><identifier><spaces>
```
Грамматика для полей из SELECT: 
```
<fileds> -> <spaces><identifier>(<comma name>)* <spaces> | <spaces>'*'<spaces>
<comma name> -> <space*>,<space*><identifier><space*>
```
 Хвостовая часть - WHERE 
 ```
<tail query> -> <where query><bound query>
<where query> -> (<spaces>WHERE<spaces><identifier><spaces><operator><spaces><number>) | ''
<operator> -> <> | > | <
```
И ограничения (SKIP & LIMIT)
```
<bounds query> -> <skip query><limit query>
<skip query> -> (<spaces>SKIP<spaces><number>) | ''
<limit query> -> (<spaces>LIMIT<spaces><number>) | ''
```

SQL запрос может содержать конструкции намного сложнее, это
упрощенный вариант грамматики.  
