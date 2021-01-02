# FileDB
A File based key-value data store which supports Create, Read and Delete Operations (CRD).


##### Functional requirements:
1. It can be initialized using an optional file path. If one is not provided, it will reliably create itself in a reasonable location on the computer.
2. A new key-value pair can be added to the datastore using the Create operation. The key is always a string - capped at 32 characters. The value is always a JSON object - capped at 16KB.
3. If Create is invoked for an existing key, an appropriate error must be returned.
4. A Read operation on a key can be performed by providing the key, and receiving the value in respose, as a JSON object.
5. A Delete operation can be performed by providing the key.
6. Every key supports setting a Time-To-Live property when it is created. This property is optional. If provided, it will be evaluated as an integer defining the number of seconds the key must be retained in the datastore. Once the Time-To-Live for a key has expired, the key will no longer be available for Read or Delete operations.
7. Appropriate error responses must always be returned to a client if it uses the datastore in unexpected ways or breaches any limits.
##### Non-Functional requirements:
1. The size of the file storing data must never exceed 1GB.
2. More than one client process cannot be allowed to use the same file as a data store at any given time.
3. A client process is allowed to access the datastore using multiple threads, if it desires to. The datastore must therefore be thread-safe.
4. The client will bear as little memory costs as possible to use this datastore, while deriving maximum performance with respect to response times for accsesing the datastore.

## Features of FileDB

* Data is stored using `RandomAcessFile` for flexibity of Cursor movement.
* Keys and Values are always provided as Strings.
* For more utilisation of Storage, **Compaction** is implemented efficently.
* Storage is divided into blocks of variable length and allocation of memory is done using `BESTFIT` strategy.

Interface ` DataStore ` provides 
``` 
  void create(String key, String value)
  void create(String key, String value, int ttl)
  void delete(String key)
  String read(String key)
  void close() 
```
On failure of any operation respective `DataStoreException` will be raised.

`DataStore ` is implemented by class ` DBStore ` 
 To get instance of DBStore an empty Constructor can be called directly or FilePath can be passed as an argument to the Constructor.
 
  
