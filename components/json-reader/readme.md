# corespin.json-reader - Reads json files

## Design considerations

- *Schema*: We need to establish a solid schema for the JSON file. Each object in the array represents a 'record'. We need to list the fields which will be part of each record. It is important to maintain consistency throughout all records.

- *Size of the file*: If a JSON file is going to be very large we might face performance issues. Loading a large file might consume lots of memory and reading and writing operations might be slow. Do we need to handle extra-large files that cannot fit in memory?

- *Parsing*: Use a reliable JSON parsing library that can handle various data types present in JSON, like nested objects, array etc. Probably Cheshire, it handle JSON data with ease.

- *File Access*: We need to decide how the file will be accessed. Do we open it once and keep it open, or do you open/close for each operation? The latter can ensure the file is always up-to-date at the potential cost of efficiency. Do we need to consider JSON file getting updates?

- *Data Integrity*: If JSON data can potentially be updated, we should always verify incoming data before appending to the JSON file to maintain the integrity of our 'database'.

- *Concurrency*: If multiple threads or processes will be accessing this file do we need a mechanism to handle possible concurrency issues and conflicts?

- *Exception Handling*: Think of any unexpected scenarios that may arise.

## Initial draft

- Let's read the file in memory, unless it exceeds a given size, then it should not be cached in memory, but should be streamed

- We probably don't want to hardcode the max file size to consider before streaming 
