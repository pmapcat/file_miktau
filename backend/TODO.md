# What are pros/cons of working with DataBase vs working within FS

Database benefits:
* With database, no touching of user files happens (no errors, no problems)
* Database is consistent to implement. (timing on FS IO is unpredictable)

Database problems:
  * another source of truth. Synchronization problems
  * 
