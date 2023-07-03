package CPU;

import CPU.Registers.Registers;
import CPU.ParserInstruction.ParserInstruction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class CPU {

    public CPU() {
        this._RAM = new byte[_RAM_size];
        this._inst_codes = new InstrucionCodes();
        this._registers = new Registers();
    }

    public boolean execute_program(String fileMame, String mode) {
        read_and_parse_file(fileMame);

        if (mode.equals("debugger")) {
            try (Scanner myObj = new Scanner(System.in)) {
                while (execute_instruction()) {
                    dump_memory();
                    print_registers_value();
                    System.out.println("press Enter to go to next | to exit dial * ");
                    String option = myObj.nextLine();
                    if (option.equals("*")) {
                        return false;
                    }
                }
                myObj.close();
            }
        } else {
            int limit = 1000;
            while (execute_instruction()) {
                if (limit-- == 0) {
                    System.out.println("infinite loop: limit is over");
                    return false;
                }

            }
        }
        return true;
    }

    private void read_and_parse_file(String fileMame) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileMame));
            List<String> lines = reader.lines().toList();

            ParserInstruction parsedData = new ParserInstruction(_registers, _RAM_size, lines, _inst_codes);

            // loader
            byte size = parsedData.load_instruction_to_RAM(_RAM);
            _free_memory_index = size;
            _registers.set_instruction_pointer(0);

            // parsedData.print_binary_code();
            reader.close();

        } catch (IOException e) {
            throw new Error("!! File not found");
        }

    }

    private boolean execute_instruction() {

        byte IP = _registers.get_instruction_pointer();
        byte upCode = _RAM[IP++];
        byte enCode = _RAM[IP++];
        _registers.set_instruction_pointer(IP);

        byte instCode = _inst_codes.get_instruction_code(upCode);
        switch (_inst_codes.get_instruction_type(instCode)) {
            case 0:
                exe_mov(upCode, enCode);
                return true;
            case 1:
            case 2:
                do_inst(instCode, upCode, enCode);
                return true;
            case 3:
            case 4:
                return exe_function_call_ret(upCode, enCode);
            case 9:
                exe_jmps(upCode, enCode);
                return true;
        }

        System.out.println("ERROR: instruction binary conde not found!");
        return false;
    }

    private boolean exe_function_call_ret(byte upCode, byte enCode) {
        String key = _inst_codes.get_jumps_key(upCode);
        switch (key) {
            case "CALL":
                byte memoryAddres = enCode;
                if (memoryAddres >= _free_memory_index) {
                    throw new RuntimeException("!CALL: this memory isn't public");
                }
                push(_registers.get_instruction_pointer());
                _registers.set_instruction_pointer(memoryAddres);
                return true;
            case "RET":
                byte retAddres = pop();
                if (retAddres == -1) {
                    push((byte) -1);
                    System.out.println("Program over successfully");
                    return false;
                }
                _registers.set_instruction_pointer(retAddres);
                return true;
        }

        return false;
    }

    private void exe_jmps(byte upCode, byte enCode) {
        byte memoryAddres = enCode;

        if (memoryAddres >= _free_memory_index) {
            throw new RuntimeException("!JMP: this memory isn't public");
        }

        switch (_inst_codes.get_jumps_key(upCode)) {
            case "JMP":
                _registers.set_instruction_pointer(memoryAddres);
                return;
            case "JE":
                if (_registers.get_register_value("DA") == 0) {
                    _registers.set_instruction_pointer(memoryAddres);
                }
                return;
            case "JG":
                if (_registers.get_register_value("DA") > 0) {
                    _registers.set_instruction_pointer(memoryAddres);
                }
                return;
            case "JL":
                if (_registers.get_register_value("DA") < 0) {
                    _registers.set_instruction_pointer(memoryAddres);
                }
                return;
        }
    }

    private void do_inst(byte instCode, byte upCode, byte enCode) {

        byte regCode = (byte) ((upCode & 0b00001110) >> 1);
        int op1 = Byte.toUnsignedInt(get_upcode_operand(upCode));
        int op2 = Byte.toUnsignedInt(get_encode_operand(get_encode_type(upCode), enCode));
        int result = 0;

        switch (_inst_codes.get_instruction_key(instCode)) {
            case "ADD":
                result = op1 + op2;
                break;
            case "SUB":
                result = (op1 - op2);
                break;
            case "DIV":
                result = (op1 / op2);
                break;
            case "MUL":
                result = (op1 * op2);
                break;
            case "AND":
                result = (op1 & op2);
                break;
            case "OR":
                result = (op1 | op2);
                break;
            case "XOR":
                result = (op1 ^ op2);
                break;
            case "NOT":
                result = ~op1;
                break;
            case "CMP":
                _registers.set_register_value("DA", (op1 - op2));
                return;
        }
        _registers.set_register_value(regCode, result);
        _registers.set_flags_register(result);
    }

    private void exe_mov(byte upCode, byte enCode) {

        if (_inst_codes.get_instruction_code(upCode) == 0b1011) {

            byte regCode = (byte) ((upCode & 0b00001110) >> 1);
            _registers.set_register_value(regCode, get_encode_operand(get_encode_type(upCode), enCode));

        } else {
            byte memoryCode = (byte) (((upCode & 0b00111110) >> 1) + (_segment << 5));
            if (memoryCode < _free_memory_index) {
                throw new RuntimeException("this memory isn't public");
            }

            _RAM[memoryCode] = get_encode_operand(get_encode_type(upCode), enCode);
        }

    }

    private byte get_encode_operand(String type, byte enCode) {
        if (type == "literal") {
            return enCode;
        } else if (type == "regORmem") {// reg or mem

            String type2 = get_encode_type2(enCode);

            if (type2 == "register") { // register
                byte regCode = (byte) (enCode & 0b00011111);
                return _registers.get_register_value(regCode);
            }
            if (type2 == "memory") { // memory
                byte memoryCode = (byte) ((enCode & 0b00011111) + (_segment << 5));

                if (memoryCode < _free_memory_index) {
                    throw new RuntimeException("this memory isn't public");
                }

                return _RAM[memoryCode];
            } else {
                System.out.println("ERROR: second operand type2 not found");
                return -1;
            }
        } else {
            System.out.println("ERROR: second operand type not found");
            return -1;
        }

    }

    private String get_encode_type(byte upCode) {

        if ((upCode & 0b00000001) == 1) {
            return "literal";
        }
        return "regORmem";
    }

    private byte get_upcode_operand(byte upCode) {
        byte regCode = (byte) ((upCode & 0b00001110) >> 1);

        return _registers.get_register_value(regCode);
    }

    private String get_encode_type2(byte enCode) {
        if (Math.abs(enCode >> 7) == 1) {
            return "memory";
        }
        return "register";
    }

    private byte pop() {
        byte stack_pointer = _registers.get_register_value("SP");
        byte val = _RAM[stack_pointer];
        _registers.set_register_value("SP", stack_pointer + 1);
        return val;
    }

    private boolean push(byte val) {

        byte stack_pointer = _registers.get_register_value("SP");
        // print_stack();
        if (stack_pointer == _registers.get_register_value("SS")) {
            throw new Error("stack overflow");
        }
        _RAM[--stack_pointer] = val;

        _registers.set_register_value("SP", stack_pointer);
        return true;
    }

    public void dump_memory() {
        for (int i = 0; i < _RAM.length; ++i) {
            System.out
                    .println("addres: " + i + (i < _free_memory_index ? "  privat" : "  public") + "  val: " + _RAM[i]);
        }
    }

    public void print_stack() {
        byte sp = _registers.get_register_value("SP");
        byte ss = _registers.get_register_value("SS");
        System.out.println("stack");
        for (int i = ss; i <= sp; ++i) {
            System.out.println("addres: " + i + "  val: " + _RAM[i]);
        }
    }

    public void dump_free_memory() {
        for (int i = _free_memory_index; i < _RAM.length; ++i) {
            System.out
                    .println("addres: " + i + "  val: " + _RAM[i]);
        }
    }

    public void print_registers_value() {
        System.out.println();
        _registers.print_reg();
    }

    private byte[] _RAM;
    private byte _RAM_size = 64;
    private byte _segment = 1;
    private byte _free_memory_index;
    private Registers _registers;
    private InstrucionCodes _inst_codes;

}