CS576 HW6

Compile:
javac Router.java Const.java Client.java

Run Server:
java Router <port>

Run Client:
Open ten terminals, each run
java Client  <server_addr> <server_port> <clientId>
where clientId is 0 to 9 respectively

Expected output:
Client will output message:
Client <source> sent <fileName> to client <destination>
Client <destination>: Received packet from <source>
For example: 
Client 2 sent fileA.txt to client 5
Client 2: Received packet from 1

Each client will send a random file to a random destination, with an interval between 1 seconds and 11 seconds

There should be new files, named file_from_*_to_*, e.g file_from_2_to_5, file_from_1_to_2.
These are the files received. You can do diff to verify it
