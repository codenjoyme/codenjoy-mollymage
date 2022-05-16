## В чем суть игры?

Будь внимателен: во время игры тебе стоит сосредоточиться на реализации логики
передвижения и активности героя. Остальное - подключение по websocket, некоторое
API для парсинга борды - уже реализовано для тебя.

Надо написать своего бота для героя, который обыграет других
ботов по очкам. Все играют на одном поле. Герой может передвигаться
по свободным ячейкам во все четыре стороны.

Герой может также поставить зелье. Зелье взорвется через 5 тиков
(секунд). Ядовитые газы от зелья могут зацепить обитателей поля.
Все, кто был задет - исчезает. С помощью зелья можно открывать сундуки.
Пострадать можно и на своем, и на чужом зелье. 

На своем пути герой может повстречать призрака - призрачная 
субстанция, уничтожающая на своем пути всех героев. 

Каждый разрушенный объект на поле (герой, призрак, сундуки)
тут же восстанавливается в другом месте. Если пострадал герой,
ему зачисляются штрафные очки -50[*](#ask). 

Герой, от зелья которого были открыты сундуки или уничтожены 
другие участники на карте получит
бонусные очки: за открытый сундук +10[*](#ask), 
за призрака +100[*](#ask), за
другого героя +1000[*](#ask). 

Очки суммируются. Побеждает игрок с большим числом очков (до условленного
времени).

[*](#ask)Точное количество очков за любое действие, а так же другие
настройки на данный момент игры уточни у Сенсея.