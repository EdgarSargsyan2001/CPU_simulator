# CPU Simulator
## Compile only the main file with javac, after which you will get the main.class file. After running it, also transfer the code.txt file.

1. You have the following registries 
   | General purpose register | flags register | | FLAGS |
    | :---: | :---: |  :---: |
    | AYB  | ZA    |  OF     |
    | BEN  |       |  CF     |
    | GIM  |       |  SF     |
    |  DA  |       |  ZF     |
    |  ECH |       |         |
   
   
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
    |    | register , memory  | register , memory , literal   |
    | MOV   | +  +  | + + +   |
   | ADD   | + -  | + + +   |
   | SUB    | +  -  | + + +   |
   | MUL   | +  -  | + + +   |
   | DIV   | +  -  | + + +   |
   | AND   | +  -  | + + +   |
   | OR   | +  -  | + + +   |
   | XOR  | +  -  |+ + +    |
   | NOT | +  -  | - - -    |
   | CMP    | +  -  | + + +   |
   
   | isnt. name  | argument |
    | ------------- | ------------- |
    | JMP  | Label  |
    | JG  | Label  |
   | JL  | Label  |
   | JE  | Label  |

+ the value of these operations is stored in the first register
+ The response of the CMP operation is stored in the DA register, which is used by the JG, JL, JE instructions.
  
       DA > 0։ JG 
       DA < 0։ JԼ 
       DA  = 0 : JE
+ You can use both uppercase and lowercase letters for instruction and register names
+ We have a set limit of 500 instruction execution to prevent infinite loop in the program
