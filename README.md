# CPU Simulator
## Compile only the main file with javac, after which you will get the main.class file. After running it, also transfer the code.txt file.

1. You have the following registries 
    > General use registers:
            AYB ,
            BEN,
            GIM,
            DA,
            ECH
   
    > Flag register: where OF, CF, SF, ZF are inserted
            ZA
+ We have 32 bytes of RAM where the program is installed.
+ Each instruction occupies 2 bytes of memory and is placed in RAM from address 0, and the rest of the memory is subject to use.
+ We can read from RAM as follows [31] specifying the address.
+ If you specify the memory belonging to the program, we will get a Runtime Error
+ We can specify a string using ':' before the instruction and giving the corresponding label.

2. Have the following instructions set that accept:

    | isnt. name | First argument | Second argument |
    |        :---: |     :---:      |         :---:   |
    |    | register | memory  |    |
    |    |   |    |
