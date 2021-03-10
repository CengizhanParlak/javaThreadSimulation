# Synchronous Thread Simulation

---
**Assignment Date**: 11/15/2019-12/10/2019  
**Subject**: Process Management on Synchronous Threads  
**Chosen Language**: Java  
**Assignment is controlled by**: Researcher M. A. A. and Researcher H. O.  
**Grade**: A+  

---
This project is designed and developed to simulate the synchronous threads running on an application and managing processes between these (hypothetical) servers (threads).  

At the very beginning of the program, there are 1 Main Server and 2 Sub Servers (threads) on the application, to add that, there are 1 thread for creating Main Thread and 1 for managing Sub Thread operations (like creating a new thread or terminating the ongoing one). There are total of 5 threads at the beginning. (From now on "**Server(s)**" will be used as "**Thread(s)**" in this context for the sake of verbalism.)  

###Main Thread  

---
Main Thread is the heart of the program. At first, user requests (processes) land on this thread. This thread has a maximum process limit of 10,000 and an adjustable process answering limit. You can choose 50 or 350 processes to be answered at every second from the buttons on the application.  

There is also an option for both main and sub threads to change the incoming processes at any second.

###Main Thread Logic and Overflowing

---
Main Thread has no protective measures for overflowing. Only option for admin(s) to not lose any processes, is by adjusting the limits of incoming and outgoing processes accordingly.

###Sub Thread(s)

---
Main Thread directs processes to Sub Threads accordingly to the servers' process limits. Sub Threads has the process capacity of 5,000.

###Sub Thread Logic and Overflowing

---
Sub Threads have an overflow protection from the design. When the processes surpass the %70 of Sub Thread's the capacity (which is 3500), a new Sub Thread is created and new Thread will have half of the processes that were on the overflowed Thread. So now, they both have the 1750 processes on them.  

There can only be 8 sub threads because of the design of the code but source code is adjustable for any number of thread to be implemented to the application. 

###Screenshots  

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Program SS")  
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Program SS")  
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Program SS")  
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Program SS")  
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Program SS")  

###Demo  

[![demo capture](https://img.youtube.com/vi/GJzdTiB9Fpw/0.jpg)](https://www.youtube.com/watch?v=GJzdTiB9Fpw)