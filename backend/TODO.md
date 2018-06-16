# What are pros/cons of working with DataBase vs working within FS

Database benefits:
* With database, no touching of user files happens (no errors, no problems)
* Database is consistent to implement. (timing on FS IO is unpredictable)

Database problems:
  * another source of truth. Synchronization problems
  * 
  
## What if I do immutable on FS (a.k.a. readonly system)
* No hard (a.k.a. by mistake, deleted all user files) operations
* Reasonable performance 
* Does not change user directory structure
* Potential issues:
  * Does not change user directory structure
  
### How would it look like (work)? 
On init (read):
* Read only source of truth (File System)
* Then, read "patch" from a database file (in the same directory): 
  * In patch ("what directories/tags are "removed")
  * What directories "tags" are added.
  * Apply patch
  
On update (write):
  * Patch data resolution:
    * Write down what to delete (into delete, of patch)
    * Remove, (what to delete) from (what to add) on patch
    * Remove (what to add) from (what to delete) on patch
    * Save patched data into a patch database.
    
Thus, making this persistence seamless. I.e. no need to think 
about potential harm that could be done to user system. 

Another benefit, it is an easily pluggable system. 

And, it should be performant, without much time with changing inodes and stuff.

Basically, it should work on "Aggregation" level. 

### Okay, simpler example:

I've got record. 
It has <Program files> <dacha> <hello_world> directories on it. 
In patch it has {:remove ["Program files" "whatever" "hello world"]}

I've got to 
* delete several tags
* add some tags

To a given query set. 


