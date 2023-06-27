package CPU;

import CPU.ParserInstruction.ParserInstruction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CPU {

    public CPU() {
        this._RAM = new byte[_RAM_size];
        this._registers = new byte[6];
        this._registersName = new String[] { "AYB", "BEN", "GIM", "ECH", "DA" };
        this._inst_codes = new InstrucionCodes();

    }

    public void execute_program(String fileMame) {

        read_and_parse_file(fileMame);
        while (execute_instruction()) {
        }
    }

    private void read_and_parse_file(String fileMame) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileMame));
            List<String> lines = reader.lines().toList();

            ParserInstruction parsedData = new ParserInstruction(_registersName, _RAM_size, lines, _inst_codes);

            // loader
            byte size = parsedData.load_instruction_to_RAM(_RAM);
            _free_memory_index = size;
            _GH = 0;

            // parsedData.print_binary_code();
            reader.close();

        } catch (IOException e) {
            throw new Error("!! File not found");
        }

    }

    private boolean execute_instruction() {
        if (_GH >= _free_memory_index) {
            System.out.println("Program over successfully");
            return false;
        }

        byte upCode = _RAM[_GH++];
        byte enCode = _RAM[_GH++];

        byte instCode = _inst_codes.get_instruction_code(upCode);

        switch (_inst_codes.get_instruction_type(instCode)) {
            case 0:
                exe_mov(upCode, enCode);
                return true;
            case 1:
            case 2:
                do_inst(instCode, upCode, enCode);
                return true;
            case 9:
                exe_jmps(upCode, enCode);
                return true;
        }

        System.out.println("ERROR: instruction binary conde not found!");
        return false;
    }

    private void exe_jmps(byte upCode, byte enCode) {
        byte memoryAddres = enCode;

        if (memoryAddres >= _free_memory_index) {
            throw new RuntimeException("!JMP: this memory isn't free");
        }

        switch (_inst_codes.get_jumps_key(upCode)) {
            case "JMP":
                _GH = memoryAddres;
                return;
            case "JE":
                if (_registers[4] == 0) {
                    _GH = memoryAddres;
                }
                return;
            case "JG":
                if (_registers[4] > 0) {
                    _GH = memoryAddres;
                }
                return;
            case "JL":
                if (_registers[4] < 0) {
                    _GH = memoryAddres;
                }
                return;
        }
    }

    private void do_inst(byte instCode, byte upCode, byte enCode) {

        byte regCode = (byte) ((upCode & 0b00001110) >> 1);
        byte op1 = get_upcode_operand(upCode);
        byte op2 = get_encode_operand(get_encode_type(upCode), enCode);
        // System.out.println("key " + key);
        // System.out.println("op1 " + op1);
        // System.out.println("op2 " + op2);

        switch (_inst_codes.get_instruction_key(instCode)) {
            case "ADD":
                _registers[regCode] = (byte) (op1 + op2);
                return;
            case "SUB":
                _registers[regCode] = (byte) (op1 - op2);
                return;
            case "DIV":
                _registers[regCode] = (byte) (op1 / op2);
                return;
            case "MUL":
                _registers[regCode] = (byte) (op1 * op2);
                return;
            case "AND":
                _registers[regCode] = (byte) (op1 & op2);
                return;
            case "OR":
                _registers[regCode] = (byte) (op1 | op2);
                return;
            case "XOR":
                _registers[regCode] = (byte) (op1 ^ op2);
                return;
            case "NOT":
                _registers[regCode] = (byte) ~op1;
                return;
            case "CMP":
                _registers[4] = (byte) (op1 - op2);
                return;
        }
    }

    private void exe_mov(byte upCode, byte enCode) {

        if (_inst_codes.get_instruction_code(upCode) == 0b1011) {
            byte regCode = (byte) ((upCode & 0b00001110) >> 1);
            _registers[regCode] = get_encode_operand(get_encode_type(upCode), enCode);

        } else {
            byte memoryCode = (byte) ((upCode & 0b00111110) >> 1);
            if (memoryCode < _free_memory_index) {
                throw new RuntimeException("this memory isn't free");
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
                return _registers[regCode];
            }
            if (type2 == "memory") { // memory
                byte memoryCode = (byte) (enCode & 0b00011111);

                if (memoryCode < _free_memory_index) {
                    throw new RuntimeException("this memory isn't free");
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

        return _registers[regCode];
    }

    private String get_encode_type2(byte enCode) {
        if (Math.abs(enCode >> 7) == 1) {
            return "memory";
        }
        return "register";
    }

    public void dump_memory() {
        for (int i = 0; i < _RAM.length; ++i) {
            System.out
                    .println("addres: " + i + (i < _free_memory_index ? "  privat" : "  public") + "  val: " + _RAM[i]);
        }
        System.out.println();
        print_reg();

    }

    public void print_reg() {
        for (int i = 0; i < _registersName.length; ++i) {
            System.out.println(_registersName[i] + " : val " + _registers[i]);
        }
    }

    private byte _GH; // instruction pointer
    private byte[] _RAM;
    private byte _RAM_size = 32;
    private byte _free_memory_index;
    private byte[] _registers;
    private String[] _registersName;
    private InstrucionCodes _inst_codes;

}