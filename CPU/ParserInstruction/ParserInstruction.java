package CPU.ParserInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CPU.InstrucionCodes;

public class ParserInstruction {
    private Map<String, Byte> _labelTable = new HashMap<String, Byte>();
    private short _lineIndex = 0;
    private short _memorySize;
    private ArrayList<Byte> _binaryCode = new ArrayList<Byte>();
    private byte[] _instCode = new byte[] { 0, 0 };
    private String[] _regsName;

    public ParserInstruction(String[] regsName, int ramSize, List<String> lines, InstrucionCodes codes) {

        this._memorySize = (short) ramSize;
        this._regsName = regsName;

        ArrayList<String> linesWithoutLabel = creat_label_table_ret_sub_lines(lines);

        // for (Map.Entry<String, Byte> entry : _labelTable.entrySet()) {
        // System.out.println(entry.getKey() + ": " + entry.getValue());
        // }

        for (String line : linesWithoutLabel) {
            if (!parser_instruction(line, codes)) {
                throw new Error("Dsfdsf");
            }
        }

    }

    public boolean parser_instruction(String line, InstrucionCodes codes) {
        this._lineIndex += 1;

        if (line.isEmpty()) {
            return true;
        }
        int instEnd = line.indexOf(" ");

        String instName = line.substring(0, instEnd).trim().toUpperCase();
        if (instName.isEmpty()) {
            return print_error("Syntax Error");
        }

        String operands = line.substring(instEnd, line.length()).trim();
        if (operands.isEmpty()) {
            return print_error("Syntax Error");
        }

        Byte[] instCode = codes.get_instruction_info(instName);
        if (instCode == null) {
            return print_error("Error instruction not found");
        }

        switch (instCode[0]) {
            case 0:
                return purce_mov(instCode[1], instCode[2], operands);
            case 2:
                return instruction_two_operands(instCode[1], operands);
            case 1:
                return instruction_one_operand(instCode[1], operands);
            case 9:
                return purce_jmps(instCode[1], instCode[2], operands);
            default:
                return print_error("Error instruction type not found");

        }

    }

    private boolean instruction_two_operands(int instCode, String operandStr) {
        String[] operands = operandStr.replaceAll(" ", "").split(",");
        if (operands.length != 2) {
            return print_error("Syntax Error");
        }
        // first operand
        if (!parse_first_Byte(operands[0], instCode)) {
            return false;
        }

        // second operand
        if (!parse_second_Byte(operands[1])) {
            return false;
        }

        // save binary data
        add_instCode_to_binaryCode();
        return true;
    }

    private boolean instruction_one_operand(int instCode, String op1) {
        op1 = op1.replaceAll(" ", "");

        if (!parse_first_Byte(op1, instCode)) {
            return false;
        }

        add_instCode_to_binaryCode();
        return true;

    }

    private boolean purce_mov(int instCode1, int instCode2, String operands) {

        String[] arr = operands.replaceAll(" ", "").split(",");
        if (arr.length != 2) {
            return print_error("Syntax Error: MOV must have 2 operands");
        }
        String op1 = arr[0], op2 = arr[1];

        // first argument
        if (is_register(op1)) {

            set_instruction(instCode1);
            set_upcode_register_ref(get_register_code(op1));

        } else if (is_memory_dereference(op1)) {

            set_instruction(instCode2);
            int num = memory_dereference(op1);
            if (num >= _memorySize) {
                return print_error("Error: number is larger than memory size");
            }
            set_upcode_reference(num);

        } else {
            return print_error("Error: first operand is invalid");
        }

        // second argument
        if (!parse_second_Byte(op2)) {
            return false;
        }

        // save binary data
        add_instCode_to_binaryCode();
        return true;
    }

    private boolean purce_jmps(int instCode, int instCode2, String op1) {
        Byte referense = _labelTable.get(op1);
        if (referense == null) {
            return print_error("Error: label not found");
        }
        set_instruction(instCode);
        set_upcode_label(referense, instCode2);

        add_instCode_to_binaryCode();
        return true;
    }

    private boolean parse_first_Byte(String op1, int instCode) {
        if (!is_register(op1)) {
            return print_error("Error: first argument most be register");
        }
        set_instruction(instCode);
        set_upcode_register_ref(get_register_code(op1));
        return true;
    }

    private boolean parse_second_Byte(String op2) {
        if (is_numeric(op2)) {

            Integer num = Integer.parseInt(op2);
            if (num > 255) {
                return print_error("Warning: value is greater than one byte");
            }
            set_encode_literal(num);

        } else if (is_register(op2)) {

            set_encode_register_ref(get_register_code(op2));

        } else if (is_memory_dereference(op2)) {

            int num = memory_dereference(op2);
            if (num >= _memorySize) {
                return print_error("Error: number is larger than memory size");
            }
            set_encode_reference(num);

        } else {
            return print_error("Error: second operand is invalid");
        }
        return true;
    }

    private Integer get_register_code(String rName) {
        for (int i = 0; i < _regsName.length; ++i) {
            if (_regsName[i].equals(rName.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }

    private boolean is_register(String name) {

        for (int i = 0; i < _regsName.length; ++i) {
            if (_regsName[i].equals(name.toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    private boolean print_error(String err) {
        System.out.println("line: " + _lineIndex + ": " + err);
        return false;
    }

    private boolean is_memory_dereference(String n) {
        n = n.replaceAll(" ", "");
        if (n.charAt(0) == '[' && n.charAt(n.length() - 1) == ']') {

            if (is_numeric(n.substring(1, n.length() - 1))) {
                return true;
            }
        }

        return false;
    }

    private Integer memory_dereference(String n) {
        return Integer.parseInt(n.substring(1, n.length() - 1));
    }

    private static boolean is_numeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void set_instruction(int code) {
        _instCode[0] = (byte) (_instCode[0] | (code << 4));
    }

    private void set_upcode_reference(int code) {
        _instCode[0] = (byte) (_instCode[0] | (code << 1));
    }

    private void set_upcode_register_ref(int code) {
        _instCode[0] = (byte) (_instCode[0] | (code << 1));
    }

    private void set_encode_reference(int code) {
        _instCode[1] = (byte) (_instCode[1] | (1 << 7));
        _instCode[1] = (byte) (_instCode[1] | code);
    }

    private void set_encode_register_ref(int code) {
        _instCode[1] = (byte) (_instCode[1] | code);
    }

    private void set_encode_literal(int literal) {
        _instCode[0] = (byte) (_instCode[0] | 1);
        _instCode[1] = (byte) literal;
    }

    private void set_upcode_label(int label, int instCode) {
        _instCode[0] = (byte) (_instCode[0] | (instCode << 2));
        _instCode[1] = (byte) (_instCode[1] | label);
    }

    private void add_instCode_to_binaryCode() {
        _binaryCode.add(_instCode[0]);
        _binaryCode.add(_instCode[1]);
        _instCode[0] = 0;
        _instCode[1] = 0;
    }

    private ArrayList<String> creat_label_table_ret_sub_lines(List<String> lines) {
        ArrayList<String> linesWithoutLabel = new ArrayList<String>();

        int lineIndex = 1, instructionAddres = 0;
        for (String line : lines) {

            if (line.contains(":")) {
                int labelIndex = line.indexOf(':');
                linesWithoutLabel.add(line.substring(labelIndex + 1, line.length()).trim());

                if (!line.isEmpty()) {
                    String label = line.substring(0, labelIndex).trim();
                    if (_labelTable.get(label) != null) {
                        throw new Error("line: " + lineIndex + " Error: that label already exists", null);
                    }
                    _labelTable.put(label, (byte) instructionAddres);
                }

            } else {
                linesWithoutLabel.add(line.trim());
            }

            if (!line.isEmpty()) {
                instructionAddres += 2;
            }
            lineIndex++;
        }
        return linesWithoutLabel;
    }

    public byte load_instruction_to_RAM(byte[] RAM) {
        byte size = 0;
        for (int i = 0; i < _binaryCode.size(); ++i) {
            RAM[i] = _binaryCode.get(i);
            size++;
        }
        return size;
    }

    public void print_binary_code() {
        for (int i = 0; i < _binaryCode.size(); ++i) {
            print_bites(_binaryCode.get(i));
            System.out.print(" ");
            if (i % 2 == 1) {
                System.out.print(" \n");
            }

        }
    }

    private static void print_bites(Byte b) {
        Integer k = (int) b;
        String ans = "";
        for (int i = 0; i < 8; ++i) {
            ans += (k >> i) & 1;

        }
        for (int i = ans.length() - 1; i >= 0; --i) {
            System.out.print(ans.charAt(i));
        }
    }

}
