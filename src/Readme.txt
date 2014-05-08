CS576 HW6

Compile:
javac Router.java Const.java Client.java

Run Server:
java Router <port>

Run Client:
java Client  <server_addr> <server_port>

Expected output:
Client will output message:
Client <source> sent <fileName> to client <destination>
For example: Client 2 sent fileA.txt to client 5

It should take no more than 6 seconds to see all clients randomly pick a file and a target to send. Then kill Router, Client will be automatically closed

There should be 10 new files, named file_from_*_to_*, e.g file_from_2_to_5.
These are the files received. You can do diff to verify it
